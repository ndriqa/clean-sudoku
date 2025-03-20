package com.ndriqa.cleansudoku.core.util.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    borderWidth: Dp,
    color: Color,
    cornerRadius: Dp = 0.dp,
    dashWidth: Float = 10f,
    dashGap: Float = 10f
): Modifier = this.drawBehind {
    val strokeWidthPx = borderWidth.toPx()
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)

    drawRoundRect(
        color = color,
        style = Stroke(width = strokeWidthPx, pathEffect = pathEffect),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        size = size.copy(
            width = size.width - strokeWidthPx,
            height = size.height - strokeWidthPx
        ),
        topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
    )
}