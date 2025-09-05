package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.goal.GoalTodo
import kotlinx.coroutines.tasks.await

class GoalTodoService(
    private val firestore: FirebaseFirestore
) {
    private fun userGoalTodosRef(uid: String) =
        firestore.collection("users").document(uid).collection("goalTodos")

    suspend fun createGoalTodo(uid: String, goalTodo: GoalTodo): GoalTodo {
        val ref = userGoalTodosRef(uid).document()
        val autoId = ref.id
        val todoWithId = goalTodo.copy(goalTodoId = autoId)
        ref.set(todoWithId, SetOptions.merge()).await()
        return todoWithId
    }

    suspend fun getGoalTodosByGoalId(uid: String, goalId: String): List<GoalTodo> {
        return userGoalTodosRef(uid)
            .whereEqualTo("goalId", goalId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(GoalTodo::class.java) }
    }


    suspend fun updateGoalTodo(uid: String, goalTodo: GoalTodo) {
        userGoalTodosRef(uid).document(goalTodo.goalTodoId)
            .set(goalTodo, SetOptions.merge()).await()
    }

    suspend fun deleteGoalTodo(uid: String, goalTodoId: String) {
        userGoalTodosRef(uid).document(goalTodoId).delete().await()
    }
}
