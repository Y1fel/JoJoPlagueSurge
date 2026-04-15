JoJoPlagueSurge 1.0.3
==============================

Project Type:
Minecraft Forge 1.20.1 mod

Basic Info:
- Mod ID: jojoplaguesurge
- Mod Name: JoJoPlagueSurge
- Version: 1.0.3
- Author: Y1fel
- Java: 17
- Forge: 47.4.10
- Mappings: Parchment 2023.09.03-1.20.1
- License: MIT

Overview:
This project is a JoJo-themed Forge mod focused on stand entities, skill HUD,
custom combat logic, OZONE house mechanics, and several related entities and
items.

Current Main Content:
- Stand entity system
- Skill HUD with dynamic key display
- DuWang stand
- BlueHawaii stand
- OZONE block and house-effect logic
- DuVillager entity
- Criminal DuVillager entity
- Miracle entity
- TrackingTornado entity
- Bloody Tooth item
- Stand summon discs

Current Key Bindings:
- Z: Skill 1
- X: Skill 2
- C: Skill 3
- N: Summon / recall stand

The HUD displays the player's real bound keys instead of hard-coded text.

1.0.3 Update Notes:
- Fixed the bug where stand contact could incorrectly damage players.
- OZONE skill 1 range is centered on the caster and checks players within a
  16-block radius.
- OZONE skill 1 now follows staged timing:
  immediate Heavy I,
  Injury Outburst I after 10 seconds,
  Bleeding I after 45 seconds.
- OZONE skill 1 Injury Outburst duration updated to 50 seconds.
- OZONE skill 3 Injury Outburst duration updated to 30 seconds.
- OZONE active skills now show persistent blue bold action-bar hints.
- OZONE active status hints can display up to three different ongoing states at
  the same time.
- DuVillager attack damage was reduced to 1 heart.
- Added Criminal DuVillager as a separate derived entity.
- Criminal DuVillager uses the same random appearance pool as normal villagers.
- Criminal DuVillager drops jcraft:sinners_soul on death.
- Added a dedicated Criminal DuVillager spawn egg.
- DuWang skill 2 now grants:
  Solid Shield,
  Resistance II for 15 seconds,
  Regeneration I for 5 seconds.

Current Skill Notes:
- The skill HUD uses 3 hotkey slots globally.
- DuWang currently uses 2 visible skill slots.
- BlueHawaii currently uses 3 visible skill slots.
- OZONE currently uses 2 HUD skill slots.
- OZONE house effect is triggered by placing the block and right-clicking it in
  the world.

Gameplay Notes:
- DuWang skill 1 summons a tracking tornado from the active stand.
- DuWang skill 2 is a self-defense buff skill.
- BlueHawaii skill flow is tied to Bloody Tooth and target locking.
- OZONE supports direct skill casting and a separate house-area effect.
- Criminal DuVillager is separate from normal DuVillager and is intended for
  controlled spawning via its own spawn egg.

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

Optional / Related Notes:
- Some logic will try to use the More Potion Effects mod if it is present.
- Runtime lookup is used for effects such as imprison, heavy,
  injury_outburst, bleeding, and solid_shield.
- This allows the project to be edited even if More Potion Effects is not set
  up as a strict compile-time requirement for every environment.

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

Development Notes:
- Most gameplay logic is split between packet handlers, tick events, and skill
  catalog classes.
- GeckoLib entities use the standard Entity + Model + Renderer structure.
- OZONE is both a placeable block and a skill trigger object.
- The workspace may contain ongoing uncommitted changes during development.

Run Notes:
- Open the project in IntelliJ IDEA and import the Gradle build.
- Generate run configs if needed with:
  gradlew genIntellijRuns
- Runtime config files are under:
  run/config/
