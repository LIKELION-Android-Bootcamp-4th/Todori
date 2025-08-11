package com.mukmuk.todori.ui.screen.community

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.StudyMember

data class CommunityState (

    val postList: List<StudyPost> = emptyList(),
    val allPostList: List<StudyPost> = emptyList(),
    val selectedOption: String = "참가자 수",
    val communitySearchPostList: List<StudyPost> = emptyList(),
    val communitySearchList: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    )