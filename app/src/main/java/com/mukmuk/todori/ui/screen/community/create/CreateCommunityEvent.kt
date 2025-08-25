package com.mukmuk.todori.ui.screen.community.create

sealed interface CreateCommunityEvent {
    data class OnTitleChange(val title: String) : CreateCommunityEvent
    data class OnContentChange(val content: String) : CreateCommunityEvent
    data class OnTagClick(val tag: String) : CreateCommunityEvent
    data object OnStudyPickerClick : CreateCommunityEvent
    data class OnStudySelected(val studyId: String) : CreateCommunityEvent
    data object OnStudyPickerDismiss : CreateCommunityEvent
    data class OnPostSubmit(val postId: String? = null) : CreateCommunityEvent
    data class LoadPostForEditing(val postId: String) : CreateCommunityEvent
}