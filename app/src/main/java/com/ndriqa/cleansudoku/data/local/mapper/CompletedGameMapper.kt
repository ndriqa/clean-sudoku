package com.ndriqa.cleansudoku.data.local.mapper

import com.ndriqa.cleansudoku.core.data.CompletedGame
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.data.local.model.ApiCompletedGame
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun CompletedGame.toApi(): ApiCompletedGame? {
    return ApiCompletedGame(
        sudokuBoard = SudokuBoard.toString(sudokuBoard) ?: return null,
        completedTimestamp = completedDate.toInstant().toEpochMilli(),
        completionTime = completionTime,
        difficulty = difficulty.name
    )
}

fun ApiCompletedGame.toUI(): CompletedGame? {
    val parsedBoard = sudokuBoard?.let { SudokuBoard.fromString(it) } ?: return null

    val parsedDifficulty = Level.entries
        .find { it.name.equals(difficulty, ignoreCase = true) }
        ?: return null

    val zonedDateTime = completedTimestamp?.let {
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(it),
            ZoneId.systemDefault()
        )
    } ?: return null

    return CompletedGame(
        sudokuBoard = parsedBoard,
        completedDate = zonedDateTime,
        completionTime = completionTime ?: return null,
        difficulty = parsedDifficulty
    )
}