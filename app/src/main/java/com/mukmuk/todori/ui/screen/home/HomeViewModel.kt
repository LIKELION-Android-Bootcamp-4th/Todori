package com.mukmuk.todori.ui.screen.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.data.local.datastore.RecordRepository
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.HomeRepository
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest // collectLatest import
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDate
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeSettingRepository: HomeSettingRepository,
    private val todoRepository: TodoRepository,
    private val homeRepository: HomeRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state

    private val _homeSettingState = MutableStateFlow(HomeSettingState())
    val homeSettingState: StateFlow<HomeSettingState> = _homeSettingState.asStateFlow()

    private val _todoList = MutableStateFlow<List<Todo>>(emptyList())
    val todoList: StateFlow<List<Todo>> = _todoList.asStateFlow()

    private val currentUid: String = "testuser"

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
            recordRepository.totalRecordTimeFlow.collectLatest { savedTime ->
                _state.update { it.copy(totalRecordTimeMills = savedTime) }
            }
        }

        loadTodosForHomeScreen(currentUid, currentDate)
        loadDailyRecordAndSetTotalTime(currentUid, currentDate)
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
                homeRepository.updateDailyRecord(currentUid, dailyRecord)
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
            recordRepository.saveTotalRecordTime(newRecordTime)

            val dailyRecord = DailyRecord(
                date = currentDate.toString(),
                studyTimeMillis = _state.value.totalStudyTimeMills
            )
            try {
                todoRepository.updateTodo(uid, updated)
                homeRepository.updateDailyRecord(currentUid, dailyRecord)
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
    fun loadTodosForHomeScreen(uid: String, date: LocalDate) {
        viewModelScope.launch {
            try {
                val todos = todoRepository.getTodosByDate(uid, date)
                _todoList.value = todos
            } catch (e: Exception) {
                _todoList.value = emptyList()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTodoCompleted(uid: String, todo: Todo) {
        viewModelScope.launch {
            val updated = todo.copy(completed = !todo.completed)
            try {
                todoRepository.updateTodo(uid, updated)
                loadTodosForHomeScreen(uid, LocalDate.parse(todo.date))
            } catch (e: Exception) {
                Log.e("todorilog", e.toString())
            }
        }
    }

    private fun loadDailyRecordAndSetTotalTime(uid: String, date: LocalDate) {
        viewModelScope.launch {
            try {
                val dailyRecords = homeRepository.getDailyRecord(uid, date)
                val totalTime = dailyRecords.firstOrNull()?.studyTimeMillis ?: 0L
                _state.update { it.copy(totalStudyTimeMills = totalTime) }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading daily record: ${e.message}")
            }
        }
    }
    fun startObservingTodos(uid: String) {
        val today = LocalDate.now()
        todoRepository.observeTodos(uid) { updatedTodos ->
            val filteredTodos = updatedTodos.filter { it.date == today.toString() }
            _todoList.value = filteredTodos
        }
    }
}