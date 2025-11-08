package com.github.skeome.arcana

/**
 * An interface (a contract) that defines what all cards MUST have.
 */
interface Card {
    val name: String
    val element: String
    val aetherCost: Int
    val rulesText: String
}

/**
 * SpiritCard now 'implements' the Card interface.
 * We add the 'override' keyword to show we are
 * fulfilling the contract.
 */
data class SpiritCard(
    override val name: String,
    override val element: String,
    override val aetherCost: Int,
    val power: Int, // This is NOT in the interface, so no 'override'
    val health: Int, // This is NOT in the interface, so no 'override'
    override val rulesText: String
) : Card // The ': Card' part means "it implements the Card interface"

/**
 * SpellCard also implements the Card interface.
 */
data class SpellCard(
    override val name: String,
    override val element: String,
    override val aetherCost: Int,
    override val rulesText: String
) : Card // Also implements Card