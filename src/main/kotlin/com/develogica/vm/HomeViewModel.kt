package com.develogica.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.develogica.util.Config

class HomeViewModel(
    config: Config,
) {
    var uiState by mutableStateOf(
        HomeUIState(
            launchInPortrait = config.launchInPortrait,
            width = config.width,
            height = config.height
        )
    )
        private set
}

data class HomeUIState(
    val launchInPortrait: Boolean,
    val width: Int,
    val height: Int,
) {
    val gradientFocus: Float = width + 20f
}
