package com.ndriqa.cleansudoku.core.data

import com.ndriqa.cleansudoku.core.util.sudoku.Level
import java.time.ZonedDateTime

data class CompletedGame(
    val sudokuBoard: SudokuBoard,
    val completedDate: ZonedDateTime, // in epoch millis, for better sorting/comparing
    val completionTime: Long, // milliseconds
    val difficulty: Level // e.g., "Easy", "Medium", etc.
)
