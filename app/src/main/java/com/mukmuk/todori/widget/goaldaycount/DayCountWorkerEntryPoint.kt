package com.mukmuk.todori.widget.goaldaycount

import com.mukmuk.todori.data.repository.GoalRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DayCountWorkerEntryPoint {
    fun goalRepository(): GoalRepository
}