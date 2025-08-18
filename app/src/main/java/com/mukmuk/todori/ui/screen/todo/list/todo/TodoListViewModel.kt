package com.mukmuk.todori.ui.screen.todo.list.todo

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import androidx.core.net.toUri


@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val categoryRepository: TodoCategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TodoListState())
    val state: StateFlow<TodoListState> = _state.asStateFlow()


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTodoList(uid: String, selectedDate: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // 카테고리 불러오기
                val categories = categoryRepository.getCategories(uid)
                // 해당 날짜의 todo 불러오기
                val todos = todoRepository.getTodosByDate(uid, selectedDate)
                // 카테고리별로 todo 리스트 그룹핑
                val todoMap: Map<String, List<Todo>> = todos.groupBy { it.categoryId }
                _state.update {
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        todosByCategory = todoMap,
                        selectedDate = selectedDate
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
