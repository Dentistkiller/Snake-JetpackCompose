package com.fake.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

data class Point(val x: Int, val y: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGame()
        }
    }
}

@Composable
fun SnakeGame() {
    val gridSize = 20
    val cellSize = 30
    val delayMillis = 150L

    var snake by remember { mutableStateOf(listOf(Point(5, 5))) }
    var direction by remember { mutableStateOf(Point(1, 0)) }
    var food by remember { mutableStateOf(Point(Random.nextInt(gridSize), Random.nextInt(gridSize))) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    LaunchedEffect(snake) {
        if (!isGameOver) {
            delay(delayMillis)
            val newHead = Point(snake.first().x + direction.x, snake.first().y + direction.y)
            val newSnake = listOf(newHead) + snake.dropLast(1)

            if (
                newHead.x !in 0 until gridSize ||
                newHead.y !in 0 until gridSize ||
                newSnake.drop(1).contains(newHead)
            ) {
                isGameOver = true
            } else {
                if (newHead == food) {
                    food = Point(Random.nextInt(gridSize), Random.nextInt(gridSize))
                    snake = listOf(newHead) + snake
                    score += 1
                } else {
                    snake = newSnake
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size((gridSize * cellSize).dp)
                .border(2.dp, Color.Black)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val (dx, dy) = dragAmount
                        if (abs(dx) > abs(dy)) {
                            if (dx > 0 && direction != Point(-5, 0)) direction = Point(1, 0)
                            else if (dx < 0 && direction != Point(1, 0)) direction = Point(-1, 0)
                        } else {
                            if (dy > 0 && direction != Point(0, -1)) direction = Point(0, 1)
                            else if (dy < 0 && direction != Point(0, 1)) direction = Point(0, -1)
                        }
                    }
                }
        ) {
            snake.forEach {
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(it.x * cellSize.toFloat(), it.y * cellSize.toFloat()),
                    size = androidx.compose.ui.geometry.Size(cellSize.toFloat(), cellSize.toFloat())
                )
            }
            drawRect(
                color = Color.Red,
                topLeft = Offset(food.x * cellSize.toFloat(), food.y * cellSize.toFloat()),
                size = androidx.compose.ui.geometry.Size(cellSize.toFloat(), cellSize.toFloat())
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Score: $score", fontSize = 20.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                if (direction != Point(1, 0)) direction = Point(-1, 0)
            }) { Text("←") }

            Spacer(Modifier.width(8.dp))

            Column {
                Button(onClick = {
                    if (direction != Point(0, 1)) direction = Point(0, -1)
                }) { Text("↑") }

                Spacer(Modifier.height(8.dp))

                Button(onClick = {
                    if (direction != Point(0, -1)) direction = Point(0, 1)
                }) { Text("↓") }
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = {
                if (direction != Point(-1, 0)) direction = Point(1, 0)
            }) { Text("→") }
        }

        if (isGameOver) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Game Over!", fontSize = 24.sp, color = Color.Red)

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                snake = listOf(Point(5, 5))
                direction = Point(1, 0)
                food = Point(Random.nextInt(gridSize), Random.nextInt(gridSize))
                isGameOver = false
                score = 0
            }) {
                Text("Restart")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SnakeGamePreview() {
    SnakeGame()
}
