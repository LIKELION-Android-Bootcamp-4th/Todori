package com.mukmuk.todori.ui.screen.todo.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoCategoryViewModel @Inject constructor(
    private val repository: TodoCategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoCategoryUiState())

    fun loadCategories(uid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, success = false)
            try {
                val categories = repository.getCategories(uid)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = categories,
                    error = null,
                    success = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                    success = false
                )
            }
        }
    }

    fun createCategory(
        uid: String,
        category: TodoCategory,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, success = false)
            try {
                repository.createCategory(uid, category)
                loadCategories(uid)
                _uiState.value = _uiState.value.copy(success = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                    success = false
                )
                onError(e)
            }
        }
    }

    fun updateCategory(
        uid: String,
        category: TodoCategory,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, success = false)
            try {
                repository.updateCategory(uid, category)
                loadCategories(uid)
                _uiState.value = _uiState.value.copy(success = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                    success = false
                )
                onError(e)
            }
        }
    }
}
