package com.ndriqa.cleansudoku.core.util.notifications

import android.app.NotificationManager

sealed class NotificationChannelInfo(
    val id: String,
    val name: String,
    val description: String,
    val importance: Int
) {
    object General : NotificationChannelInfo(
        id = "general_channel",
        name = "General Notifications",
        description = "General notifications for this app",
        importance = NotificationManager.IMPORTANCE_HIGH
    )
}
