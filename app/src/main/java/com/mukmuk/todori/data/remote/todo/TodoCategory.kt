package com.mukmuk.todori.data.remote.todo

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoCategory(
    val categoryId: String = "",
    val uid: String = "",
    val name: String = "",
    val description: String? = null,
    val colorHex: String = "",
    val createdAt: Timestamp? = null
) : Parcelable