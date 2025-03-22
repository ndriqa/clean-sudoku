package com.ndriqa.cleansudoku.core.util.extensions

import android.view.KeyEvent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import com.ndriqa.cleansudoku.core.data.MoveDirection
import com.ndriqa.cleansudoku.core.util.sudoku.Level

fun Modifier.dashedBorder(
    borderWidth: Dp,
    color: Color,
    shape: RoundedCornerShape,
    dashWidth: Float = 10f,
    dashGap: Float = 10f
): Modifier = this.drawBehind {
    val strokeWidthPx = borderWidth.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)

    val path = when (outline) {
        is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        is Outline.Generic -> outline.path
        else -> return@drawBehind
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidthPx, pathEffect = pathEffect)
    )
}

fun Modifier.sudokuKeyboardInput(
    onNumberClicked: (Int?) -> Unit,
    onSelectedCellMove: (MoveDirection) -> Unit,
    onCandidateModeToggle: () -> Unit
): Modifier = this.onKeyEvent { event ->
    if (event.type == KeyEventType.KeyDown) {
        val number = event.nativeKeyEvent.unicodeChar.toChar().digitToIntOrNull()
        val keyCode = event.nativeKeyEvent.keyCode

        when {
            number in 1..9 -> {
                onNumberClicked(number)
                true
            }

            keyCode == KeyEvent.KEYCODE_DEL ||
                    event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_FORWARD_DEL -> {
                onNumberClicked(null)
                true
            }

            keyCode == KeyEvent.KEYCODE_SPACE -> {
                onCandidateModeToggle()
                true
            }

            keyCode == KeyEvent.KEYCODE_DPAD_UP -> {
                onSelectedCellMove(MoveDirection.UP)
                true
            }

            keyCode == KeyEvent.KEYCODE_DPAD_DOWN -> {
                onSelectedCellMove(MoveDirection.DOWN)
                true
            }

            keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> {
                onSelectedCellMove(MoveDirection.LEFT)
                true
            }

            keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> {
                onSelectedCellMove(MoveDirection.RIGHT)
                true
            }

            else -> false
        }
    } else false
}

fun Level.getMaterialIcon() = when(this) {
    Level.EASY -> Icons.Rounded.CheckCircle
    Level.MID -> Icons.Rounded.Extension
    Level.HARD -> Icons.Rounded.ElectricBolt
}
