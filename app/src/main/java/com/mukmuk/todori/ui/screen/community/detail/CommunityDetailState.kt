package com.mukmuk.todori.ui.screen.community.detail

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember

data class CommunityDetailState(

    val post: StudyPost? = null,
    val isLoading: Boolean = false,
    val studyId: String = "",
    val study: Study? = null,
    val memberList: List<StudyMember> = emptyList(),
    val error: String? = null,
    val commentList: Map<String, List<StudyPostComment>> = emptyMap(),

)
