# Changelog

## [26.1.2.12]

### Fixed

- Crash on startup when Ars Magica Legacy is not installed
- Warnings and errors in the log when Thaumaturge is not installed

## [26.1.2.11]

### Changed

- The XyCraft Flare Rod no longer needs XyCraft's experimental features, so new worlds no longer have them turned on and no longer show the experimental settings warning

### Fixed

- XyCraft Flares and the beams between them not showing with an Iris shaderpack on

## [26.1.2.9]

### Fixed

- Client crash when an Ars Magica Legacy altar uses up a dropped ingredient during spell crafting (FTBTesting/Testing-Issues#4627)
- Disconnect when opening a XyCraft tank valve with Sophisticated Inventory Interactions installed

## [26.1.2.8]

### Added

- Shift-clicking a Fabricator input tank empties it, so a wrong fluid no longer gets stuck (FTBTesting/Testing-Issues#4643)

## [26.1.2.7]

### Fixed

- Skill XP from dealing damage dropping the leftover fraction of every hit, so weak hits like shotgun pellets on the Ender Dragon gave no Gunnery XP at all

## [26.1.2.6]

### Removed

- The SG Economy coin display fix, which SG Economy now does itself and which would have crashed the game with the updated mod

## [26.1.2.5]

### Fixed

- Crash on launch with Thaumaturge 1.0.1
- Focus Power skills not boosting Thaumaturge spells, and casting Thaumaturge spells not giving Magic skill XP
- Magical Hephaestus tools not spawning aspect orbs when mining ores

## [26.1.2.4]

### Fixed

- Pipez item pipes not feeding the FTB Skyline

## [26.1.2.3]

### Fixed

- Lootr containers looking up and copying their loot data every tick, adding server lag and memory use
- Ars Magica Legacy resending the Life Ward state of every living entity to clients every tick

## [26.1.2.2]

### Fixed

- LTX Industries recipes in JEI no longer flicker to an empty fluid slot when the recipe accepts more than one fluid
- FTB Skyline bays now each feed their own kind of task in the selected quest, so items go in while an energy or fluid task is selected, and the other way round

## [26.1.2.1]

- Initial release for Evo Companion 2
