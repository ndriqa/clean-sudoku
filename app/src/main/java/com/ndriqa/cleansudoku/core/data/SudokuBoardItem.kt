package com.ndriqa.cleansudoku.core.data

data class SudokuBoardItem(
    val number: Int?,
    val isInitial: Boolean,
    val helperDigits: MutableSet<Int> = mutableSetOf()
)
