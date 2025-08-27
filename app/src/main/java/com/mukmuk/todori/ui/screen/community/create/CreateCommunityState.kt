package com.mukmuk.todori.ui.screen.community.create

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.study.MyStudy
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.user.User

data class CreateCommunityState(
    val title: String = "",
    val content: String = "",
    val isTitleError: Boolean = false,
    val selectedTags: List<String> = emptyList(),
    val isStudyPickerVisible: Boolean = false,
    val isTagPickerVisible: Boolean = false,
    val currentStudy: Study? = null,
    val myStudyList: List<MyStudy> = emptyList(),
    val memberList: List<StudyMember> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPostSubmitted: Boolean = false,
    val studyId: String = "",
    val user: User? = null,
    val post: StudyPost? = null,
    val toastMessage: String? = null
)