package com.ndriqa.cleansudoku.core.util.sudoku

import androidx.annotation.StringRes
import com.ndriqa.cleansudoku.R

enum class Level(
    @StringRes val titleResId: Int,
    val id: String,
    val digitsToRemove: Int,
) {
    EASY(titleResId = R.string.difficulty_easy, id = "easy", digitsToRemove = 34),
    MID(titleResId = R.string.difficulty_mid, id = "mid", digitsToRemove = 45),
    HARD(titleResId = R.string.difficulty_hard, id = "hard", digitsToRemove = 52);

    companion object {
        fun getLevel(id: String) = entries.find { it.id == id } ?: EASY
    }
}