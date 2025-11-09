package com.github.skeome.arcana

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // <-- Import viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.skeome.arcana.ui.theme.ArcanaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Represents the primary game state: are we exploring or are we in a fight?
enum class GameState {
    DUNGEON_CRAWL,
    BATTLE
}

class MainActivity : ComponentActivity() {

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val tag = "ArcanaMainActivity"

    // Instantiate the ViewModel.
    // This ViewModel will be kept alive across configuration changes (like screen rotation)
    private val dungeonViewModel: DungeonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        // --- Default UI Code ---
        setContent {
            // This state will hold our userId once we get it
            var userId by remember { mutableStateOf<String?>("Logging in...") }
            // This state holds the entire game's current view
            var gameState by remember { mutableStateOf(GameState.DUNGEON_CRAWL) }

            // This function will be called when the Activity is created
            // It signs in the user and updates the userId state
            signInAndSetupPlayer { uid ->
                userId = uid
            }

            ArcanaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This is our main game state router.
                    when (gameState) {
                        GameState.DUNGEON_CRAWL -> {
                            // Show the dungeon, passing in the single ViewModel instance
                            DungeonCrawlScreen(
                                userId = userId,
                                onStartBattle = {
                                    Log.d(tag, "BATTLE TRIGGERED!")
                                    gameState = GameState.BATTLE
                                },
                                vm = dungeonViewModel // <-- Pass the ViewModel
                            )
                        }
                        GameState.BATTLE -> {
                            // Show the battle screen
                            BattleScreen(
                                onBattleEnd = {
                                    Log.d(tag, "Battle over, returning to dungeon.")
                                    gameState = GameState.DUNGEON_CRAWL
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Signs the user in (anonymously for now) and triggers player account setup.
     */
    private fun signInAndSetupPlayer(onComplete: (String?) -> Unit) {
        // As you noted, auth is working. We'll use signInAnonymously here
        // to ensure we have a user session.
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's ID
                    Log.d(tag, "signInAnonymously:success")
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // This is the "Next Logical Step"!
                        createPlayerAccountIfNotExist(userId)
                    }
                    onComplete(userId) // Update the UI
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInAnonymously:failure", task.exception)
                    onComplete("Login Failed") // Update the UI
                }
            }
    }

    /**
     * This is the "Next Logical Step" from your blueprint.
     * It checks if a 'playerAccounts' document exists for the given userId.
     * If it does NOT exist, it creates one with default values.
     */
    private fun createPlayerAccountIfNotExist(userId: String) {
        // 1. Get a reference to the 'playerAccounts' collection
        val playerDocRef = db.collection("playerAccounts").document(userId)

        // 2. Try to get the document
        playerDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 3. Document ALREADY exists.
                    Log.d(tag, "Welcome back, player! Document already exists for: $userId")
                    // We could load the player data here if we wanted to
                } else {
                    // 4. Document does NOT exist. This is a first-time login.
                    Log.d(tag, "First-time login. Creating new player account for: $userId")

                    // 5. Create the new account object (using the data class)
                    val newAccount = PlayerAccount(
                        username = "New Wizard", // Default username
                        digitalInventory = listOf("card_001", "card_001", "card_004"), // Default starter deck
                        playerIDCardUID = "", // No card linked yet
                        hasUnlocked3D = false, // Not yet purchased
                        player_location = PlayerLocation(0, 0), // Start at origin
                        in_battle = false,
                        event_log = listOf("Welcome to Arcana!")
                    )

                    // 6. Set the new document in Firestore
                    playerDocRef.set(newAccount)
                        .addOnSuccessListener {
                            Log.d(tag, "Successfully created new player account for: $userId")
                        }
                        .addOnFailureListener { e ->
                            Log.w(tag, "Error creating player account for: $userId", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting player document: ", exception)
            }
    }
}