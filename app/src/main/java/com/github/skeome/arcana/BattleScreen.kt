package com.github.skeome.arcana

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A placeholder screen for the card battle.
 * This is where your 2D GBA-style or 3D Wizard101-style
 * battle renderer would go.
 *
 * @param onBattleEnd A callback function to signal that the battle is over
 * and we should return to the dungeon.
 */
@Composable
fun BattleScreen(onBattleEnd: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer) // A reddish background
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BATTLE!",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "This is where the card game happens.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // "End Battle" button to return to the dungeon
            Button(onClick = onBattleEnd) {
                Text(text = "Win Battle (Return to Dungeon)")
            }
        }
    }
}