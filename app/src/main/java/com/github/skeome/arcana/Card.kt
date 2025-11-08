package com.github.skeome.arcana

/**
 * A blueprint for a Spirit card.
 * This 'data class' holds all the immutable (val) data
 * that defines a specific Spirit.
 */
data class SpiritCard(
    val name: String,
    val element: String,
    val aetherCost: Int,
    val power: Int,
    val health: Int,
    val rulesText: String
)

/**
 * A blueprint for a Spell card.
 * It's simpler and doesn't need power or health.
 */
data class SpellCard(
    val name: String,
    val element: String,
    val aetherCost: Int,
    val rulesText: String
)