package com.alexmercerind.audire.repository

import android.app.Application
import com.alexmercerind.audire.db.HistoryItemDatabase
import com.alexmercerind.audire.models.HistoryItem

class HistoryRepository(private val application: Application) {
    fun insert(historyItem: HistoryItem) =
        HistoryItemDatabase(application)
            .historyItemDao()
            .insert(historyItem)

    fun delete(historyItem: HistoryItem) =
        HistoryItemDatabase(application)
            .historyItemDao()
            .delete(historyItem)

    fun getAll() =
        HistoryItemDatabase(application)
            .historyItemDao()
            .getAll()
}
