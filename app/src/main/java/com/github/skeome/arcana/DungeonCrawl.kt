package com.github.skeome.arcana

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Represents the four cardinal directions the player can face
enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

/**
 * A hard-coded 5x5 dungeon map for our first prototype.
 * 0 = Wall
 * 1 = Hallway
 * 2 = Door
 * 3 = ENCOUNTER TILE  <-- NEW TILE
 * 9 = Player Start
 */
private val dungeonMap = listOf(
    listOf(0, 0, 0, 0, 0),
    listOf(0, 9, 1, 3, 0), // <-- Added '3' for encounter
    listOf(0, 0, 0, 2, 0),
    listOf(0, 1, 1, 1, 0),
    listOf(0, 0, 0, 0, 0)
)

// Helper function to find the starting position (tile '9')
private fun getStartPosition(): Pair<Int, Int> {
    dungeonMap.forEachIndexed { y, row ->
        row.forEachIndexed { x, tile ->
            if (tile == 9) {
                return Pair(x, y)
            }
        }
    }
    return Pair(1, 1) // Fallback
}

/**
 * The main screen for the first-person dungeon crawler.
 * This Composable manages the game state (player position, facing)
 * and displays the viewport, mini-map, and controls.
 *
 * @param userId The logged-in user's ID.
 * @param onStartBattle A new callback function to tell MainActivity
 * that we need to switch to the BattleScreen.
 */
@Composable
fun DungeonCrawlScreen(
    userId: String?,
    onStartBattle: () -> Unit // <-- NEW PARAMETER
) {
    // --- Game State ---
    // The player's current X, Y position on the map
    var playerPos by remember { mutableStateOf(getStartPosition()) }
    // The direction the player is currently facing
    var playerFacing by remember { mutableStateOf(Direction.NORTH) }
    // A message to show in the "viewport"
    var gameMessage by remember { mutableStateOf("You enter the dungeon.") }

    // --- Game Logic ---
    val onTurnLeft = {
        playerFacing = when (playerFacing) {
            Direction.NORTH -> Direction.WEST
            Direction.WEST -> Direction.SOUTH
            Direction.SOUTH -> Direction.EAST
            Direction.EAST -> Direction.NORTH
        }
        gameMessage = "You turn left."
    }

    val onTurnRight = {
        playerFacing = when (playerFacing) {
            Direction.NORTH -> Direction.EAST
            Direction.EAST -> Direction.SOUTH
            Direction.SOUTH -> Direction.WEST
            Direction.WEST -> Direction.NORTH
        }
        gameMessage = "You turn right."
    }

    val onMoveForward = {
        val (currentX, currentY) = playerPos
        val (nextX, nextY) = when (playerFacing) {
            Direction.NORTH -> currentX to currentY - 1
            Direction.SOUTH -> currentX to currentY + 1
            Direction.WEST -> currentX - 1 to currentY
            Direction.EAST -> currentX + 1 to currentY
        }

        // Check for collision with map boundaries or walls
        val nextTile = dungeonMap.getOrNull(nextY)?.getOrNull(nextX)
        when (nextTile) {
            null, 0 -> {
                // Hit a wall or out-of-bounds
                gameMessage = "A cold, damp wall blocks your path."
            }
            1, 9 -> {
                // Moved to a hallway or the start tile
                playerPos = nextX to nextY
                gameMessage = "You walk forward."
            }
            2 -> {
                // Found a door
                playerPos = nextX to nextY
                gameMessage = "You found a door!"
                // TODO: Add logic to go to next level or trigger event
            }
            // *** NEW BATTLE LOGIC ***
            3 -> {
                // Stepped on an encounter tile!
                playerPos = nextX to nextY
                gameMessage = "You are ambushed!"
                // Trigger the callback to switch screens
                onStartBattle()
            }
            // **************************
        }
    }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Classic dungeon crawl aesthetic
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: Status bar (for debugging and player info)
        Text(
            text = "User: $userId | Pos: $playerPos | Facing: $playerFacing",
            color = Color.White,
            fontSize = 12.sp
        )

        // Center: The "Viewport"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.DarkGray)
                .border(2.dp, Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = gameMessage,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Middle: The Mini-Map
        MiniMap(map = dungeonMap, playerPos = playerPos, facing = playerFacing)

        // Bottom: Navigation Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Turn Left Button
            Button(
                onClick = onTurnLeft,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Turn Left", modifier = Modifier.fillMaxSize())
            }

            // Move Forward Button
            Button(
                onClick = onMoveForward,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.Filled.ArrowUpward, "Move Forward", modifier = Modifier.fillMaxSize())
            }

            // Turn Right Button
            Button(
                onClick = onTurnRight,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Turn Right", modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * A Composable that renders the 2D tile-based mini-map.
 */
@Composable
private fun MiniMap(map: List<List<Int>>, playerPos: Pair<Int, Int>, facing: Direction) {
    val tileSize = 24.dp // The size of each tile on the map
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .border(1.dp, Color.Gray)
    ) {
        map.forEachIndexed { y, row ->
            Row {
                row.forEachIndexed { x, tile ->
                    val isPlayerPos = (playerPos.first == x && playerPos.second == y)
                    val tileColor = when (tile) {
                        0 -> Color.Gray.copy(alpha = 0.5f) // Wall
                        2 -> Color.Yellow.copy(alpha = 0.7f) // Door
                        3 -> Color.Red.copy(alpha = 0.7f) // Encounter
                        else -> Color.DarkGray // Hallway / Start
                    }

                    Box(
                        modifier = Modifier
                            .size(tileSize)
                            .background(tileColor)
                            .border(0.5.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlayerPos) {
                            // Draw the player icon, rotated to match their facing direction
                            val rotation = when (facing) {
                                Direction.NORTH -> 0f
                                Direction.EAST -> 90f
                                Direction.SOUTH -> 180f
                                Direction.WEST -> 270f
                            }
                            Icon(
                                Icons.Filled.Navigation,
                                "Player",
                                modifier = Modifier
                                    .fillMaxSize(0.8f)
                                    .rotate(rotation),
                                tint = Color.Cyan
                            )
                        }
                    }
                }
            }
        }
    }
}