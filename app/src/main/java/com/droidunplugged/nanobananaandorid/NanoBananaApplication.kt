package com.droidunplugged.nanobananaandorid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NanoBananaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize logging or other app-wide tools here
    }
}
