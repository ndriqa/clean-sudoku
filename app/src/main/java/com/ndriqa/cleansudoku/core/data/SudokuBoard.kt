package com.ndriqa.cleansudoku.core.data

import android.os.Parcelable
import com.ndriqa.cleansudoku.core.util.sudoku.countSolutions
import com.ndriqa.cleansudoku.core.util.sudoku.isSudokuSolved
import com.ndriqa.cleansudoku.core.util.sudoku.isValidSudoku
import kotlinx.parcelize.Parcelize

@Parcelize
data class SudokuBoard(
    val board: Array<IntArray>
): Parcelable {
    fun isValid() = isValidSudoku(board)
    fun countSolutions() = countSolutions(board)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SudokuBoard

        if (!board.contentDeepEquals(other.board)) return false

        return true
    }

    override fun hashCode(): Int {
        return board.contentDeepHashCode()
    }

    companion object {
        fun fromString(board: String, size: Int = 9): SudokuBoard? {
            val sudokuNumbersBoard = board
                .split("-")
                .mapNotNull { it.toIntOrNull() }
                .also { if (it.size != size * size) return null }

            val sudokuBoard = sudokuNumbersBoard
                .chunked(size)
                .map { it.toIntArray() }
                .toTypedArray()

            return SudokuBoard(board = sudokuBoard)
        }

        fun toString(sudokuBoard: SudokuBoard): String? {
            return if (isSudokuSolved(sudokuBoard.board)) {
                sudokuBoard.board.joinToString("-") { it.joinToString("-") }
            } else null
        }
    }
}
