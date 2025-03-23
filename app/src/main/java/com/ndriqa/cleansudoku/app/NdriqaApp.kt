package com.ndriqa.cleansudoku.app

import android.app.Application
import com.ndriqa.cleansudoku.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NdriqaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeLogger()
        initializeNotificationChannels()
    }

    private fun initializeLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initializeNotificationChannels() {
//        NotificationHelper.createNotificationChannels(this)
    }

//    private fun initializeFirebase() {
//        FirebaseApp.initializeApp(this)
//    }
}