package com.alexmercerind.audire.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.models.Music
import com.alexmercerind.audire.repository.HistoryRepository
import java.util.Calendar

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HistoryRepository(application)

    /**
     * Adds a new music into the database as history item.
     */
    fun insert(music: Music) = repository.insert(convertMusicToHistoryItem(music))

    /**
     * Returns all history items.
     */
    fun getAll() = repository.getAll()

    private fun convertMusicToHistoryItem(music: Music) = HistoryItem(
        null,
        Calendar.getInstance().time.time,
        music.title,
        music.artists,
        music.cover,
        music.album,
        music.label,
        music.year,
        music.lyrics
    )
}
