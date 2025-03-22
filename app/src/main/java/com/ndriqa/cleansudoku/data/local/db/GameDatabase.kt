package com.ndriqa.cleansudoku.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ndriqa.cleansudoku.data.local.model.ApiCompletedGame

@Database(entities = [ApiCompletedGame::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun completedGameDao(): CompletedGameDao
}