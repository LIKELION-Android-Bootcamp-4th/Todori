package com.mukmuk.todori.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.mukmuk.todori.data.remote.todo.Todo
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TodayTodoRepository @Inject constructor(
    @Named("todo_settings") private val dataStore: DataStore<Preferences>
) {
    private val TODOS_KEY = stringPreferencesKey("today_todos_widget")

    suspend fun saveTodayTodos(todos: List<Todo>) {
        val json = Gson().toJson(todos)
        dataStore.edit { prefs ->
            prefs[TODOS_KEY] = json
        }
    }

    suspend fun clearTodoSetting() {
        dataStore.edit { it.clear() }
    }
}