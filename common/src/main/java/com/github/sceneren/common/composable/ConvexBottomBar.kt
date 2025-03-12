package com.github.sceneren.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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

/**
 * 可组合函数，用于创建一个带有凸起形状的底部导航栏。
 *
 * @param modifier 修饰符，用于自定义布局或外观，默认为 `Modifier`。
 * @param circleRadius 凸起圆的半径，默认为 `20.dp`。
 * @param circleGap 凸起圆之间的间距，默认为 `5.dp`。
 * @param backgroundColor 底部导航栏的背景颜色，默认为 `Color.White`。
 * @param borderWidth 边框宽度，默认为 `1.dp`。
 * @param borderColor 边框颜色，默认为 `Color.Gray`。
 * @param content 底部导航栏的内容，使用 `RowScope` 定义。
 */
@Composable
fun ConvexBottomBar(
    modifier: Modifier = Modifier,
    circleRadius: Dp = 20.dp,
    circleGap: Dp = 5.dp,
    backgroundColor: Color = Color.White,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.Gray,
    content: @Composable RowScope.() -> Unit
) {

    // 记录底部导航栏宽度的像素值，初始值为 0。
    var widthPx by remember { mutableIntStateOf(0) }

    // 获取当前屏幕的密度，用于将 dp 转换为像素。
    val density = LocalDensity.current

    // 将边框宽度从 dp 转换为像素。
    val borderWidthPx = density.run { borderWidth.toPx() }

    // 根据宽度、圆半径和间距动态计算底部导航栏的形状。
    val barShape by remember {
        derivedStateOf {
            BarShape(
                offset = widthPx / 2f,
                circleRadius = circleRadius,
                circleGap = circleGap,
            )
        }
    }

    // 使用 Row 创建底部导航栏，并应用修饰符和内容。
    Row(
        modifier = modifier
            .background(color = backgroundColor, shape = barShape) // 设置背景颜色和形状。
            .shadow(
                elevation = 0.dp,
                shape = barShape
            ) // 添加阴影效果。
            .drawBehind {
                // 绘制顶部边框路径。
                val borderPath = barShape.getTopPath(size, density)
                drawPath(
                    path = borderPath,
                    color = borderColor,
                    style = Stroke(width = borderWidthPx),
                    blendMode = BlendMode.SrcOver
                )
            }
            .onGloballyPositioned { coordinates ->
                // 当组件布局完成后，获取其宽度并更新 widthPx。
                widthPx = coordinates.size.width
            },
        content = content // 定义导航栏的内容。
    )
}