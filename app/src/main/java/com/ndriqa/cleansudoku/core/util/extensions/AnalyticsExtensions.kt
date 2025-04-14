package com.ndriqa.cleansudoku.core.util.extensions

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ndriqa.cleansudoku.core.data.AnalyticsEvent

fun AnalyticsEvent.logEvent() {
    Firebase.analytics.logEvent(name, params)
}

fun Array<IntArray>.toAnalyticsString(): String {
    return joinToString("\n") { it.joinToString("") }
}