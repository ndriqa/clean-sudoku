package com.ndriqa.cleansudoku.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ndriqa.cleansudoku.R

fun ndriqaOtherApps(context: Context) {
    val developerId = context.getString(R.string.ndriqa_developer_id)
    val playStoreUri = "https://play.google.com/store/apps/dev?id=$developerId"
    Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUri))
        .apply { `package` = "com.android.vending" }
        .also {
            try {
                context.startActivity(it)
            } catch (e: ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUri)))
            }
        }
}