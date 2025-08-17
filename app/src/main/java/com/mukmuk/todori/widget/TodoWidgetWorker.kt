package com.mukmuk.todori.widget

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.dagger.assisted.Assisted
import com.google.firebase.functions.dagger.assisted.AssistedInject
import com.mukmuk.todori.util.WidgetUtil
import kotlinx.coroutines.tasks.await

@HiltWorker
class TodoWidgetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val currentUser = Firebase.auth.currentUser
            val uid = currentUser?.uid ?: return Result.failure()

            val db = Firebase.firestore
            val snapshot = db.collection("todos")
                .whereEqualTo("done", false)
                .get()
                .await()

            val todos = snapshot.documents.map { doc ->
                val title = doc.getString("title") ?: ""
                val completed = doc.getBoolean("completed") ?: false
                title to completed
            }

            WidgetUtil.loadWidgetTodos(uid)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<TodoWidgetWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}