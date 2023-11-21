package com.alexmercerind.audire.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alexmercerind.audire.repository.SettingsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    val theme = repository.theme

    val systemColorScheme = repository.systemColorScheme

    fun setTheme(value: String) = repository.setTheme(value)

    fun setSystemColorScheme(value: Boolean) = repository.setSystemColorScheme(value)
}
