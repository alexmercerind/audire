package com.alexmercerind.audire.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * HistoryItem(s) to be used displayed in RecyclerView.
     *
     * Automatically handles if a search term is entered.
     */
    val historyItems: StateFlow<List<HistoryItem>?>
        get() = _historyItems

    private val _historyItems = MutableStateFlow<List<HistoryItem>?>(null)

    var term: String = ""
        set(value) {
            // Avoid duplicate operation in Room.
            if (value == field) {
                return
            }

            field = value
            viewModelScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    _historyItems.emit(
                        when (value) {
                            // Search term == "" -> Show all HistoryItem(s)
                            "" -> getAll().first()
                            // Search term != "" -> Show search HistoryItem(s)
                            else -> search(value.lowercase())
                        }
                    )
                }
            }
        }

    private val mutex = Mutex()

    private val repository = HistoryRepository(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAll().collect {
                _historyItems.emit(it)
            }
        }
    }

    private fun getAll() = repository.getAll()

    private suspend fun search(term: String) = repository.search(term)

    suspend fun insert(historyItem: HistoryItem) = repository.insert(historyItem)

    suspend fun delete(historyItem: HistoryItem) = repository.delete(historyItem)

    suspend fun like(historyItem: HistoryItem) = repository.like(historyItem)

    suspend fun unlike(historyItem: HistoryItem) = repository.unlike(historyItem)
}
