package com.example.smartwriter

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartWriterApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(SmartWriterApplication::class.java.simpleName, "Application started")
    }
}