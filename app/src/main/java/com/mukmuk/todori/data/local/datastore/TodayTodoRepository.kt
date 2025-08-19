package com.mukmuk.todori.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mukmuk.todori.data.remote.todo.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TodayTodoRepository  @Inject constructor(
    @Named("today_todo") private val dataStore: DataStore<Preferences>
) {
    private val TODOS_KEY = stringPreferencesKey("today_todo_widget")

    val todayTodosFlow: Flow<List<Todo>> = dataStore.data.map { prefs ->
        val json = prefs[TODOS_KEY] ?: "[]"
        Gson().fromJson(json, object : TypeToken<List<Todo>>() {}.type)
    }

    suspend fun saveTodayTodos(todos: List<Todo>) {
        val json = Gson().toJson(todos)
        dataStore.edit { prefs ->
            prefs[TODOS_KEY] = json
        }
    }

    suspend fun clearTodayTodos() {
        dataStore.edit { prefs ->
            prefs.remove(TODOS_KEY)
        }
    }
}