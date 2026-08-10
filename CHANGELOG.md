# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [21.1.0]

### Fixed

- Occultism Nature Paste was destroyed instead of taking durability damage when used on a grass block in an Oh The Biomes We've Gone biome, and the same on any small flower because of Utilitarian's flower duplication. Both mods consumed an item from the stack whenever they handled the bone meal, without checking whether the item was bone meal or something that pays with durability. Neither now touches items that have durability, so the paste takes damage as it does on crops and underwater sand (FTBTeam/FTB-Modpack-Issues#13046).

- Servers hanging and shutting down with a watchdog timeout while generating new chunks. An Immersive Engineering loot condition read a block from the world in a way that can force a chunk to load, which deadlocks when it happens during chunk generation. It now reads only already-loaded chunks when off the main thread. Scholar 1.2.5 fixes the case this was reported against, this stops any other mod triggering the same hang (FTBTeam/FTB-Modpack-Issues#13026).

- Being kicked from the world with a "Failed to decode packet" error when a Sophisticated Backpack, storage block, mounted storage or moving storage held too much to fit in one packet. Same fix applied to all four (FTBTeam/FTB-Modpack-Issues#12996).

- Assembling a Dyson Cube Project multiblock onto an airship no longer crashes the game, and no longer keeps crashing every time you load the world afterwards. The multiblock still does not run while on a ship, which is up to the mod author (FTBTeam/FTB-Modpack-Issues#12421, InnovativeOnlineIndustries/Dyson-Cube-Project#35).

- Industrial Foregoing Item Transporters no longer duplicate items when fitted with an Efficiency Addon (FTBTeam/FTB-Modpack-Issues#12413).
