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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

// Represents the four cardinal directions the player can face
enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

/**
 * The main screen for the first-person dungeon crawler.
 * This Composable is now "dumb". It just displays state from
 * the DungeonViewModel and sends events (button clicks) to it.
 *
 * @param userId The logged-in user's ID.
 * @param onStartBattle A callback function to tell MainActivity
 * that we need to switch to the BattleScreen.
 * @param vm The ViewModel that holds all game state and logic.
 */
@Composable
fun DungeonCrawlScreen(
    userId: String?,
    onStartBattle: () -> Unit,
    vm: DungeonViewModel = viewModel() // Get the ViewModel instance
) {
    // Collect all UI state from the ViewModel
    val uiState by vm.uiState.collectAsState()

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
            text = "User: $userId | Pos: ${uiState.playerPos} | Facing: ${uiState.playerFacing}",
            color = Color.White,
            fontSize = 12.sp
        )

        // Center: The "Viewport" (Our future 2D/3D renderer)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.DarkGray)
                .border(2.dp, Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.gameMessage, // <-- Display message from ViewModel
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Middle: The Mini-Map
        MiniMap(
            map = uiState.visibleMap, // <-- Display the "visible" map
            playerPos = uiState.playerPos,
            facing = uiState.playerFacing
        )

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
                onClick = { vm.onTurnLeft() }, // <-- Call ViewModel function
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Turn Left", modifier = Modifier.fillMaxSize())
            }

            // Move Forward Button
            Button(
                // Pass the onStartBattle callback to the ViewModel
                onClick = { vm.onMoveForward(onStartBattle) }, // <-- Call ViewModel function
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.Filled.ArrowUpward, "Move Forward", modifier = Modifier.fillMaxSize())
            }

            // Turn Right Button
            Button(
                onClick = { vm.onTurnRight() }, // <-- Call ViewModel function
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
 * It now renders "UNEXPLORED" tiles as black boxes.
 */
@Composable
private fun MiniMap(map: List<List<Int>>, playerPos: Pair<Int, Int>, facing: Direction) {
    val tileSize = 12.dp // Smaller tiles for a bigger map
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .border(1.dp, Color.Gray)
            .background(Color.Black) // Unexplored area is black
    ) {
        map.forEachIndexed { y, row ->
            Row {
                row.forEachIndexed { x, tile ->
                    val isPlayerPos = (playerPos.first == x && playerPos.second == y)
                    val tileColor = when (tile) {
                        TILE_UNEXPLORED -> Color.Black
                        TILE_WALL -> Color.Gray.copy(alpha = 0.5f)
                        TILE_DOOR -> Color.Yellow.copy(alpha = 0.7f)
                        TILE_ENCOUNTER -> Color.Red.copy(alpha = 0.7f)
                        else -> Color.DarkGray // Floor / Start
                    }

                    Box(
                        modifier = Modifier
                            .size(tileSize)
                            .background(tileColor)
                            .border(0.5.dp, Color.Gray.copy(alpha = 0.2f)), // Faint grid
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlayerPos) {
                            // Draw the player icon
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