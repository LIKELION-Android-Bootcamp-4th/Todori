package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.todo.Todo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TodoStatsService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getTodosByMonth(uid: String, year: Int, month: Int): List<Todo> {
        val startDateStr = "%04d-%02d-01".format(year, month)
        val nextMonth = if (month == 12) 1 else month + 1
        val nextYear = if (month == 12) year + 1 else year
        val endDateStr = "%04d-%02d-01".format(nextYear, nextMonth)

        val snapshot = firestore
            .collection("users")
            .document(uid)
            .collection("todos")
            .whereGreaterThanOrEqualTo("date", startDateStr)
            .whereLessThan("date", endDateStr)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Todo::class.java) }
    }
}
