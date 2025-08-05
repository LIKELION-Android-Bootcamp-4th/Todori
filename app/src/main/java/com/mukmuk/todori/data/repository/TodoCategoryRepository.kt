package com.mukmuk.todori.data.repository
import android.util.Log
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.data.service.TodoCategoryService
import javax.inject.Inject

class TodoCategoryRepository @Inject constructor(
    private val todoCategoryService: TodoCategoryService
) {
    // 카테고리 생성
    suspend fun createCategory(uid: String, category: TodoCategory) {
        Log.d("TodoCategoryService", "repository 실행됨")
        try {
            todoCategoryService.createCategory(uid, category)
            Log.d("TodoCategoryService", "service 호출 끝")
        } catch (e: Exception) {
            Log.e("TodoCategoryService", "service 호출 중 오류!", e)
        }
    }

    // 카테고리 목록 조회
    suspend fun getCategories(uid: String): List<TodoCategory> {
        return todoCategoryService.getCategories(uid)
    }

    // 카테고리 수정
    suspend fun updateCategory(uid: String, category: TodoCategory) {
        todoCategoryService.updateCategory(uid, category)
    }

    // 카테고리 삭제
    suspend fun deleteCategory(uid: String, categoryId: String) {
        todoCategoryService.deleteCategory(uid, categoryId)
    }
}
