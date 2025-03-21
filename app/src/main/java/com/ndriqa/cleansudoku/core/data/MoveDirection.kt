package com.ndriqa.cleansudoku.core.data

enum class MoveDirection(val offset: IntOffset) {
    UP(offset = IntOffset(0, -1)),
    RIGHT(offset = IntOffset(1, 0)),
    DOWN(offset = IntOffset(0, 1)),
    LEFT(offset = IntOffset(-1, 0)),
}

data class IntOffset(
    val x: Int,
    val y: Int
)