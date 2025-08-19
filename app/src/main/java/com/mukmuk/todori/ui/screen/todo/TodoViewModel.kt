package com.mukmuk.todori.ui.screen.todo

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.data.repository.DailyRecordRepository
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.util.toJavaLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val categoryRepo: TodoCategoryRepository,
    private val dailyRepo: DailyRecordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state

    private val _selectedDate =
        MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _studyRecordsMillis = MutableStateFlow<Map<LocalDate, Long>>(emptyMap())
    val studyRecordsMillis: StateFlow<Map<LocalDate, Long>> = _studyRecordsMillis

    private val weekCache =
        mutableMapOf<LocalDate, Map<LocalDate, Long>>()

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun onWeekVisible(
        uid: String,
        start: LocalDate,
        end: LocalDate
    ) {
        val key = start
        weekCache[key]?.let {
            _studyRecordsMillis.value = it
            return
        }
        viewModelScope.launch {
            val list =
                dailyRepo.getRecordsByWeek(uid, start.toJavaLocalDate(), end.toJavaLocalDate())

            val map = buildMap<LocalDate, Long> {
                list.forEach { rec ->
                    val d = LocalDate.parse(rec.date)
                    put(d, rec.studyTimeMillis)
                }
            }
            weekCache[key] = map
            _studyRecordsMillis.value = map

            prefetchNeighbors(uid, start)
        }
    }

    fun getTodoCategories(uid: String) {
        viewModelScope.launch {
            try {
                val categories = categoryRepo.getCategories(uid)
                _state.update {
                    it.copy(
                        categories = categories,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun sendTodoCategory(category: TodoCategory) {
        viewModelScope.launch {
            try {
                val updatedCategory = category.copy(
                    uid = "",
                    categoryId = "",
                    createdAt = Timestamp.now()
                )

                val categoryId = categoryRepo.createSendTodoCategory(updatedCategory)
                val url = createUrl(categoryId)
                _state.update {
                    it.copy(
                        sendUrl = url
                    )
                }
            }
            catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearSendTodoCategory() {
        _state.update {
            it.copy(
                sendUrl = null
            )
        }
    }

    private fun createUrl(categoryId: String): String {
        return Uri.Builder()
            .scheme("https")
            .authority("todori-7d791.web.app")
            .appendPath("category")
            .appendQueryParameter("categoryId", categoryId)
            .build()
            .toString()
    }

    fun addTodoCategoryFromUrl(uid: String?, categoryId: String?) {
        viewModelScope.launch {
            try {
                val uid = uid ?: return@launch
                if (categoryId != null) {
                    val category = categoryRepo.getSendCategory(categoryId)

                    if(category != null) {
                        val updatedCategory = category.copy(
                            uid = uid,
                            categoryId = "",
                            createdAt = Timestamp.now()
                        )


                        categoryRepo.createCategory(uid, updatedCategory)


                    }
                }
            }
            catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prefetchNeighbors(uid: String, thisWeekStart: LocalDate) {
        val prev = thisWeekStart.minus(7, DateTimeUnit.DAY)
        val next = thisWeekStart.plus(7, DateTimeUnit.DAY)

        listOf(prev, next).forEach { s ->
            if (!weekCache.containsKey(s)) {
                viewModelScope.launch {
                    val e = s.plus(6, DateTimeUnit.DAY)
                    val list =
                        dailyRepo.getRecordsByWeek(uid, s.toJavaLocalDate(), e.toJavaLocalDate())
                    val map =
                        list.associate { LocalDate.parse(it.date) to it.studyTimeMillis }
                    weekCache[s] = map
                }
            }
        }
    }
}
