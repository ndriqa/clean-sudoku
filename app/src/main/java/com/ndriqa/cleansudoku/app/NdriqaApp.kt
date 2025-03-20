package com.ndriqa.cleansudoku.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NdriqaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeNotificationChannels()
    }

    private fun initializeNotificationChannels() {
//        NotificationHelper.createNotificationChannels(this)
    }

//    private fun initializeFirebase() {
//        FirebaseApp.initializeApp(this)
//    }
}