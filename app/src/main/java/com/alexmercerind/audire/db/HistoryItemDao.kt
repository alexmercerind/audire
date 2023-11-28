package com.alexmercerind.audire.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexmercerind.audire.models.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historyItem: HistoryItem)

    @Delete
    fun delete(historyItem: HistoryItem)

    @Query("SELECT * FROM history_item ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history_item WHERE LOWER(title) LIKE '%' || :term || '%' ORDER BY timestamp DESC")
    suspend fun search(term: String): List<HistoryItem>
}
