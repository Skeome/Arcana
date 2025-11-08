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

        // --- Create Instances of Our Cards ---
        // We are using the 'SpiritCard' and 'SpellCard' blueprints
        // from your 'Card.kt' file.

        val fireSpirit = SpiritCard(
            name = "Fire Spirit",
            element = "Fire",
            aetherCost = 2,
            power = 3,
            health = 2,
            rulesText = "A basic spirit of flame."
        )

        val waterElemental = SpiritCard(
            name = "Water Elemental",
            element = "Water",
            aetherCost = 4,
            power = 3,
            health = 5,
            rulesText = "A sturdy elemental of the deep."
        )

        val fireball = SpellCard(
            name = "Fireball",
            element = "Fire",
            aetherCost = 3,
            rulesText = "Deal 4 damage to any target."
        )


        // --- Why this is awesome ---

        // 1. Data classes give you a 'toString()' for free!
        //    Just print the object to see all its data.
        println("--- My New Cards ---")
        println(fireSpirit)
        println(waterElemental)
        println(fireball)

        // 2. You can access individual properties using '.'
        println("\n--- Using The Data ---")
        var wizardHP = 100
        println("Wizard HP: $wizardHP")
        println("Casting 'Fireball' costs ${fireball.aetherCost} Aether...")
        println("It deals 4 damage!")
        wizardHP = wizardHP - 4 // We'd use our function here later
        println("Wizard HP is now: $wizardHP")


        // --- This is all the default UI code ---
        // It just displays "Hello Android!" on the screen.
        // We'll replace this later with our game.
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

// This is just a 'Composable' (UI) function for the default screen
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// This is just for the 'Preview' window in Android Studio
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ArcanaTheme {
        Greeting("Android")
    }
}