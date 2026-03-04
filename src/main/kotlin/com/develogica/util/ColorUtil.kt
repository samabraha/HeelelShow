package com.develogica.util

import androidx.compose.ui.graphics.Color
import kotlin.random.Random


object ColorUtil {
    private val random = Random(System.currentTimeMillis())

    //    val lightColor = Color(100, 255, 200, 255) // For Narrated Videos
    val lightColor = Color(100, 200, 255, 255) // For Silent Videos
    val offGlassColor = Color(5, 15, 25, 255)

    /**
     * Create a random color withing provided margins of randomness for hue, saturation and lightness.
     * */
    fun randomColor(
        hue: Float = (random.nextInt(73) * 5).toFloat(),
        saturation: Float, lightness: Float
    ): Color {
        return Color.hsl(
            hue = hue,
            saturation = saturation,
            lightness = lightness
        )
    }
}

