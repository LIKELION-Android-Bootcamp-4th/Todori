package com.mukmuk.todori.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.todo.Todo
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TodoStatsService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodosByMonth(uid: String, year: Int, month: Int): List<Todo> {
        val start = LocalDate.of(year, month, 1)
        val end = start.plusMonths(1)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startDateStr = start.format(formatter)
        val endDateStr = end.format(formatter)

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