package ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import util.ColorUtil
import util.FontUtil
import kotlin.math.ceil
import kotlin.time.Duration

@Composable
fun ProgressBar(
    key: Any?,
    modifier: Modifier = Modifier,
    duration: Duration,
    onFinished: suspend () -> Unit,
    lightColor: Color = ColorUtil.lightColor,
    offGlassColor: Color = ColorUtil.offGlassColor
) {
    val animatedProgress = remember { Animatable(1f) }

    LaunchedEffect(key1 = key) {
        animatedProgress.snapTo(1f)
        animatedProgress.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = duration.inWholeMilliseconds.toInt(),
                easing = LinearEasing
            )
        )
        onFinished()
    }

    Row(
        modifier = modifier.fillMaxWidth().height(36.dp).background(Color.Black.copy(alpha = .5f)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val maxBeads = (2 * duration.inWholeSeconds + 1).toInt()

        Row(
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val visibleBeads = ceil(maxBeads * animatedProgress.value).toInt()
            val offBeadsCount = maxBeads - visibleBeads
            val leftOffBeads = offBeadsCount / 2
            val rightOffBeads = offBeadsCount - leftOffBeads

            for (i in 1..maxBeads) {
                val isOff = i <= leftOffBeads || i > maxBeads - rightOffBeads
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(25.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isOff) offGlassColor else lightColor)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(64.dp)
        ) {
            Text(
                text = String.format("0:%02d", ceil(duration.inWholeSeconds * animatedProgress.value).toInt()),
                fontFamily = FontUtil.bpfFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = lightColor
            )
        }
    }
}
