package com.blackcat.android.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.blackcat.android.ui.theme.AccentPrimary
import com.blackcat.android.ui.theme.PrimaryBlue
import com.blackcat.android.ui.theme.PrimaryGreen
import com.blackcat.android.ui.theme.PrimaryOrange
import com.blackcat.android.ui.theme.PureYellow
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiAnimation(
    isVisible: Boolean,
    onComplete: () -> Unit = {}
) {
    if (!isVisible) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(AccentPrimary, PrimaryBlue, PrimaryGreen, PrimaryOrange, PureYellow)
        List(80) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                velocityX = (Random.nextFloat() - 0.5f) * 0.3f,
                velocityY = Random.nextFloat() * 0.5f + 0.3f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                size = Random.nextFloat() * 8f + 4f,
                color = colors.random()
            )
        }
    }

    LaunchedEffect(isVisible) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
        onComplete()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val t = progress.value
        particles.forEach { particle ->
            val currentX = (particle.x + particle.velocityX * t) * size.width
            val currentY = (particle.y + particle.velocityY * t) * size.height
            val currentRotation = particle.rotation + particle.rotationSpeed * t
            val alpha = (1f - t).coerceIn(0f, 1f)

            if (currentY in 0f..size.height && alpha > 0f) {
                rotate(currentRotation, pivot = Offset(currentX, currentY)) {
                    drawRect(
                        color = particle.color.copy(alpha = alpha),
                        topLeft = Offset(
                            currentX - particle.size / 2,
                            currentY - particle.size / 2
                        ),
                        size = Size(particle.size, particle.size * 0.6f)
                    )
                }
            }
        }
    }
}
