package com.github.skeome.arcana

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

// Tile Types
const val TILE_UNEXPLORED = -1
const val TILE_WALL = 0
const val TILE_FLOOR = 1
const val TILE_DOOR = 2
const val TILE_ENCOUNTER = 3
const val TILE_START = 9

data class DungeonUiState(
    val visibleMap: List<List<Int>> = emptyList(),
    val playerPos: Pair<Int, Int> = 0 to 0,
    val playerFacing: Direction = Direction.NORTH,
    val gameMessage: String = "Loading..."
)

class DungeonViewModel : ViewModel() {

    // This is the "real" map that the game logic uses
    private var _internalMap: List<List<Int>> = emptyList()

    // This holds all the UI state, including the "visible" map
    private val _uiState = MutableStateFlow(DungeonUiState())
    val uiState: StateFlow<DungeonUiState> = _uiState.asStateFlow()

    init {
        generateDungeon(25, 25)
    }

    private fun generateDungeon(width: Int, height: Int) {
        // --- 1. Create a grid full of walls ---
        val map = MutableList(height) { MutableList(width) { TILE_WALL } }

        // --- 2. Run the "Random Walk" algorithm ---
        val random = Random.Default
        var currentX = width / 2
        var currentY = height / 2
        map[currentY][currentX] = TILE_FLOOR // Start point

        val floorTiles = mutableListOf<Pair<Int, Int>>()
        floorTiles.add(currentX to currentY)

        val totalFloorTiles = (width * height * 0.25).toInt() // Carve 25% of the map as floor
        var tilesCarved = 1

        while (tilesCarved < totalFloorTiles) {
            // Move in a random direction
            when (random.nextInt(4)) {
                0 -> currentY = (currentY - 1).coerceIn(1, height - 2) // North
                1 -> currentY = (currentY + 1).coerceIn(1, height - 2) // South
                2 -> currentX = (currentX - 1).coerceIn(1, width - 2)  // West
                3 -> currentX = (currentX + 1).coerceIn(1, width - 2)  // East
            }

            if (map[currentY][currentX] == TILE_WALL) {
                map[currentY][currentX] = TILE_FLOOR
                tilesCarved++
                floorTiles.add(currentX to currentY)
            }
        }

        // --- 3. Place special tiles ---
        val startPos = floorTiles.removeAt(0)
        map[startPos.second][startPos.first] = TILE_START

        val doorPos = floorTiles.removeAt(floorTiles.lastIndex)
        map[doorPos.second][doorPos.first] = TILE_DOOR

        val encounterPos = floorTiles.random()
        map[encounterPos.second][encounterPos.first] = TILE_ENCOUNTER

        // --- 4. Set the maps ---
        _internalMap = map
        val visibleMap = List(height) { List(width) { TILE_UNEXPLORED } }

        // --- 5. Set initial UI State ---
        _uiState.value = DungeonUiState(
            visibleMap = visibleMap,
            playerPos = startPos,
            playerFacing = Direction.NORTH,
            gameMessage = "You enter the dungeon."
        )

        // Reveal the starting area
        updateVisibility(startPos)
    }

    private fun updateVisibility(pos: Pair<Int, Int>) {
        val (x, y) = pos
        _uiState.update { currentState ->
            val newVisibleMap = currentState.visibleMap.map { it.toMutableList() }

            // Reveal a 3x3 area around the player (simple "torchlight")
            for (dy in -1..1) {
                for (dx in -1..1) {
                    val checkY = y + dy
                    val checkX = x + dx
                    if (checkY in _internalMap.indices && checkX in _internalMap[0].indices) {
                        // Copy the "real" tile to the "visible" map
                        newVisibleMap[checkY][checkX] = _internalMap[checkY][checkX]
                    }
                }
            }
            currentState.copy(visibleMap = newVisibleMap)
        }
    }

    fun onTurnLeft() {
        _uiState.update { currentState ->
            val newFacing = when (currentState.playerFacing) {
                Direction.NORTH -> Direction.WEST
                Direction.WEST -> Direction.SOUTH
                Direction.SOUTH -> Direction.EAST
                Direction.EAST -> Direction.NORTH
            }
            currentState.copy(
                playerFacing = newFacing,
                gameMessage = "You turn left."
            )
        }
    }

    fun onTurnRight() {
        _uiState.update { currentState ->
            val newFacing = when (currentState.playerFacing) {
                Direction.NORTH -> Direction.EAST
                Direction.EAST -> Direction.SOUTH
                Direction.SOUTH -> Direction.WEST
                Direction.WEST -> Direction.NORTH
            }
            currentState.copy(
                playerFacing = newFacing,
                gameMessage = "You turn right."
            )
        }
    }

    fun onMoveForward(onStartBattle: () -> Unit) {
        val (currentX, currentY) = _uiState.value.playerPos
        val (nextX, nextY) = when (_uiState.value.playerFacing) {
            Direction.NORTH -> currentX to currentY - 1
            Direction.SOUTH -> currentX to currentY + 1
            Direction.WEST -> currentX - 1 to currentY
            Direction.EAST -> currentX + 1 to currentY
        }

        // Check for collision
        when (_internalMap.getOrNull(nextY)?.getOrNull(nextX)) {
            null, TILE_WALL -> {
                _uiState.update { it.copy(gameMessage = "A cold, damp wall blocks your path.") }
            }
            TILE_FLOOR, TILE_START -> {
                _uiState.update { it.copy(playerPos = nextX to nextY, gameMessage = "You walk forward.") }
                updateVisibility(nextX to nextY)
            }
            TILE_DOOR -> {
                _uiState.update { it.copy(playerPos = nextX to nextY, gameMessage = "You found the exit!") }
                updateVisibility(nextX to nextY)
                // TODO: Regenerate dungeon or go to next level
            }
            TILE_ENCOUNTER -> {
                _uiState.update { it.copy(playerPos = nextX to nextY, gameMessage = "You are ambushed!") }
                updateVisibility(nextX to nextY)
                onStartBattle()
            }
        }
    }
}