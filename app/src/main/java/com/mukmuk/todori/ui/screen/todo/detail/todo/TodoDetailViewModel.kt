package com.mukmuk.todori.ui.screen.todo.detail.todo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
//                val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
    fun toggleTodoCompleted(uid: String, todo: Todo) {
        viewModelScope.launch {
            val updated = todo.copy(isCompleted = !todo.isCompleted)
            try {
                todoRepository.updateTodo(uid, updated)
                // 성공 시 목록 다시 로딩 등 처리
                loadDetail(uid, todo.categoryId, todo.date)
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }


}
