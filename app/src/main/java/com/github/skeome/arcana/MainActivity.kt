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
        // (This part is unchanged)
        val fireSpirit = SpiritCard("Fire Spirit", "Fire", 2, 3, 2, "A basic spirit of flame.")
        val waterElemental = SpiritCard("Water Elemental", "Water", 4, 3, 5, "A sturdy elemental.")
        val fireball = SpellCard("Fireball", "Fire", 3, "Deal 4 damage to any target.")
        val earthGolem = SpiritCard("Earth Golem", "Earth", 5, 4, 6, "Has Guard.")
        val heal = SpellCard("Heal", "Water", 1, "Restore 3 health to a Spirit.")
        val windScout = SpiritCard("Wind Scout", "Wind", 1, 1, 1, "Draw a card.")

        // --- 2. Create Our Collections (NOW TYPE-SAFE!) ---

        // The master list is now a 'List<Card>'
        val masterDeckList: List<Card> = listOf(
            fireSpirit, fireSpirit,
            waterElemental,
            fireball, fireball,
            earthGolem,
            heal,
            windScout, windScout
        )

        // The player deck is now a 'MutableList<Card>'
        val playerDeck: MutableList<Card> = masterDeckList.toMutableList()

        playerDeck.shuffle()

        // The player hand is also a 'MutableList<Card>'
        val playerHand: MutableList<Card> = mutableListOf()


        // --- 3. Simulate the Game! ---
        println("--- GAME START ---")
        println("Player's deck has ${playerDeck.size} cards.")
        println("Shuffling deck...")

        println("\n--- DRAWING HAND ---")
        repeat(7) {
            val drawnCard = playerDeck.removeFirst()
            playerHand.add(drawnCard)
        }

        println("\n--- Player's Hand (Now with types!) ---")
        playerHand.forEach { card ->
            // !! THIS IS THE BIG CHANGE !!
            // We can now access '.name' and '.aetherCost' directly,
            // because Kotlin knows every item in the list is a 'Card'.
            println("Drew: ${card.name} (Cost: ${card.aetherCost})")
        }

        println("\n--- Deck After Draw ---")
        println("Player's deck now has ${playerDeck.size} cards remaining.")


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