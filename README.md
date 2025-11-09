# Arcana: A Digital-First Card RPG

Welcome to the official repository for Arcana, a "Digital-First, Physical-Optional" collectible card game being built for Android.

This project combines classic TCG/RPG mechanics with modern technology, allowing players to build their collection digitally or bridge the gap with real-world, NFC-enabled physical cards.

## Core Features

* Digital-First, Physical-Optional: The game is designed to be 100% playable with a fully digital collection. Physical NFC cards are a premium, optional add-on for collectors and for a unique, tactile local multiplayer experience.

* Dual Rendering Modes: A single account and game logic powers two different visual experiences:

* Free 2D Mode: A fast, accessible 2D renderer with pixel-art-style sprites.

* Premium 3D Mode: A high-fidelity 3D renderer with full-body models and dynamic battle arenas (unlocked via in-app purchase).

* Firebase Backend: All game data, player accounts, and card definitions are handled in real-time by Firebase, allowing for instant card updates and a seamless cross-platform experience.

* NFC "UID-as-Key" System: Our system uses read-only NFC tags. The app reads a card's unique serial number (UID) and looks up its data in Firebase. This means physical cards can't be "hacked" and can be "patched" or updated via the database at any time.

* Multiple Game Modes:

* * Story/Campaign: A lore-rich, open-world single-player experience.

* * Dungeon Crawl: A generative, action-focused mode for quick play.

* * Online Multiplayer: 1v1 up to 4v4 battles.

* * Local Multiplayer: Play with friends in the same room, with an option to use physical NFC cards to play.

## Technology Stack

* Client: Native Android (Kotlin & Jetpack Compose)

* Backend: Firebase (Firestore, Authentication, Storage)

* Package Name: com.github.skeome.arcana

## Current Status

The project is in the early stages of development. The core data structures for the game logic are being built in Kotlin.

## Current Milestones:

[X] Project Setup in Android Studio (API 21+, Kotlin DSL)

[X] GitHub Repository Initialized

[X] Core Data Blueprints (data class for Spirit/Spell cards)

[X] Deck/Hand Simulation (MutableList<Card>)

[X] Core Game Logic (when statements for card types)

## Next Steps:

* Building the first Jetpack Compose UI screens for the game.
