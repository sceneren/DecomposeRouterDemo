package com.github.sceneren.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ConvexBottomBar(
    modifier: Modifier = Modifier,
    circleRadius: Dp = 20.dp,
    circleGap: Dp = 5.dp,
    backGroundColor: Color = Color.White,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.Gray,
    content: @Composable RowScope.() -> Unit
) {

    var widthPx by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current

    val borderWidthPx = density.run { borderWidth.toPx() }

    val barShape by remember {
        derivedStateOf {
            BarShape(
                offset = widthPx / 2f,
                circleRadius = circleRadius,
                circleGap = circleGap,
            )
        }
    }


    Row(
        modifier = modifier
            .background(color = backGroundColor, shape = barShape)
            .shadow(
                elevation = 0.dp,
                shape = barShape
            )
            .drawBehind {
                val borderPath = barShape.getTopPath(size, density)
                drawPath(
                    path = borderPath,
                    color = borderColor,
                    style = Stroke(width = borderWidthPx),
                    blendMode = BlendMode.SrcOver
                )
            }
            .onGloballyPositioned { coordinates ->
                widthPx = coordinates.size.width
            },
        content = content
    )
}