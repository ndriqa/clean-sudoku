package com.ndriqa.cleansudoku.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_games")
data class ApiCompletedGame(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sudokuBoard: String?,
    val completedTimestamp: Long?, // in epoch millis, for better sorting/comparing
    val completionTime: Long?, // milliseconds
    val difficulty: String?, // e.g., "Easy", "Medium", etc.
)