package com.ndriqa.cleansudoku.core.util.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

// Non-nullable function to convert any object to JSON
inline fun <reified T> T.toJson(): String {
    return gson.toJson(this)
}

// Nullable function to convert JSON back to an object
inline fun <reified T> String?.fromJson(): T? {
    return this?.let {
        gson.fromJson(it, object : TypeToken<T>() {}.type)
    }
}