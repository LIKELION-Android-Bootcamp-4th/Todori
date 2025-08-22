package com.mukmuk.todori.ui.screen.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.HomeRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import com.mukmuk.todori.widget.UpdateWidgetWorker
import com.mukmuk.todori.widget.timer.TimerAction
import com.mukmuk.todori.widget.timer.TimerService
import com.mukmuk.todori.widget.timer.TimerWidget
import com.mukmuk.todori.widget.timer.TimerWidget.Companion.TOGGLE_KEY
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget.Companion.ACTION_UPDATE_TOTAL_TIME_WIDGET
import com.mukmuk.todori.widget.totaltime.TotalTimeWidget.Companion.EXTRA_TOTAL_TIME_MILLIS
import com.mukmuk.todori.widget.totaltime.TotalTimeWidgetBroadcastReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    @ApplicationContext private val context: Context
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
            homeSettingRepository.homeSettingStateFlow.collectLatest { settings ->
                currentSettings = settings
                _homeSettingState.value = settings
                updateInitialTimerSettings(settings)
            }
        }

        viewModelScope.launch {
            recordSettingRepository.totalRecordTimeFlow.collectLatest { savedTime ->
                _state.update { it.copy(totalStudyTimeMills = savedTime) }
            }
        }

        viewModelScope.launch {
            recordSettingRepository.runningStateFlow.collectLatest { running ->
                if (running) {
                    startTimer()
                } else {
                    stopTimer()
                }

                _state.update {
                    it.copy(
                        status = if (running) TimerStatus.RUNNING else TimerStatus.IDLE
                    )
                }
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

        val intent = Intent(context, TimerService::class.java)
        context.startForegroundService(intent)

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (_state.value.timeLeftInMillis > 0) {
                delay(1000)

                _state.update { current ->
                    val newTotalStudy =
                        if (current.pomodoroMode == PomodoroTimerMode.FOCUSED) {
                            current.totalStudyTimeMills + 1000
                        } else current.totalStudyTimeMills

                    val updated = current.copy(
                        timeLeftInMillis = current.timeLeftInMillis - 1000,
                        totalStudyTimeMills = newTotalStudy
                    )

                    recordSettingRepository.saveTotalRecordTime(updated.totalStudyTimeMills)
                    updated
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

        val uid = _state.value.uid
        val totalTime = _state.value.totalStudyTimeMills
        saveRecord(uid, totalTime)
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

    private fun saveRecord(uid: String, recordTime: Long) {
        viewModelScope.launch {
            if (uid.isEmpty()) {
                Log.e("todorilog", "기록 저장 실패: UID가 유효하지 않습니다.")
                return@launch
            }
            try {
                val dailyRecord = DailyRecord(
                    date = currentDate.toString(),
                    studyTimeMillis = recordTime
                )
                homeRepository.updateDailyRecord(uid, dailyRecord)

                recordSettingRepository.saveTotalRecordTime(recordTime)
                recordSettingRepository.saveRunningState(false)

                val intent = Intent(context, TimerService::class.java)
                context.stopService(intent)
                val broadcastIntent = Intent(context, TotalTimeWidgetBroadcastReceiver::class.java).apply {
                    action = ACTION_UPDATE_TOTAL_TIME_WIDGET
                    putExtra(EXTRA_TOTAL_TIME_MILLIS, recordTime)
                }
                context.sendBroadcast(broadcastIntent)
            } catch (e: Exception) {
                Log.e("todorilog", "기록 저장 및 위젯 업데이트 중 오류 발생: ${e.message}", e)
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

    fun setTodoRecordTimeMills(recordTime: Long, uid: String, todo: Todo) {
        viewModelScope.launch {
            val updatedTodo = todo.copy(totalFocusTimeMillis = todo.totalFocusTimeMillis + recordTime)
            try {
                todoRepository.updateTodo(uid, updatedTodo)
            } catch (e: Exception) {
                Log.e("todorilog", "Todo 업데이트 실패: ${e.message}", e)
            }
        }
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
            val totalTime =
                updatedRecords.firstOrNull { it.date == today }?.studyTimeMillis ?: 0L
            _state.update { it.copy(totalStudyTimeMills = totalTime) }
        }
    }
}
