package com.mukmuk.todori.ui.screen.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.HomeRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingState
import com.mukmuk.todori.widget.timer.TimerService
import com.mukmuk.todori.widget.timer.TimerWidget
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
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private var todosJob: Job? = null
    private var dailyRecordJob: Job? = null
    private val authUserFlow = MutableStateFlow(Firebase.auth.currentUser?.uid)

    init {
        viewModelScope.launch {
            homeSettingRepository.homeSettingStateFlow.collectLatest { settings ->
                currentSettings = settings
                _homeSettingState.value = settings
                updateInitialTimerSettings(settings)
            }
        }

        viewModelScope.launch {
            val running = recordSettingRepository.runningStateFlow.first()
            val totalTime = recordSettingRepository.totalRecordTimeFlow.first()

            _state.update {
                it.copy(
                    status = if (running) TimerStatus.RUNNING else TimerStatus.IDLE,
                    totalStudyTimeMills = totalTime
                )
            }
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(TimerWidget::class.java)
            glanceIds.forEach { id ->
                updateAppWidgetState(context, id) { prefs ->
                    prefs[TimerWidget.RUNNING_STATE_PREF_KEY] = running
                }
                TimerWidget().update(context, id)
            }

            if (running && timerJob?.isActive != true) {
                startTimer()
            } else if (!running) {
                val intent = Intent(context, TimerService::class.java)
                context.stopService(intent)
            }
        }
        viewModelScope.launch {
            recordSettingRepository.runningStateFlow
                .distinctUntilChanged()
                .collectLatest { running ->
                    if (running) startTimer() else stopTimer(false)
                    _state.update { it.copy(status = if (running) TimerStatus.RUNNING else TimerStatus.IDLE) }
                }
        }
        viewModelScope.launch {
            Firebase.auth.addAuthStateListener { firebaseAuth ->
                authUserFlow.value = firebaseAuth.currentUser?.uid
            }
        }
        viewModelScope.launch {
            authUserFlow.collectLatest { uid ->
                if (uid != null) {
                    loadProfile(uid)
                    startObservingTodos(uid)
                    startObservingDailyRecord(uid)
                } else {
                    todosJob?.cancel()
                    _todoList.value = emptyList()
                    _state.update {
                        it.copy(
                            uid = "",
                            status = TimerStatus.IDLE,
                            timeLeftInMillis = (currentSettings.focusMinutes * 60 + currentSettings.focusSeconds) * 1000L,
                            pomodoroMode = PomodoroTimerMode.FOCUSED,
                            completedFocusCycles = 0,
                            totalStudyTimeMills = 0L,
                            totalRecordTimeMills = 0L
                        )
                    }
                    val stopIntent = Intent(context, TimerService::class.java)
                    context.stopService(stopIntent)

                    val totalTimeIntent = Intent(context, TotalTimeWidgetBroadcastReceiver::class.java).apply {
                        action = ACTION_UPDATE_TOTAL_TIME_WIDGET
                        putExtra(EXTRA_TOTAL_TIME_MILLIS, 0L)
                    }
                    context.sendBroadcast(totalTimeIntent)

                    val timerIntent = Intent(context, TimerService::class.java).apply {
                        action = TimerService.ACTION_RESET
                    }
                    context.startService(timerIntent)
                }
            }
        }
    }



    fun onEvent(event: TimerEvent) {
        when (event) {
            is TimerEvent.Start -> startTimer()
            is TimerEvent.Resume -> resumeTimer()
            is TimerEvent.Stop -> stopTimer(false)
            is TimerEvent.Reset -> resetTimer()
            is TimerEvent.Record -> enterRecordMode()
        }
    }

    private fun startTimer() {
        if (timerJob?.isActive == true || _state.value.status == TimerStatus.RUNNING) {
            return
        }
        timerJob?.cancel()
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

    private fun stopTimer(isRecordMode: Boolean) {
        timerJob?.cancel()
        timerJob = null
        _state.update { it.copy(status = TimerStatus.IDLE) }

        val uid = _state.value.uid
        val totalTime = _state.value.totalStudyTimeMills
        saveRecord(uid, totalTime, isRecordMode)
        viewModelScope.launch {
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(TimerWidget::class.java)
            glanceIds.forEach { id ->
                updateAppWidgetState(context, id) { prefs ->
                    prefs[TimerWidget.RUNNING_STATE_PREF_KEY] = false
                }
                TimerWidget().update(context, id)
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

    private fun saveRecord(uid: String, recordTime: Long, isRecordMode: Boolean) {
        viewModelScope.launch {
            if (uid.isEmpty()) {
                return@launch
            }
            try {

                val today = currentDate.toString()
                val currentHour = java.time.LocalTime.now().hour.toString().padStart(2, '0')

                val existingRecords = homeRepository.getDailyRecord(uid, currentDate)
                val existing = existingRecords.firstOrNull()

                val updatedHourly = existing?.hourlyMinutes?.toMutableMap() ?: mutableMapOf()
                updatedHourly[currentHour] = (updatedHourly[currentHour] ?: 0) + recordTime

                val data = mutableMapOf<String, Any>(
                    "date" to currentDate.toString(),
                    "studyTimeMillis" to recordTime,
                    "hourlyMinutes" to updatedHourly
                )

                // 기록 모드일 때만 recordTimeMillis 추가
                if (isRecordMode) {
                    data["recordTimeMillis"] = recordTime
                }
                homeRepository.updateDailyRecord(uid, data)
                recordSettingRepository.saveTotalRecordTime(recordTime)
                recordSettingRepository.saveRunningState(false)

                val intent = Intent(context, TimerService::class.java)
                context.stopService(intent)
                val broadcastIntent =
                    Intent(context, TotalTimeWidgetBroadcastReceiver::class.java).apply {
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
            stopTimer(true)
            val updatedTodo =
                todo.copy(totalFocusTimeMillis = todo.totalFocusTimeMillis + recordTime)
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
        todosJob?.cancel()
        todosJob = viewModelScope.launch {
            todoRepository.observeTodos(uid).collectLatest { updatedTodos ->
                val filteredTodos = updatedTodos.filter { it.date == today.toString() }
                _todoList.value = filteredTodos.sortedBy { it.completed }
            }
        }
    }
    private fun loadDailyRecord(uid: String) {
        val today = LocalDate.now()
        viewModelScope.launch {
            try {
                val records = homeRepository.getDailyRecord(uid, today)
                val totalTime = records.firstOrNull()?.studyTimeMillis ?: 0L
                _state.update {
                    it.copy(
                        totalStudyTimeMills = totalTime,
                        uid = uid
                    )
                }
            } catch (e: Exception) {
                Log.e("todorilog", "Failed to load daily record: ${e.message}", e)
            }
        }
    }
    private fun startObservingDailyRecord(uid: String) {
        val today = LocalDate.now().toString()
        dailyRecordJob?.cancel()
        dailyRecordJob = viewModelScope.launch {
            homeRepository.observeDailyRecord(uid).collectLatest { updatedRecords ->
                val totalTime = updatedRecords.firstOrNull { it.date == today }?.studyTimeMillis ?: 0L
                val recordTime = updatedRecords.firstOrNull { it.date == today }?.recordTimeMillis ?: 0L
                _state.update {
                    it.copy(
                        totalStudyTimeMills = totalTime,
                        totalRecordTimeMills = recordTime,
                        uid = uid // uid 업데이트
                    )
                }
            }
        }
    }
}
