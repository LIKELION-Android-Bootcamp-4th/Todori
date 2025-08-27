package com.mukmuk.todori.ui.screen.community.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.repository.CommunityRepository
import com.mukmuk.todori.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val studyRepository: StudyRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(CreateCommunityState())
    val state: StateFlow<CreateCommunityState> = _state

    private var loadPostJob: Job? = null

    init {
        val uid = auth.currentUser?.uid ?: ""
        if (uid.isNotBlank()) {
            getUserById(uid)
        }
    }

    fun onEvent(event: CreateCommunityEvent) {
        when (event) {
            is CreateCommunityEvent.OnTitleChange -> _state.update { it.copy(title = event.title, isTitleError = false) }
            is CreateCommunityEvent.OnContentChange -> _state.update { it.copy(content = event.content) }
            is CreateCommunityEvent.OnTagClicked -> {
                val current = state.value.selectedTags.toMutableList()
                if (current.contains(event.tag)) {
                    current.remove(event.tag)
                } else if (current.size < 3) {
                    current.add(event.tag)
                }
                _state.value = state.value.copy(selectedTags = current)
            }
            is CreateCommunityEvent.OnStudyPickerClick -> {
                _state.update { it.copy(isStudyPickerVisible = true) }
                loadMyStudies()
            }
            is CreateCommunityEvent.OnTagPickerClick -> {
                _state.update { it.copy(isTagPickerVisible = true) }
            }
            is CreateCommunityEvent.OnTagRemoved -> {
                val updatedTags = _state.value.selectedTags.filter { it != event.tag }
                _state.update { it.copy(selectedTags = updatedTags) }
            }
            is CreateCommunityEvent.OnTagPickerDismiss -> _state.update { it.copy(isTagPickerVisible = false) }
            is CreateCommunityEvent.OnStudySelected -> loadStudy(event.studyId)
            is CreateCommunityEvent.OnStudyPickerDismiss -> _state.update { it.copy(isStudyPickerVisible = false) }
            is CreateCommunityEvent.OnPostSubmit -> submitPost(event.postId)
            is CreateCommunityEvent.LoadPostForEditing -> loadPostForEditing(event.postId)
        }
    }
    private fun loadMyStudies() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                val myStudies = studyRepository.getMyStudies(uid)
                _state.update { it.copy(myStudyList = myStudies) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    private fun getUserById(uid: String) {
        viewModelScope.launch {
            try {
                val user = communityRepository.getUserById(uid)
                _state.update { it.copy(user = user) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun loadStudy(studyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isStudyPickerVisible = false, isLoading = true, studyId = studyId) }
            try {
                communityRepository.loadStudyById(studyId).collectLatest { study ->
                    _state.update { it.copy(currentStudy = study, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadPostForEditing(postId: String) {
        loadPostJob?.cancel()
        loadPostJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                communityRepository.getPostById(postId).collectLatest { post ->
                    if (post != null) {
                        _state.update {
                            it.copy(
                                title = post.title,
                                content = post.content,
                                selectedTags = post.tags,
                                studyId = post.studyId,
                                isLoading = false,
                                post = post
                            )
                        }
                        if (post.studyId.isNotBlank()) {
                            loadStudy(post.studyId)
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun submitPost(postId: String?) {
        if (_state.value.title.isBlank()) {
            _state.update { it.copy(isTitleError = true) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val post = _state.value
                val uid = auth.currentUser?.uid ?: return@launch

                val postToSubmit = if (postId != null) {
                    val originalPost = post.post ?: return@launch
                    originalPost.copy(
                        title = post.title,
                        content = post.content,
                        tags = post.selectedTags,
                        studyId = post.studyId,
                        memberCount = post.memberList.size,
                        commentsCount = originalPost.commentsCount
                    )
                } else {
                    StudyPost(
                        title = post.title,
                        content = post.content,
                        tags = post.selectedTags,
                        studyId = post.studyId,
                        memberCount = post.memberList.size,
                        commentsCount = 0,
                        createdAt = Timestamp.now(),
                        createdBy = uid
                    )
                }

                if (postId != null) {
                    communityRepository.updatePost(postId, postToSubmit)
                } else {
                    communityRepository.createPost(postToSubmit)
                }

                _state.update { it.copy(isLoading = false, isPostSubmitted = true) }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}