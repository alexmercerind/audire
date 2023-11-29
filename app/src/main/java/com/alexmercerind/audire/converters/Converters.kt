package com.alexmercerind.audire.converters

import com.alexmercerind.audire.api.shazam.models.ShazamResponse
import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.models.Music
import java.util.Calendar

fun ByteArray.toShortArray(): ShortArray {
    val result = ShortArray(size / 2)
    for (i in 0..result.size step 2) {
        result[i / 2] = (this[i].toInt() and 0xFF or (this[i + 1].toInt() shl 8)).toShort()
    }
    return result
}

fun ShazamResponse.toMusic() = Music(
    track?.title!!,
    track?.subtitle!!,
    track?.images?.coverarthq!!,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
        ?.metadata
        ?.firstOrNull { metadata -> metadata.title?.uppercase() == "ALBUM" }
        ?.text,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
        ?.metadata
        ?.firstOrNull { metadata -> metadata.title?.uppercase() == "LABEL" }
        ?.text,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
        ?.metadata
        ?.firstOrNull { metadata -> metadata.title?.uppercase() == "RELEASED" }
        ?.text,
    track?.sections
        ?.firstOrNull { section -> section.type?.uppercase() == "LYRICS" }
        ?.text
        ?.joinToString("\n")
)

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

fun HistoryItem.toMusic() = Music(
    title,
    artists,
    cover,
    album,
    label,
    year,
    lyrics
)
