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
            width = config.width.dp,
            height = config.height.dp
        )
    )
        private set
}

data class HomeUIState(
    val launchInPortrait: Boolean,
    val width: Dp,
    val height: Dp,
) {
    val gradientFocus: Float = width.value + 20
}
