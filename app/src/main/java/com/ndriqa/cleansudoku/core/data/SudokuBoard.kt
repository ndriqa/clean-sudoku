package com.ndriqa.cleansudoku.core.data

import android.os.Parcelable
import com.ndriqa.cleansudoku.core.util.sudoku.countSolutions
import com.ndriqa.cleansudoku.core.util.sudoku.isValidSudoku
import kotlinx.parcelize.Parcelize

@Parcelize
data class SudokuBoard(
    val board: Array<IntArray>
): Parcelable {
    fun isValid() = isValidSudoku(board)
    fun countSolutions() = countSolutions(board)
}
