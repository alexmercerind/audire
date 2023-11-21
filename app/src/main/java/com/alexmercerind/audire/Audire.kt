package com.alexmercerind.audire

import android.app.Application
import com.alexmercerind.audire.ui.SettingsViewModel

class Audire : Application() {
    override fun onCreate() {
        super.onCreate()
        SettingsViewModel(this).apply()
    }
}
