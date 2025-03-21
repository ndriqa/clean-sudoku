package com.ndriqa.cleansudoku.core.util.extensions

import java.util.Locale

fun Long.toFormattedTime(): String {
    val hours = this / 3600000
    val minutes = (this / 60000) % 60
    val seconds = (this / 1000) % 60
    val milliseconds = this % 1000

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d:%03d", minutes, seconds, milliseconds)
    }
}

fun List<Int>.asCandidateGrid(boardSize: Int = 9): String {
    return (1..boardSize)
        .map { if (contains(it)) "$it" else " " }
        .chunked(3)
        .joinToString("\n") { it.joinToString(" ") }
}
