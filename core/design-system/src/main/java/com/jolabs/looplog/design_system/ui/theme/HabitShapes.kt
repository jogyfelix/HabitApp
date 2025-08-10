package com.jolabs.looplog.design_system.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import kotlin.math.max


class RoundedPolygonShape(polygon: RoundedPolygon) : Shape {

    private val originalPath = polygon.toPath().asComposePath()
    private val bounds = polygon.calculateBounds().let {
        Rect(it[0], it[1], it[2], it[3])
    }
    private val maxDimension = max(bounds.width, bounds.height)

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply { addPath(originalPath) }

        val matrix = Matrix().apply {
            scale(size.width / maxDimension, size.height / maxDimension)
            translate(-bounds.left, -bounds.top)
        }

        path.transform(matrix)

        return Outline.Generic(path)
    }
}





object HabitShapes {
    // Basic polygons
    val OctagonShape = RoundedPolygonShape(RoundedPolygon(8, rounding = CornerRounding(0.2f)))

    // Cookie shapes (rounded polygons)
    val Cookie4Shape = RoundedPolygonShape(RoundedPolygon(4, rounding = CornerRounding(0.4f)))
    val Cookie8Shape = RoundedPolygonShape(RoundedPolygon(8, rounding = CornerRounding(0.4f)))
    val Cookie12Shape = RoundedPolygonShape(RoundedPolygon(12, rounding = CornerRounding(0.4f)))


    // Star/Burst shapes
    val Burst8Shape = RoundedPolygonShape(RoundedPolygon.star(8, innerRadius = 0.8f, radius = 1f))
    val Boom20Shape = RoundedPolygonShape(RoundedPolygon.star(20, innerRadius = 0.8f, radius = 1f))
    val Flower12Shape = RoundedPolygonShape(RoundedPolygon.star(12, innerRadius = 0.8f, radius = 1f, rounding = CornerRounding(0.2f)))

    // Default rounded shapes for easy access
    val CircleShape = RoundedCornerShape(50)


    val funShapes = listOf(
        OctagonShape,
        Cookie4Shape,
        Cookie8Shape,
        Cookie12Shape,
        Burst8Shape,
        Boom20Shape,
        Flower12Shape,
        CircleShape,
    )

}