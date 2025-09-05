package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.service.TodoCategoryService
import javax.inject.Inject

class TodoCategoryRepository @Inject constructor(
    private val todoCategoryService: TodoCategoryService
) {
    suspend fun createCategory(uid: String, category: TodoCategory): String {
        var categoryId = ""
        try {
            categoryId = todoCategoryService.createCategory(uid, category)
        } catch (e: Exception) {
        }

        return categoryId
    }

    suspend fun getUserById(uid: String): User? {
        return todoCategoryService.getUserById(uid)
    }

    suspend fun createSendTodoCategory(
        uid: List<String> = emptyList(),
        categoryId: String
    ): String {
        return todoCategoryService.createSendTodoCategory(uid, categoryId)
    }

    suspend fun getCategoryById(uid: String, categoryId: String): TodoCategory? {
        return todoCategoryService.getCategoryById(uid, categoryId)
    }

    suspend fun getCategoryByData(categoryId: String): TodoCategory? {
        return todoCategoryService.getCategoryByData(categoryId)
    }

    suspend fun getCategories(uid: String): List<TodoCategory> {
        return todoCategoryService.getCategories(uid)
    }

    suspend fun getSendCategory(uid: String, sendCategoryId: String): TodoCategory? {
        return todoCategoryService.getSendCategory(uid, sendCategoryId)
    }

    suspend fun getSendCategories(uid: String): List<TodoCategory> {
        return todoCategoryService.getSendCategories(uid)
    }

    suspend fun deleteSendCategory(uid: String, sendCategoryId: String) {
        todoCategoryService.deleteSendCategory(uid, sendCategoryId)
    }

    suspend fun updateCategory(uid: String, category: TodoCategory) {
        todoCategoryService.updateCategory(uid, category)
    }

    suspend fun deleteCategory(uid: String, categoryId: String) {
        todoCategoryService.deleteCategory(uid, categoryId)
    }
}
