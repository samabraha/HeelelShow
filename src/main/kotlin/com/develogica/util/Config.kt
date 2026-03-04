package com.develogica.util

import kotlinx.serialization.Serializable
import kotlin.io.path.Path
import kotlin.io.path.pathString

@Serializable
data class Config(
    var launchInPortrait: Boolean = false,
    var sourceDir: String = Path(develogicaPath, "tig_qz.db").pathString,
    var width: Float = 1440f,
    var height: Float = 1920f
) {
    companion object {
        val develogicaPath: String = Path(System.getProperty("user.home"), "Develogica").pathString

    }
}