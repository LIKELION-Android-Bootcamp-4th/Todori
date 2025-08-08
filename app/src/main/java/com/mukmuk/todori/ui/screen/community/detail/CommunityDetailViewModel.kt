package com.mukmuk.todori.ui.screen.community.detail

import androidx.lifecycle.ViewModel
import com.mukmuk.todori.data.repository.CommunityRepository
import jakarta.inject.Inject


class CommunityDetailViewModel@Inject constructor(
    private val repository: CommunityRepository
): ViewModel() {

}