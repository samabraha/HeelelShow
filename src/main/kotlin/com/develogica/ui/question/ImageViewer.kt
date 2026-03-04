package com.develogica.ui.question

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ImageView(
    imagePath: String?,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Vertical
) {
    key(imagePath) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = if (orientation == Orientation.Vertical) {
                modifier.heightIn(max = 400.dp)
            } else modifier
        ) {
            AsyncImage(
                model = imagePath,
                contentDescription = null,
            )
        }
    }
}
