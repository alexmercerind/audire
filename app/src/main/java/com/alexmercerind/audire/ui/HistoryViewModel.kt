package com.alexmercerind.audire.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.map
import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.repository.HistoryRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HistoryRepository(application)

    fun insert(historyItem: HistoryItem) = repository.insert(historyItem)

    fun getAll() = repository.getAll()
}
