package vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class HomeViewModel(
    val launchInPortrait: Boolean = false,
) {
    var uiState by mutableStateOf(HomeUIState(launchInPortrait = launchInPortrait))
        private set
}

data class HomeUIState(
    val launchInPortrait: Boolean = true,
) {
    val width: Dp
    val height: Dp
    val gradientFocus: Float

    init {
        if (launchInPortrait) {
            width = 700.dp
            height = 1000.dp
        } else {
            width = 1860.dp
            height = 1000.dp
        }
        gradientFocus = width.value + 20
    }
}

