package com.alexmercerind.audire.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.adapters.HistoryItemAdapter
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
     * HistoryItemAdapter to be used with RecyclerView.
     *
     * Automatically handles if a search term is entered.
     */
    val adapter: StateFlow<HistoryItemAdapter?>
        get() = _adapter

    private val _adapter = MutableStateFlow<HistoryItemAdapter?>(null)

    var term: String = ""
        set(value) {
            // Avoid duplicate operation in Room.
            if (value == field) {
                return
            }

            field = value
            viewModelScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    _adapter.emit(
                        HistoryItemAdapter(
                            when (value) {
                                // Search term == "" -> Show all HistoryItem(s)
                                "" -> getAll().first()
                                // Search term != "" -> Show search HistoryItem(s)
                                else -> search(value.lowercase())
                            }, this@HistoryViewModel
                        )
                    )
                }
            }
        }

    private val mutex = Mutex()

    private val repository = HistoryRepository(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAll().collect {
                _adapter.emit(
                    HistoryItemAdapter(
                        it, this@HistoryViewModel
                    )
                )
            }
        }
    }

    private fun getAll() = repository.getAll()

    private suspend fun search(term: String) = repository.search(term)

    fun insert(historyItem: HistoryItem) = repository.insert(historyItem)

    fun delete(historyItem: HistoryItem) = repository.delete(historyItem)
}
