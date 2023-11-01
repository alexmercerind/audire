package com.alexmercerind.audire.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexmercerind.audire.models.HistoryItem

@Dao
interface HistoryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historyItem: HistoryItem)

    @Query("SELECT * FROM history_item ORDER BY timestamp DESC")
    fun getAll(): LiveData<List<HistoryItem>>
}
