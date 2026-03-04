package com.develogica.util

import kotlinx.serialization.Serializable
import kotlin.io.path.Path
import kotlin.io.path.pathString

@Serializable
data class Config(
    var launchInPortrait: Boolean = false,
    var sourceDir: String = Path(develogicaPath, "tig_qz.db").pathString,
    var imageRoot: String = "D:/Develogica/Zara_Images/",
    var width: Int = 1440,
    var height: Int = 720
) {
    companion object {
        val develogicaPath: String = Path(System.getProperty("user.home"), "Develogica").pathString

    }
}