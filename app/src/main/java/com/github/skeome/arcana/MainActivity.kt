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

        // --- 2. Create Our Collections ---
        val masterDeckList: List<Card> = listOf(
            fireSpirit, waterElemental, fireball, earthGolem, fireSpirit, fireball
        )
        val playerDeck: MutableList<Card> = masterDeckList.toMutableList()
        playerDeck.shuffle()
        val playerHand: MutableList<Card> = mutableListOf()

        // --- 3. Simulate Drawing ---
        println("--- GAME START ---")
        repeat(7) {
            // Let's add a check so we don't crash if the deck is empty
            if (playerDeck.isNotEmpty()) {
                val drawnCard = playerDeck.removeFirst()
                playerHand.add(drawnCard)
            }
        }

        println("--- Player's Hand ---")
        playerHand.forEach { card ->
            println("In hand: ${card.name} (Cost: ${card.aetherCost})")
        }

        // --- 4. Simulate PLAYING a card with 'when' ---
        println("\n--- PLAYING FIRST CARD ---")

        // Let's get the first card from our hand
        val cardToPlay = playerHand.first() // .first() just peeks at the first item

        // Here is the 'when' statement.
        // It checks the 'type' of the 'cardToPlay' variable.
        when (cardToPlay) {
            is SpiritCard -> {
                // We're in this block, so Kotlin is now smart
                // It 'smart-casts' cardToPlay to a SpiritCard
                // so we can safely access .power and .health
                println("It's a Spirit! Summoning '${cardToPlay.name}'.")
                println("It has ${cardToPlay.power} Power and ${cardToPlay.health} Health.")
            }
            is SpellCard -> {
                // We're in this block, so Kotlin 'smart-casts'
                // cardToPlay to a SpellCard.
                println("It's a Spell! Casting '${cardToPlay.name}'.")
                println("Effect: ${cardToPlay.rulesText}")
            }
            else -> {
                // A fallback, just in case
                println("It's... something else?")
            }
        }


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