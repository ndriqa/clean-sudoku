package com.ndriqa.cleansudoku.core.data

import android.os.Bundle
import androidx.core.os.bundleOf

sealed class AnalyticsEvent(
    val name: String,
    val params: Bundle? = null
) {
    class ScreenView(screenName: String) : AnalyticsEvent(
        name = "screen_view",
        params = bundleOf("screen_name" to screenName)
    )

    class ButtonClick(buttonName: String) : AnalyticsEvent(
        name = "button_click",
        params = bundleOf("button_name" to buttonName)
    )

    class Switch(switchName: String, optionSelected: String) : AnalyticsEvent(
        name = "switch",
        params = bundleOf(
            "switch_name" to switchName,
            "switched_to" to optionSelected
        )
    )

    class CustomEvent(eventName: String, customParams: Map<String, Any?> = emptyMap()) : AnalyticsEvent(
        name = eventName,
        params = Bundle().apply {
            customParams.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }
    )
}
