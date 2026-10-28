package tachiyomi.presentation.core.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate

/**
 * Draws Material 3 Expressive decorative shapes behind the content of a surface.
 *
 * Shapes are computed once per size through [drawWithCache], so the backdrop costs
 * nothing but a handful of draw calls per frame.
 */
fun Modifier.expressiveBackdrop(
    shapeColor: Color,
    accentColor: Color = shapeColor,
): Modifier = drawWithCache {
    val width = size.width
    val height = size.height
    val shortest = size.minDimension

    val circleRadius = shortest * 0.86f
    val circleCenter = Offset(width * 0.9f, height * 0.08f)
    val accentRadius = shortest * 0.3f
    val accentCenter = Offset(width * 0.58f, height * 1.04f)
    val dotRadius = shortest * 0.055f
    val dotCenter = Offset(width * 0.12f, height * 0.16f)

    val pillSize = Size(width * 0.42f, height * 0.9f)
    val pillTopLeft = Offset(width * 0.02f, height * 0.42f)
    val pillCorner = CornerRadius(pillSize.width * 0.42f, pillSize.width * 0.42f)

    onDrawBehind {
        drawCircle(
            color = shapeColor.copy(alpha = 0.14f),
            radius = circleRadius,
            center = circleCenter,
        )
        drawCircle(
            color = accentColor.copy(alpha = 0.10f),
            radius = accentRadius,
            center = accentCenter,
        )
        rotate(degrees = -28f, pivot = Offset(pillTopLeft.x, pillTopLeft.y + pillSize.height / 2f)) {
            drawRoundRect(
                color = accentColor.copy(alpha = 0.09f),
                topLeft = pillTopLeft,
                size = pillSize,
                cornerRadius = pillCorner,
            )
        }
        drawCircle(
            color = shapeColor.copy(alpha = 0.22f),
            radius = dotRadius,
            center = dotCenter,
        )
    }
}
