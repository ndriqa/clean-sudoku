package com.ndriqa.cleansudoku.data.repository

import com.ndriqa.cleansudoku.core.data.CompletedGame
import com.ndriqa.cleansudoku.data.local.db.CompletedGameDao
import com.ndriqa.cleansudoku.data.local.mapper.toApi
import com.ndriqa.cleansudoku.data.local.mapper.toUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CompletedGameRepository {
    suspend fun saveGame(game: CompletedGame)
    fun getAllGames(): Flow<List<CompletedGame>>
    suspend fun clear()
}

class CompletedGameRepositoryImpl @Inject constructor(
    private val dao: CompletedGameDao
) : CompletedGameRepository {

    override suspend fun saveGame(game: CompletedGame) {
        game.toApi()?.let { dao.insert(it) }
    }

    override fun getAllGames(): Flow<List<CompletedGame>> {
        return dao.getAllFlow()
            .map { list -> list.mapNotNull { it.toUI() } }
    }
    override suspend fun clear() {
        dao.clearAll()
    }
}