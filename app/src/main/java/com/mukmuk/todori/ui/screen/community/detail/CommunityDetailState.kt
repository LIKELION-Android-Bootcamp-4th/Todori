package com.mukmuk.todori.ui.screen.community.detail

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.Study

data class CommunityDetailState(

    val post: StudyPost? = null,
    val isLoading: Boolean = false,
    val study: Study? = null,
    val error: String? = null,
    val commentList: List<StudyPostComment> = emptyList()

)
