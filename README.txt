JoJoPlagueSurge
==============================

Project Type:
Minecraft Forge 1.20.1 mod

Basic Info:
- Mod ID: jojoplaguesurge
- Mod Name: JoJoPlagueSurge
- Version: 1.0.0
- Author: Y1fel
- Java: 17
- Forge: 47.4.10
- Mappings: Parchment 2023.09.03-1.20.1

Overview:
This project is a JoJo-themed Forge mod focused on stand entities, skill HUD,
custom target logic, and several related entities/blocks.

Current Main Content:
- Stand entity system
- Skill HUD with dynamic key display
- DuWang stand
- BlueHawaii stand
- DuVillager entity
- Miracle entity
- TrackingTornado entity
- OZONE block and item logic
- Bloody Tooth item

Current Skill Notes:
- The skill HUD uses 3 hotkey slots globally and displays the player's actual
  bound keys instead of hard-coded text.
- DuWang currently uses 2 visible skill slots.
- BlueHawaii currently uses 3 visible skill slots.
- OZONE currently uses 2 HUD skill slots, while its house effect is triggered
  by placing the block and right-clicking it in the world.

Project Structure:
- src/main/java/com/Y1fel/JoJoPlagueSurge
  Main mod code
- src/main/java/com/Y1fel/JoJoPlagueSurge/entity
  Entity registration and custom entities
- src/main/java/com/Y1fel/JoJoPlagueSurge/network
  Skill packets and server-side logic
- src/main/java/com/Y1fel/JoJoPlagueSurge/client
  HUD, key mappings, render-side logic
- src/main/java/com/Y1fel/JoJoPlagueSurge/skill
  Skill catalog definitions and HUD icon sources
- src/main/resources/assets/jojoplaguesurge
  Models, textures, lang, blockstates, animations
- src/main/resources/data/jojoplaguesurge
  Loot tables and data assets

Important Dependencies:
- GeckoLib 4.4.9
- MCLib 20
- Architectury API
- Trimmed
- JCraft Eyes of Ender
- AzureLib
- Cloth Config
- Player Animator
- TerraBlender

Optional/Related Notes:
- Some imprison-style skill logic will try to use the More Potion Effects mod
  if it is present. The lookup is runtime-based, so the project can still be
  edited without that mod being enabled in build.gradle.

Development Notes:
- Most gameplay logic is split between packet handlers, tick events, and skill
  catalog classes.
- GeckoLib entities use the standard Entity + Model + Renderer structure.
- OZONE is both a placeable block and a skill trigger object.

Run Notes:
- Open the project in IntelliJ IDEA and import the Gradle build.
- Generate run configs if needed with:
  gradlew genIntellijRuns
- Runtime config files are under:
  run/config/

License:
MIT
