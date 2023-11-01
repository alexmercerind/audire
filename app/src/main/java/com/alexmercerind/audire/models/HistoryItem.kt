package com.alexmercerind.audire.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "history_item")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artists") val artists: String,
    @ColumnInfo(name = "cover") val cover: String,
    @ColumnInfo(name = "album") val album: String?,
    @ColumnInfo(name = "label") val label: String?,
    @ColumnInfo(name = "year") val year: String?,
    @ColumnInfo(name = "lyrics") val lyrics: String?,
) : Serializable
