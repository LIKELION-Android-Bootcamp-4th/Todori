package com.mukmuk.todori.ui.screen.community.detail

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.user.User

data class CommunityDetailState(

    val post: StudyPost? = null,
    val user: User? = null,
    val userMap: Map<String, User?> = emptyMap(),
    val isLoading: Boolean = false,
    val study: Study? = null,
    val memberList: List<StudyMember> = emptyList(),
    val error: String? = null,
    val commentList: List<StudyPostComment> = emptyList(),
    val replyToCommentId: String? = null,

    )
