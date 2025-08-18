package com.mukmuk.todori.widget

import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun recordSettingRepository(): RecordSettingRepository
}