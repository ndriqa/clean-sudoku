package com.ndriqa.cleansudoku.core.util.extensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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