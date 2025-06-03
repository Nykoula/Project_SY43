package com.example.project_sy43.ui.theme.main_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.example.project_sy43.R

@Composable
fun FlappyBirdGame() {
    var birdY by remember { mutableStateOf(0f) }
    var birdX by remember { mutableStateOf(100f) } // Position X de l'oiseau
    var gameStarted by remember { mutableStateOf(false) }
    val birdSpeed = 15
    var gravity = 0.5f
    var birdVelocity = 0f
    val pipeGaps = remember { mutableListOf<Float>() }
    val pipePositions = remember { mutableListOf<Float>() }
    val pipeWidth = 60.dp
    val pipeGap = 150.dp
    val pipeSpacing = 200.dp

    LaunchedEffect(Unit) {
        while (true) {
            if (gameStarted) {
                birdVelocity += gravity
                birdY += birdVelocity
                birdX += 1f // Faire avancer l'oiseau vers la droite

                if (pipeGaps.size < 3) {
                    pipeGaps.add(Random.nextFloat() * 300)
                    pipePositions.add(400f)
                }

                for (i in pipePositions.indices) {
                    pipePositions[i] -= 2
                }

                if (pipePositions.isNotEmpty() && pipePositions[0] < -pipeWidth.value) {
                    pipePositions.removeAt(0)
                    pipeGaps.removeAt(0)
                }
            }
            delay(16) // Approximation de 60 FPS
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    if (!gameStarted) {
                        gameStarted = true
                    }
                    birdVelocity = -birdSpeed.toFloat()
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (!gameStarted) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Écran de démarrage
            }
        } else {
            // Oiseau
            Image(
                painter = painterResource(id = R.drawable.bird),
                contentDescription = "Bird",
                modifier = Modifier
                    .size(50.dp)
                    .offset { IntOffset(birdX.toInt(), birdY.toInt()) }
            )

            // Tuyaux
            pipePositions.forEachIndexed { index, position ->
                val gap = pipeGaps[index]
                Box(
                    modifier = Modifier
                        .width(pipeWidth)
                        .height(with(LocalDensity.current) { gap.toDp() + pipeGap })
                        .offset { IntOffset(position.toInt(), 0) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pipe),
                        contentDescription = "Pipe Top",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(gap.dp)
                            .rotate(180f),
                        contentScale = ContentScale.FillHeight
                    )
                    Spacer(modifier = Modifier.height(pipeGap))
                    Image(
                        painter = painterResource(id = R.drawable.pipe),
                        contentDescription = "Pipe Bottom",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
