package com.github.skeome.arcana

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Stack
import kotlin.random.Random

// --- Tile Types ---
// Constants are now top-level for easy access from other files
const val TILE_UNEXPLORED = -1
const val TILE_WALL = 0
const val TILE_FLOOR = 1
const val TILE_DOOR = 2
const val TILE_ENCOUNTER = 3
const val TILE_TREASURE = 4 // <-- NEW: For 3x3 rooms
const val TILE_START = 9

// --- Map Dimensions ---
// Use odd numbers for maze generation to ensure corridors
private const val MAP_WIDTH = 25
private const val MAP_HEIGHT = 25

/**
 * Holds all the UI state for the dungeon crawl.
 */
data class DungeonUiState(
    val playerPos: Pair<Int, Int> = 1 to 1,
    val playerFacing: Direction = Direction.NORTH,
    val gameMessage: String = "You enter the dungeon.",
    val visibleMap: List<List<Int>> = List(MAP_HEIGHT) { List(MAP_WIDTH) { TILE_UNEXPLORED } }
)

/**
 * The "brain" of the dungeon crawl.
 * Manages game logic, player movement, and dungeon generation.
 */
class DungeonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DungeonUiState())
    val uiState: StateFlow<DungeonUiState> = _uiState.asStateFlow()

    // The complete, hidden map
    private var internalMap: List<MutableList<Int>> = emptyList()

    init {
        generateNewDungeon()
    }

    /**
     * Generates a new dungeon and resets the player state.
     */
    private fun generateNewDungeon() {
        // 1. Generate the dungeon map using DFS
        internalMap = generateDungeon()

        // 2. Find the TILE_START position placed by the generator
        var startPos = 1 to 1
        for (y in internalMap.indices) {
            for (x in internalMap[y].indices) {
                if (internalMap[y][x] == TILE_START) {
                    startPos = x to y
                    break
                }
            }
        }

        // 3. Create the initial "Fog of War" map
        val newVisibleMap = List(MAP_HEIGHT) { MutableList(MAP_WIDTH) { TILE_UNEXPLORED } }

        // 4. Reset the UI state
        _uiState.value = DungeonUiState(
            playerPos = startPos,
            playerFacing = Direction.NORTH, // Default facing direction
            gameMessage = "You enter a new dungeon."
        )

        // 5. Reveal the starting area
        revealFogOfWar(startPos.first, startPos.second)
    }

    // --- Dungeon Generation (DFS) ---

    /**
     * Generates a new dungeon map using Randomized DFS for corridors
     * and adds 3x3 treasure rooms at dead ends.
     */
    private fun generateDungeon(): List<MutableList<Int>> {
        // 1. Initialize map full of walls
        val map = MutableList(MAP_HEIGHT) { MutableList(MAP_WIDTH) { TILE_WALL } }

        // --- 2. Carve Maze using Randomized DFS ---
        val stack = Stack<Pair<Int, Int>>()
        // Start at (1, 1) - must be odd numbers
        val startPos = 1 to 1
        map[startPos.second][startPos.first] = TILE_FLOOR
        stack.push(startPos)

        // DFS carving function
        fun carvePassage(cx: Int, cy: Int) {
            map[cy][cx] = TILE_FLOOR
            val neighbors = listOf(
                (cx to cy - 2) to (cx to cy - 1), // North
                (cx to cy + 2) to (cx to cy + 1), // South
                (cx - 2 to cy) to (cx - 1 to cy), // West
                (cx + 2 to cy) to (cx + 1 to cy)  // East
            ).shuffled()

            for ((next, between) in neighbors) {
                val (nx, ny) = next
                if (nx in 1 until MAP_WIDTH - 1 && ny in 1 until MAP_HEIGHT - 1 && map[ny][nx] == TILE_WALL) {
                    map[between.second][between.first] = TILE_FLOOR
                    carvePassage(nx, ny)
                }
            }
        }

        // Start the recursive carve
        carvePassage(startPos.first, startPos.second)

        // --- 3. Find Dead Ends ---
        val deadEnds = mutableListOf<Pair<Int, Int>>()
        for (y in 1 until MAP_HEIGHT - 1) {
            for (x in 1 until MAP_WIDTH - 1) {
                if (map[y][x] == TILE_FLOOR) {
                    var floorNeighbors = 0
                    if (map[y - 1][x] != TILE_WALL) floorNeighbors++ // North
                    if (map[y + 1][x] != TILE_WALL) floorNeighbors++ // South
                    if (map[y][x - 1] != TILE_WALL) floorNeighbors++ // West
                    if (map[y][x + 1] != TILE_WALL) floorNeighbors++ // East

                    if (floorNeighbors == 1) {
                        deadEnds.add(x to y)
                    }
                }
            }
        }
        deadEnds.shuffle()

        // --- 4. Carve 3x3 Treasure Rooms ---
        val roomCount = Random.nextInt(3, 6) // Add 3-5 rooms
        var roomsPlaced = 0
        for (deadEnd in deadEnds) {
            if (roomsPlaced >= roomCount) break

            // Try to place a 3x3 room centered at the tile *behind* the dead end
            val (dx, dy) = deadEnd
            var roomCenter: Pair<Int, Int>? = null

            // Find which way the dead end is facing
            if (map[dy - 1][dx] != TILE_WALL) roomCenter = dx to dy + 1 // Facing North, room is South
            else if (map[dy + 1][dx] != TILE_WALL) roomCenter = dx to dy - 1 // Facing South, room is North
            else if (map[dy][dx - 1] != TILE_WALL) roomCenter = dx + 1 to dy // Facing West, room is East
            else if (map[dy][dx + 1] != TILE_WALL) roomCenter = dx - 1 to dy // Facing East, room is West

            if (roomCenter != null) {
                if (canCarveRoom(map, roomCenter.first, roomCenter.second)) {
                    carveRoom(map, roomCenter.first, roomCenter.second)
                    // Connect the dead end to the room
                    map[dy][dx] = TILE_FLOOR
                    roomsPlaced++
                }
            }
        }

        // --- 5. Place Special Tiles ---
        val floorTiles = mutableListOf<Pair<Int, Int>>()
        val corridorTiles = mutableListOf<Pair<Int, Int>>()
        for (y in 1 until MAP_HEIGHT - 1) {
            for (x in 1 until MAP_WIDTH - 1) {
                if (map[y][x] == TILE_FLOOR) {
                    floorTiles.add(x to y)
                    // Only add to "corridor" list if it's not a treasure room
                    if (map.getOrNull(y-1)?.getOrNull(x-1) != TILE_TREASURE &&
                        map.getOrNull(y+1)?.getOrNull(x+1) != TILE_TREASURE) {
                        corridorTiles.add(x to y)
                    }
                }
            }
        }
        corridorTiles.shuffle()

        // Place Start (re-using the DFS start)
        map[startPos.second][startPos.first] = TILE_START
        corridorTiles.remove(startPos)

        // Place Door
        val doorPos = corridorTiles.removeAt(0)
        map[doorPos.second][doorPos.first] = TILE_DOOR

        // Place Encounter
        val encounterPos = corridorTiles.removeAt(0)
        map[encounterPos.second][encounterPos.first] = TILE_ENCOUNTER

        return map
    }

    /** Helper to check if a 3x3 room can be placed */
    private fun canCarveRoom(map: List<List<Int>>, cx: Int, cy: Int): Boolean {
        for (y in cy - 1..cy + 1) {
            for (x in cx - 1..cx + 1) {
                if (y < 1 || y >= MAP_HEIGHT - 1 || x < 1 || x >= MAP_WIDTH - 1) return false // Out of bounds
                if (map[y][x] != TILE_WALL) return false // Overlaps existing path
            }
        }
        return true
    }

    /** Helper to carve a 3x3 room */
    private fun carveRoom(map: MutableList<MutableList<Int>>, cx: Int, cy: Int) {
        for (y in cy - 1..cy + 1) {
            for (x in cx - 1..cx + 1) {
                map[y][x] = TILE_TREASURE
            }
        }
    }

    // --- Public Actions ---

    fun onTurnLeft() {
        _uiState.update { currentState ->
            val newFacing = when (currentState.playerFacing) {
                Direction.NORTH -> Direction.WEST
                Direction.WEST -> Direction.SOUTH
                Direction.SOUTH -> Direction.EAST
                Direction.EAST -> Direction.NORTH
            }
            currentState.copy(playerFacing = newFacing, gameMessage = "You turn left.")
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
            currentState.copy(playerFacing = newFacing, gameMessage = "You turn right.")
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
        when (internalMap.getOrNull(nextY)?.getOrNull(nextX)) {
            null, TILE_WALL -> {
                _uiState.update { it.copy(gameMessage = "A cold, damp wall blocks your path.") }
            }
            TILE_FLOOR, TILE_START -> {
                _uiState.update { it.copy(playerPos = nextX to nextY, gameMessage = "You walk forward.") }
                revealFogOfWar(nextX, nextY)
            }
            TILE_DOOR -> {
                _uiState.update { it.copy(playerPos = nextX to nextY, gameMessage = "You found the exit!") }
                revealFogOfWar(nextX, nextY)
                generateNewDungeon() // Load next level
            }
            TILE_ENCOUNTER -> {
                _uiState.update { it.copy(playerPos = nextX to nextY, gameMessage = "You are ambushed!") }
                revealFogOfWar(nextX, nextY)
                onStartBattle() // Trigger battle
            }
            TILE_TREASURE -> {
                _uiState.update { it.copy(playerPos = nextX to nextY, gameMessage = "You found a treasure room!") }
                revealFogOfWar(nextX, nextY)
            }
        }
    }

    /**
     * Reveals a 3x3 area on the visibleMap centered on the player's new position.
     */
    private fun revealFogOfWar(px: Int, py: Int) {
        val newVisibleMap = _uiState.value.visibleMap.map { it.toMutableList() }
        for (y in (py - 1)..(py + 1)) {
            for (x in (px - 1)..(px + 1)) {
                if (y in newVisibleMap.indices && x in newVisibleMap[y].indices) {
                    newVisibleMap[y][x] = internalMap[y][x]
                }
            }
        }
        _uiState.update { it.copy(visibleMap = newVisibleMap) }
    }
}
