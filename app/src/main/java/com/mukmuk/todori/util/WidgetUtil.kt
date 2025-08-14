package com.mukmuk.todori.util

import android.content.Context
import android.util.Log

object WidgetUtil {
    fun saveWidgetTodos(context: Context, todos: List<Pair<String, Boolean>>) {
        val prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE)
        val data = todos.joinToString("|") { "${it.first},${it.second}" }
        prefs.edit().putString("todos", data).apply()
    }
    fun loadWidgetTodos(context: Context): List<Pair<String, Boolean>> {
        val prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE)
        val dataString = prefs.getString("todos", null)
        Log.d("WidgetDebug", "제발~~~: $dataString")
        if (dataString.isNullOrEmpty()) return listOf("TODO가 없습니다." to false)
        return dataString.split("|").mapNotNull { item ->
            val parts = item.split(",")
            if (parts.size == 2) {
                val title = parts[0]
                val done = parts[1].toBooleanStrictOrNull() ?: false
                title to done
            } else null
        }
    }
}