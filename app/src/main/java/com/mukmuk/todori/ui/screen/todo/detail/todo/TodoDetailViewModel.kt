package com.mukmuk.todori.ui.screen.todo.detail.todo

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.mukmuk.todori.data.local.datastore.TodayTodoRepository
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.widget.todos.TodoWidget
import com.mukmuk.todori.widget.todos.TodoWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val categoryRepository: TodoCategoryRepository,
    private val todayTodoRepository: TodayTodoRepository,

    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(TodoDetailState())
    val state: StateFlow<TodoDetailState> = _state

    // todo detail 로드 (category + todos)
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDetail(uid: String, categoryId: String, date: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val category = categoryRepository.getCategoryById(uid, categoryId)
                val todos = todoRepository.getTodosByCategoryAndDate(uid, categoryId, date)
                _state.value = _state.value.copy(
                    isLoading = false,
                    category = category,
                    todos = todos,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    //todo 생성
    @RequiresApi(Build.VERSION_CODES.O)
    fun addTodo(uid: String, categoryId: String, date: String, title: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val newTodo = Todo(
                    todoId = "",
                    uid = uid,
                    title = title,
                    categoryId = categoryId,
                    date = date,
                    completed = false,
                    createdAt = Timestamp.now()
                )
                todoRepository.createTodo(uid, newTodo)
                loadDetail(uid, categoryId, date)
                onResult(true)

                val todayTodos = todoRepository.getTodosByDate(uid, LocalDate.now())
                if (todayTodos.isNotEmpty()) {
                    todayTodoRepository.saveTodayTodos(todayTodos)
                    updateTodoWidget(todayTodos)
                }
            } catch (e: Exception) {
                onResult(false)
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }


    //확인 update
    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTodoCompleted(uid: String, todo: Todo) {
        viewModelScope.launch {
            val updated = todo.copy(completed = !todo.completed)
            try {
                todoRepository.updateTodo(uid, updated)
                // 성공 시 목록 다시 로딩 등 처리
                loadDetail(uid, todo.categoryId, todo.date)

                val todayTodos = todoRepository.getTodosByDate(uid, LocalDate.now())
                if (todayTodos.isNotEmpty()) {
                    todayTodoRepository.saveTodayTodos(todayTodos)
                    updateTodoWidget(todayTodos)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deletedTodo(uid: String, todoId: String, categoryId: String, date: String) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(uid, todoId)
                loadDetail(uid, categoryId, date)

                val todayTodos = todoRepository.getTodosByDate(uid, LocalDate.now())
                if (todayTodos.isNotEmpty()) {
                    todayTodoRepository.saveTodayTodos(todayTodos)
                    updateTodoWidget(todayTodos)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    //category 삭제 및 해당 관련 Todo 삭제
    fun deleteCategoryWithTodos(uid: String, categoryId: String) {
        viewModelScope.launch {
            try {
                val todos = todoRepository.getTodosByCategory(uid, categoryId)
                todos.forEach { todo ->
                    todoRepository.deleteTodo(uid, todo.todoId)

                    val todayTodos = todoRepository.getTodosByDate(uid, LocalDate.now())
                    if (todayTodos.isNotEmpty()) {
                        todayTodoRepository.saveTodayTodos(todayTodos)
                        updateTodoWidget(todayTodos)
                    }
                }
                categoryRepository.deleteCategory(uid, categoryId)
                _state.value = _state.value.copy(categoryDeleted = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
                _state.value = _state.value.copy(categoryDeleted = false)
            }
        }
    }

    //뒤로 가기 후 categoryDeleted 상태 초기화
    fun resetCategoryDeleted() {
        _state.value = _state.value.copy(categoryDeleted = false)
    }

    // 위젯 업데이트
    fun updateTodoWidget(todos: List<Todo>){
        val intent = Intent(context, TodoWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        context.sendBroadcast(intent)
    }
}
