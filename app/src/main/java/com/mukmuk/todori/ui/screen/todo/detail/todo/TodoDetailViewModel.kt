package com.mukmuk.todori.ui.screen.todo.detail.todo

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.local.datastore.TodayTodoRepository
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.repository.TodoRepository
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSendTodoDetail(categoryId: String, date: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val category = categoryRepository.getCategoryByData(categoryId)

                if(category != null) {
                    val todos =
                        todoRepository.getTodosByCategoryAndDate(category.uid, categoryId, date)
                    getUserById(category.uid)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        category = category,
                        todos = todos,
                        error = null
                    )
                }
            }
            catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }

    }

    fun getUserById(uid: String) {
        viewModelScope.launch {
            try {
                val user = categoryRepository.getUserById(uid)
                _state.value = _state.value.copy(userName = user?.nickname ?: "")
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

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
                    updateTodoWidget()
                }
            } catch (e: Exception) {
                onResult(false)
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleTodoCompleted(uid: String, todo: Todo) {
        viewModelScope.launch {
            val updated = todo.copy(completed = !todo.completed)
            try {
                todoRepository.updateTodo(uid, updated)
                loadDetail(uid, todo.categoryId, todo.date)

                val todayTodos = todoRepository.getTodosByDate(uid, LocalDate.now())
                if (todayTodos.isNotEmpty()) {
                    todayTodoRepository.saveTodayTodos(todayTodos)
                    updateTodoWidget()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteSendCategory(uid: String, sendCategoryId: String) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteSendCategory(uid, sendCategoryId)
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
                    updateTodoWidget()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteCategoryWithTodos(uid: String, categoryId: String) {
        viewModelScope.launch {
            try {
                val todos = todoRepository.getTodosByCategory(uid, categoryId)
                todos.forEach { todo ->
                    todoRepository.deleteTodo(uid, todo.todoId)

                    val todayTodos = todoRepository.getTodosByDate(uid, LocalDate.now())
                    if (todayTodos.isNotEmpty()) {
                        todayTodoRepository.saveTodayTodos(todayTodos)
                        updateTodoWidget()
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

    fun resetCategoryDeleted() {
        _state.value = _state.value.copy(categoryDeleted = false)
    }

    fun updateTodoWidget(){
        val intent = Intent(context, TodoWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        context.sendBroadcast(intent)
    }
}
