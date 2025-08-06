package com.mukmuk.todori.ui.screen.todo.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoCategoryViewModel @Inject constructor(
    private val repository: TodoCategoryRepository
) : ViewModel() {

    // 카테고리 리스트 상태
    private val _categories = MutableStateFlow<List<TodoCategory>>(emptyList())
    val categories: StateFlow<List<TodoCategory>> = _categories

    // 카테고리 조회
    fun loadCategories(uid: String) {
        viewModelScope.launch {
            _categories.value = repository.getCategories(uid)
            Log.d("TodoCategoryService", "${categories.value}")
        }
    }

    // 카테고리 생성
    fun createCategory(uid: String, category: TodoCategory, onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                loadCategories(uid)
                repository.createCategory(uid, category)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }


    // 카테고리 수정
    fun updateCategory(uid: String, category: TodoCategory, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateCategory(uid, category)
            loadCategories(uid)
            onSuccess()
        }
    }

    // 카테고리 삭제
    fun deleteCategory(uid: String, categoryId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteCategory(uid, categoryId)
            loadCategories(uid)
            onSuccess()
        }
    }
}
