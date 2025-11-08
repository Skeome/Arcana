package com.github.skeome.arcana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.skeome.arcana.ui.theme.ArcanaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- 1. Create Our Card Instances ---
        val fireSpirit = SpiritCard("Fire Spirit", "Fire", 2, 3, 2, "A basic spirit of flame.")
        val waterElemental = SpiritCard("Water Elemental", "Water", 4, 3, 5, "A sturdy elemental.")
        val fireball = SpellCard("Fireball", "Fire", 3, "Deal 4 damage to any target.")
        val earthGolem = SpiritCard("Earth Golem", "Earth", 5, 4, 6, "Has Guard.")
        val heal = SpellCard("Heal", "Water", 1, "Restore 3 health to a Spirit.")
        val windScout = SpiritCard("Wind Scout", "Wind", 1, 1, 1, "Draw a card.")

        // --- 2. Create Our Collections ---

        // A 'listOf' is a read-only list. This is our "Master Deck" blueprint.
        val masterDeckList = listOf(
            fireSpirit, fireSpirit, // We can add multiple copies
            waterElemental,
            fireball, fireball,
            earthGolem,
            heal,
            windScout, windScout
        )

        // A 'mutableListOf' is a list we can change. This is the player's "Deck".
        // We create it by copying the master list.
        val playerDeck: MutableList<Any> = masterDeckList.toMutableList()

        // This is where our game logic would live.
        // '.shuffle()' is a built-in function for lists!
        playerDeck.shuffle()

        // 'mutableListOf' is also perfect for a hand, starting empty.
        val playerHand: MutableList<Any> = mutableListOf()


        // --- 3. Simulate the Game! ---
        println("--- GAME START ---")
        println("Player's deck has ${playerDeck.size} cards.") // .size gives the count
        println("Shuffling deck...")

        // Let's simulate drawing our 7-card starting hand
        println("\n--- DRAWING HAND ---")
        repeat(7) {
            // '.removeFirst()' takes the top card (index 0) and returns it.
            // This is exactly like drawing from a real deck!
            val drawnCard = playerDeck.removeFirst()

            // '.add()' puts the card into our hand
            playerHand.add(drawnCard)
        }

        println("\n--- Player's Hand ---")
        // We can loop through a list
        playerHand.forEach { card ->
            println(card) // This will print the data class info
        }

        println("\n--- Deck After Draw ---")
        println("Player's deck now has ${playerDeck.size} cards.")


        // --- Default UI Code ---
        setContent {
            ArcanaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

// ... (The Greeting and GreetingPreview functions at the bottom remain unchanged) ...
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ArcanaTheme {
        Greeting("Android")
    }
}