package com.mukmuk.todori.widget

import android.content.Context
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.util.WidgetUtil
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import android.os.Build
import androidx.glance.appwidget.updateAll

class WidgetViewModel @Inject constructor(
    private val todoRepository: TodoRepository
): ViewModel(){
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTodosForWidget(context: Context, uid: String) {
        viewModelScope.launch {
            try {
                val todos = todoRepository.getTodosByDate(uid, LocalDate.now())
                val widgetTodos = todos.map { it.title to it.completed }

                WidgetUtil.loadWidgetTodos("Ek8NgW6xi1fGTb0kXqHowxUweqG3")
                TodoWidget.updateAll(context)

            } catch (e: Exception) {
                Log.e("WidgetViewModel", "투두 로드 실패: ${e.message}")
            }
        }
    }

}