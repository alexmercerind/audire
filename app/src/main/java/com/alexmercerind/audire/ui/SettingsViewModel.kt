package com.alexmercerind.audire.ui

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.R
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

class SettingsViewModel(private val application: Application) : AndroidViewModel(application) {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "SETTINGS"
        private const val SHARED_PREFERENCES_KEY_APPEARANCE_THEME = "THEME"
        private const val SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME =
            "SYSTEM_COLOR_SCHEME"
    }

    val theme: StateFlow<String?>
        get() = _theme
    private val _theme = MutableStateFlow<String?>(null)

    val systemColorScheme: StateFlow<Boolean?>
        get() = _systemColorScheme
    private val _systemColorScheme = MutableStateFlow<Boolean?>(null)

    private val sharedPreferences =
        application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _theme.emit(
                sharedPreferences.getString(
                    SHARED_PREFERENCES_KEY_APPEARANCE_THEME,
                    application.getString(R.string.settings_appearance_theme_system)
                )
            )
            _systemColorScheme.emit(
                sharedPreferences.getBoolean(
                    SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                )
            )
        }
    }

    fun apply() {
        try {
            applyTheme(
                sharedPreferences.getString(
                    SHARED_PREFERENCES_KEY_APPEARANCE_THEME,
                    application.getString(R.string.settings_appearance_theme_system)
                )!!
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        try {
            applySystemColorScheme(
                sharedPreferences.getBoolean(
                    SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                )
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun setTheme(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putString(SHARED_PREFERENCES_KEY_APPEARANCE_THEME, value)
                apply()
            }
            _theme.emit(sharedPreferences.getString(SHARED_PREFERENCES_KEY_APPEARANCE_THEME, value))

            withContext(Dispatchers.Main) { applyTheme(value) }
        }
    }

    private fun applyTheme(value: String) {
        val mode = when (value) {
            application.getString(R.string.settings_appearance_theme_light) -> AppCompatDelegate.MODE_NIGHT_NO
            application.getString(R.string.settings_appearance_theme_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            application.getString(R.string.settings_appearance_theme_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> throw IllegalStateException()
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun setSystemColorScheme(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putBoolean(SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME, value)
                apply()
            }
            _systemColorScheme.emit(
                sharedPreferences.getBoolean(
                    SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME,
                    value
                )
            )

            withContext(Dispatchers.Main) { applySystemColorScheme(value) }
        }
    }

    private fun applySystemColorScheme(value: Boolean) {
        if (value) {
            DynamicColors.applyToActivitiesIfAvailable(application)
        } else {
            application.setTheme(R.style.Theme_Audire)
        }
    }
}
