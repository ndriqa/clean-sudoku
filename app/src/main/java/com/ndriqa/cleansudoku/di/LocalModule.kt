package com.ndriqa.cleansudoku.di

import android.content.Context
import androidx.room.Room
import com.ndriqa.cleansudoku.data.local.db.CompletedGameDao
import com.ndriqa.cleansudoku.data.local.db.GameDatabase
import com.ndriqa.cleansudoku.data.repository.CompletedGameRepository
import com.ndriqa.cleansudoku.data.repository.CompletedGameRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            "game.db"
        ).build()
    }

    @Provides
    fun provideCompletedGameDao(db: GameDatabase): CompletedGameDao {
        return db.completedGameDao()
    }

    @Provides
    @Singleton
    fun provideCompletedGameRepository(
        dao: CompletedGameDao
    ): CompletedGameRepository {
        return CompletedGameRepositoryImpl(dao)
    }
}