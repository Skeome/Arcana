package com.github.skeome.arcana

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Represents the player's location in the story mode world.
 * Using default values ensures a new player starts at (0, 0).
 */
@IgnoreExtraProperties
data class PlayerLocation(
    val x: Int = 0,
    val y: Int = 0
)

/**
 * Represents a player's account document in the 'playerAccounts' collection.
 * This structure matches your project blueprint.
 *
 * @param username The player's display name.
 * @param digitalInventory A list of card IDs the player owns.
 * @param playerIDCardUID The NFC UID of the player's physical "Black Card" (if they have one).
 * @param hasUnlocked3D Flag for the premium mode In-App Purchase.
 * @param player_location Nested object for story mode coordinates.
 * @param in_battle Flag for the streamer "Second Screen" mode.
 * @param event_log A log of recent game events for the streamer mode.
 */
@IgnoreExtraProperties
data class PlayerAccount(
    val username: String = "New Wizard",
    val digitalInventory: List<String> = listOf("card_001", "card_001", "card_004"), // Starter deck
    val playerIDCardUID: String = "",
    val hasUnlocked3D: Boolean = false,
    val player_location: PlayerLocation = PlayerLocation(),
    val in_battle: Boolean = false,
    val event_log: List<String> = emptyList()
)