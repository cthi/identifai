package com.nhacks16.identifai

import android.app.Application
import android.content.Intent

class IdentifaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startService(Intent (this, AlertService::class.java))
    }
}