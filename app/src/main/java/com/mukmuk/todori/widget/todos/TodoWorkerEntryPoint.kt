package com.mukmuk.todori.widget.todos

import com.mukmuk.todori.data.repository.TodoRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TodoWorkerEntryPoint {
    fun todoRepository(): TodoRepository
}