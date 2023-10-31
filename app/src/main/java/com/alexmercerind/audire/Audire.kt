package com.alexmercerind.audire

import android.app.Application
import com.google.android.material.color.DynamicColors

class Audire : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
