package com.ndriqa.cleansudoku.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ndriqa.cleansudoku.data.local.model.ApiCompletedGame
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedGameDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(game: ApiCompletedGame)

    @Query("SELECT * FROM completed_games ORDER BY completedTimestamp DESC")
    fun getAllFlow(): Flow<List<ApiCompletedGame>>

    @Query("DELETE FROM completed_games")
    suspend fun clearAll()
}