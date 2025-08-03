package com.alexmercerind.audire.mappers

import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.models.Music
import java.util.Calendar

fun Music.toHistoryItem() = HistoryItem(
    null,
    Calendar.getInstance().time.time,
    title,
    artists,
    cover,
    album,
    label,
    year,
    lyrics,
    false
)

fun Music.toSearchQuery() = listOfNotNull(
    title.ifBlank { null },
    artists.ifBlank { null },
    album?.ifBlank { null },
    year?.ifBlank { null }
)
    .joinToString(" ")
    .trim()
