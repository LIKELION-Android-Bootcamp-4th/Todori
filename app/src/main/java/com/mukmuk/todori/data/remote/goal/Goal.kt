package com.mukmuk.todori.data.remote.goal
import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Goal(
    val goalId: String = "",
    val uid: String = "",
    val title: String = "",
    val description: String? = null,
    val color: String? = null,
    val isPinned: Boolean = false,
    val startDate: String = "",
    val endDate: String = "",
    val completed: Boolean = false,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) : Parcelable