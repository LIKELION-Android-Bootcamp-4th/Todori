package com.mukmuk.todori.widget

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateDispatcher @Inject constructor(
    @ApplicationContext private val context: Context
){
    fun updateTodos(){}
}