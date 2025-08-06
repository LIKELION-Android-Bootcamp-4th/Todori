package com.mukmuk.todori.ui.screen.todo.detail.todo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val categoryRepository: TodoCategoryRepository
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
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
