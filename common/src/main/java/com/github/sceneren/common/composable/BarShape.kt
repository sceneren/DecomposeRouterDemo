package com.github.sceneren.common.composable

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(getPath(size, density))
    }

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 2.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            // bottom left
            moveTo(x = 0F, y = size.height)
            // top left
            if (cutoutLeftX > 0) {
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = cutoutRadius,
                        right = 0f,
                        bottom = 0f + cutoutRadius
                    ),
                    startAngleDegrees = 180.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            lineTo(cutoutLeftX, cutoutRadius)
            // cutout
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = 0f,
                x3 = cutoutCenterX,
                y3 = 0f,
            )
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutRightX,
                y3 = cutoutRadius,
            )
            // top right
            if (cutoutRightX < size.width) {
                val realRightCornerDiameter = if (cutoutRightX <= size.width) {
                    0f
                } else {
                    (size.width - cutoutRightX) * 2
                }
                arcTo(
                    rect = Rect(
                        left = size.width - realRightCornerDiameter,
                        top = cutoutRadius,
                        right = size.width,
                        bottom = realRightCornerDiameter + cutoutRadius
                    ),
                    startAngleDegrees = -90.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            // bottom right
            lineTo(x = size.width, y = size.height)
            close()
        }
    }

    fun getTopPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 2.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            // bottom left
            moveTo(x = 0F, y = cutoutRadius)

            lineTo(cutoutLeftX, cutoutRadius)
            // cutout
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = 0f,
                x3 = cutoutCenterX,
                y3 = 0f,
            )
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutRightX,
                y3 = cutoutRadius,
            )
            moveTo(x = cutoutRightX, y = cutoutRadius)
            // bottom right
            lineTo(x = size.width, y = cutoutRadius)
            close()
        }
    }
}