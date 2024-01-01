package com.alexmercerind.audire.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.alexmercerind.audire.repository.HistoryRepository
import com.alexmercerind.audire.repository.ImportExportRepository
import com.alexmercerind.audire.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val historyRepository = HistoryRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val importExportRepository = ImportExportRepository(application)

    val theme = settingsRepository.theme

    val systemColorScheme = settingsRepository.systemColorScheme

    fun setTheme(value: String) = settingsRepository.setTheme(value)

    fun setSystemColorScheme(value: Boolean) = settingsRepository.setSystemColorScheme(value)

    suspend fun import(uri: Uri) = importExportRepository.import(uri)

    suspend fun export(uri: Uri) = importExportRepository.export(
        uri,
        historyRepository.getAll().first()
    )
}
