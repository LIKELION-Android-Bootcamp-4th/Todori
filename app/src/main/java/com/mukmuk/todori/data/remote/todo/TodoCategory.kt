package com.mukmuk.todori.data.remote.todo

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoCategory(
    val categoryId: String = "",           // 카테고리 ID
    val uid: String = "",                  // 사용자 ID
    val name: String = "",                 // 카테고리 이름
    val description: String? = null,       // 카테고리 설명
    val colorHex: String = "",             // 사용자 지정 색상
    val createdAt: Timestamp? = null
): Parcelable