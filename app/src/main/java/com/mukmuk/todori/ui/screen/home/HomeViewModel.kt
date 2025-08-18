package com.mukmuk.todori.ui.screen.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.HomeRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.data.service.AuthService
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeSettingRepository: HomeSettingRepository,
    private val todoRepository: TodoRepository,
    private val homeRepository: HomeRepository,
    private val recordSettingRepository: RecordSettingRepository,
    private val repository: UserRepository,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state

    private val _homeSettingState = MutableStateFlow(HomeSettingState())
    val homeSettingState: StateFlow<HomeSettingState> = _homeSettingState.asStateFlow()

    private val _todoList = MutableStateFlow<List<Todo>>(emptyList())
    val todoList: StateFlow<List<Todo>> = _todoList.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDate = LocalDate.now()

    private var timerJob: Job? = null
    private var currentSettings: HomeSettingState = HomeSettingState()

    init {
        viewModelScope.launch {
            val initialLoadedSettings = homeSettingRepository.homeSettingStateFlow.first()
            currentSettings = initialLoadedSettings
            _homeSettingState.value = initialLoadedSettings
            updateInitialTimerSettings(initialLoadedSettings)

            homeSettingRepository.homeSettingStateFlow.collectLatest { settings ->
                currentSettings = settings
                _homeSettingState.value = settings
            }
        }

        viewModelScope.launch {
            recordSettingRepository.totalRecordTimeFlow.collectLatest { savedTime ->
                _state.update { it.copy(totalRecordTimeMills = savedTime) }
            }
        }
    }

    private var hasLoaded = false

    fun observeAuthAndLoadData() {
        val auth = Firebase.auth
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null && !hasLoaded) {
                hasLoaded = true
                Log.d("todorilog", "호출")
                loadProfile(user.uid)
                startObservingTodos(user.uid)
                startObservingDailyRecord(user.uid)
            }
        }
    }

    fun onEvent(event: TimerEvent) {
        when (event) {
            is TimerEvent.Start -> startTimer()
            is TimerEvent.Resume -> resumeTimer()
            is TimerEvent.Stop -> stopTimer()
            is TimerEvent.Reset -> resetTimer()
            is TimerEvent.Record -> enterRecordMode()
        }
    }

    private fun startTimer() {
        if (_state.value.status == TimerStatus.RUNNING) return

        _state.update { it.copy(status = TimerStatus.RUNNING) }

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftInMillis > 0) {
                delay(1000)
                if (_state.value.pomodoroMode == PomodoroTimerMode.FOCUSED) {
                    _state.update {
                        it.copy(
                            timeLeftInMillis = it.timeLeftInMillis - 1000,
                            totalStudyTimeMills = it.totalStudyTimeMills + 1000
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            timeLeftInMillis = it.timeLeftInMillis - 1000,
                        )
                    }
                }
            }
            handleTimerCompletion()
        }
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(status = TimerStatus.IDLE) }

        viewModelScope.launch {
            val dailyRecord = DailyRecord(
                date = currentDate.toString(),
                studyTimeMillis = _state.value.totalStudyTimeMills
            )
            try {
                homeRepository.updateDailyRecord(_state.value.uid, dailyRecord)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error updating daily record: ${e.message}")
            }
        }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                completedFocusCycles = 0,
                status = TimerStatus.IDLE,
                pomodoroMode = PomodoroTimerMode.FOCUSED
            )
        }
    }

    private fun enterRecordMode() {
        timerJob?.cancel()
        _state.update { it.copy(status = TimerStatus.RECORDING) }
    }

    fun setTotalRecordTimeMills(recordTime: Long, uid: String, todo: Todo) {
        viewModelScope.launch {
            val updated = if (todo.totalFocusTimeMillis > 0L) {
                todo.copy(totalFocusTimeMillis = todo.totalFocusTimeMillis + recordTime)
            } else {
                todo.copy(totalFocusTimeMillis = recordTime)
            }

            val newRecordTime = _state.value.totalStudyTimeMills
            _state.update { it.copy(totalRecordTimeMills = newRecordTime) }
            recordSettingRepository.saveTotalRecordTime(newRecordTime)

            val currentHour = java.time.LocalTime.now().hour.toString().padStart(2, '0')
            val millis = recordTime

            val existingRecords = homeRepository.getDailyRecord(uid, currentDate)
            val existing = existingRecords.firstOrNull()

            val updatedHourly = existing?.hourlyMinutes?.toMutableMap() ?: mutableMapOf()
            updatedHourly[currentHour] = (updatedHourly[currentHour] ?: 0) + millis

            val dailyRecord = DailyRecord(
                date = currentDate.toString(),
                studyTimeMillis = newRecordTime,
                hourlyMinutes = updatedHourly,
            )
            try {
                todoRepository.updateTodo(uid, updated)
                homeRepository.updateDailyRecord(_state.value.uid, dailyRecord)
            } catch (e: Exception) {
                Log.e("todorilog", e.toString())
            }
        }
    }

    private fun updateInitialTimerSettings(settings: HomeSettingState) {
        _state.update {
            it.copy(
                timeLeftInMillis = (settings.focusMinutes * 60 + settings.focusSeconds) * 1000L,
                pomodoroMode = PomodoroTimerMode.FOCUSED,
                completedFocusCycles = 0,
                isPomodoroEnabled = settings.isPomodoroEnabled,
            )
        }
    }

    private fun handleTimerCompletion() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                status = TimerStatus.IDLE,
            )
        }
        if (currentSettings.isPomodoroEnabled) {
            when (_state.value.pomodoroMode) {
                PomodoroTimerMode.FOCUSED -> {
                    val nextCompletedCycles = _state.value.completedFocusCycles + 1

                    if (nextCompletedCycles % 4 == 0) {
                        _state.update { currentState ->
                            currentState.copy(
                                pomodoroMode = PomodoroTimerMode.LONG_RESTED,
                                timeLeftInMillis = (currentSettings.longRestMinutes * 60 + currentSettings.longRestSeconds) * 1000L,
                                completedFocusCycles = nextCompletedCycles,
                            )
                        }
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                pomodoroMode = PomodoroTimerMode.SHORT_RESTED,
                                timeLeftInMillis = (currentSettings.shortRestMinutes * 60 + currentSettings.shortRestSeconds) * 1000L,
                                completedFocusCycles = nextCompletedCycles,
                            )
                        }
                    }
                }

                PomodoroTimerMode.SHORT_RESTED -> {
                    _state.update { currentState ->
                        currentState.copy(
                            pomodoroMode = PomodoroTimerMode.FOCUSED,
                            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                        )
                    }
                }

                PomodoroTimerMode.LONG_RESTED -> {
                    _state.update { currentState ->
                        currentState.copy(
                            pomodoroMode = PomodoroTimerMode.FOCUSED,
                            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                            completedFocusCycles = 0
                        )
                    }
                }
            }
        } else {
            _state.update { currentState ->
                currentState.copy(
                    pomodoroMode = PomodoroTimerMode.FOCUSED,
                    timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                    completedFocusCycles = 0
                )
            }
        }
        startTimer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTodoCompleted(uid: String, todo: Todo) {
        viewModelScope.launch {
            val updated = todo.copy(completed = !todo.completed)
            try {
                todoRepository.updateTodo(uid, updated)
            } catch (e: Exception) {
                Log.e("todorilog", e.toString())
            }
        }
    }

    private fun loadProfile(uid: String) {
        viewModelScope.launch {
            runCatching { repository.getProfile(uid) }
                .onSuccess {
                    _state.value = _state.value.copy(uid = uid)
                }
                .onFailure { e ->
                    Log.e("todorilog", "${e.message}")
                }
        }
    }

    private fun startObservingTodos(uid: String) {
        val today = LocalDate.now()
        todoRepository.observeTodos(uid) { updatedTodos ->
            val filteredTodos = updatedTodos.filter { it.date == today.toString() }
            _todoList.value = filteredTodos.sortedBy { it.completed }
        }
    }

    private fun startObservingDailyRecord(uid: String) {
        val today = LocalDate.now().toString()
        homeRepository.observeDailyRecord(uid) { updatedRecords ->
            val totalTime = updatedRecords.firstOrNull { it.date == today }?.studyTimeMillis ?: 0L
            _state.update { it.copy(totalStudyTimeMills = totalTime) }
        }
    }
}