# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.1.2.0]

### Added

- Challenge Board: a task-screen-style leaderboard billboard, any size up to 9 x 9, that shows one of the top teams and their progress through the Feed The Beast quest chapter, re-ranked every minute
- FTB Skyline: a machine you pipe resources into to complete Feed The Beast quests
- `/givehat <targets> <hat|random>` (permission level 2) unlocks a Hats (Classic) hat, or a random one the player does not have yet
- Feed The Beast items: ten challenge components, the Heart of the Beast and the Beast Trophy, an animated two-block display piece
- Kelp Slurry and Algal Lipid, the items the modpack's Lipophiles bacteria use
- Chat announcements when teams change place on the Feed The Beast leaderboard, hourly by default; `announce_interval_minutes` sets the interval (0 for right away, -1 for off) and `announce_only_on_change` can post the full leaderboard every interval instead
- FTB Fabricator recipes that need a stage show a readable name for it in JEI, on the Fabricator screen and in Jade, taken from a `stage.ftbevolutioncompanion.<stage>` lang key
- Magic skill tree support: 37 new skill attributes that scale Ars Magica, Thaumaturge, Apothic Enchanting, Roots Classic, Witchery, Neo Vitae, Animus, EvilCraft, Occultism and Anima
- Magic statistics for spells cast, rites performed, brews bottled, research completed and affliction levels gained, used as Magic skill XP sources
- Magic mod tooltips and screens show costs, cooldowns and strengths with the player's Magic skills applied
- Gunnery skill support: 12 new skill attributes for Iron's Arms 'n Artifice guns covering damage, fire rate, reload speed, bullet speed, spread, ammo saving, extra bullets, knockback, seeking and ramping fire, plus stronger Cowboy Hat and Tricorne abilities (FTBTesting/Testing-Issues#4487)
- Gunnery skill attributes for bullet piercing, ricochet, recoil control and airborne accuracy (FTBTesting/Testing-Issues#4487)
- Molten fluids and buckets for 58 metals and gems, so Hephaestus can melt them and cast tool parts from them
- Hephaestus tool traits Magical, Magically Jagged, Demonic, Nautical, Dragonfire, Dragonfrost and Dragonstorm
- Hephaestus tool tiers 6 and 7 for Adamantite, Aeternium and Aurichalcum heads

### Fixed

- LTX Industries tanks no longer crash the game when they render fluid with a shader pack on
- Nether portals no longer generate inside the spawn pyramid. A portal lit in the Nether near spawn could build its Overworld side into the pyramid and clear blocks there; it now generates under or beside it instead
- Mob Flow Utilities flow pads flooding nearby players with entity movement packets, which caused connection timeouts around large mob farms (FTBTesting/Testing-Issues#4520)
- Hephaestus tools mining Adamantite, Aeternium and Aurichalcum ore below the tier those ores need, and tier 4 heads mining like wood
- Storm Caller strikes setting fires; the bolt is now cosmetic and deals its damage directly, ignoring the hit cooldown from the axe swing
- Being charmed by an Ice and Fire siren disconnecting the player or clearing their resource packs (FTBTesting/AI_Testing#45)
- Just Dire Things ore scanners and X-ray not showing Aurichalcum ore
- The game crashing when fuel is put into a Mystical Agriculture Soulium Spawner, and fuel not dragging into the fuel slot of other Mystical Agriculture machines (FTBTesting/Testing-Issues#4498, BlakeBr0/MysticalAgriculture#883)
- Dank Auto Sort reordering and merging incoming inventory updates on the client
- Water entering the output tanks of Ender IO and Ender IO Evolution Vats
- Hats equipped by a player not appearing for other players on a server (astryxion/Hats#3)
- Updated Thaumaturge no longer crashes the game on startup, and Magic skills still steady infusions
- The game crashing for players near an Ice and Fire dragon that picks up a mob or player in its mouth (FTBTesting/Testing-Issues#4484)
- Logistics Pipes pipes drop again when broken, along with the modules and upgrades installed in them
- Dank Storage danks no longer lose or duplicate items when pipes and other mods check a transfer before making it
- Powah's config file is read again; every edit used to be replaced by the mod defaults on boot (Technici4n/Powah#305)
- Generated crude oil patches settle on their own instead of waiting for a nearby block update (FTBTesting/Testing-Issues#4229)
- Taking items from a Crafting Station's side inventory gives one stack at a time (FTBTesting/Testing-Issues#4236)
- XP fluid is now the same fluid across every mod that makes it (FTBTesting/Testing-Issues#4204)
- Ender IO's XP machines and tools store experience again (FTBTesting/Testing-Issues#4204)
- GeckoLib glowing textures not glowing with a shaderpack on
- Creative inventory search not responding while Easy NPC is installed alongside Apothic Spawners
- Curios slots disappearing for every player after a `/reload` until the server restarted (TheIllusiveC4/Curios#632)
- Dragging items into Curios slots with JEI cheat mode enabled deletes them because the Curios panel was missing from JEI's GUI exclusions
- The server crashing when an Ender IO Limited Item Filter slot is set above 99 items; filter slots now stop at 99
- Ender IO filter slots duplicating items when Inventory Essentials transfer shortcuts are used on them (Team-EnderIO/EnderIO#1450)
- Mystical Agriculture's Soul Extraction page in JEI giving no way to reach the empty Soul Jar's recipe (BlakeBr0/MysticalAgriculture#882)
- Stellaris Fuel Refinery refusing the Oritech oil its pumpjacks produce
- Building from source failing on machines without Java 25 installed, and CI building with Java 21 (FTBTesting/Testing-Issues#4435)
- Temporary attribute bonuses from items or effects becoming permanent after death (FTBTesting/Testing-Issues#4439)
- Pipes connected to an FTB Skyline that sits across a chunk border no longer stop delivering after a restart
- Draining a Reliquary Hero Medallion a little at a time no longer creates extra XP fluid, and filling it in small amounts no longer loses any (FTBTesting/Testing-Issues#4444)
- Starting without Ice and Fire or Curios shows a missing-mod message instead of crashing, since both are now required (FTBTesting/Testing-Issues#4438)
- Productive Bees bee breeding, produce and conversion recipes show in JEI again
- Power Armor Compressor recipes show in JEI
- Hephaestus Forge Melters and Smeltery Controllers drop the items inside them when broken
- Hephaestus Forge Seared Ladders placed from the top down link up
- Hephaestus Forge Seared Fuel Tank fluid showing through walls
- Hephaestus Forge Melter and Smeltery tooltips showing the wrong ingot and block amounts
- Filling a bucket with Hephaestus Forge Molten Cobalt, Quartz, Diamond, Emerald, Amethyst or Blaze giving a Lava Bucket

### Removed

- The Hephaestus Constantan tool material, and Molten Tungsten with its bucket
- Adamantite, Aeternium and Aurichalcum and their ores, worldgen, tools and armor, which moved to the FTB Armory mod
- The GuideMe guide screen click fix, since GuideMe 26.1.14 fixes it and the old fix stopped the game from starting (AppliedEnergistics/GuideME#105)
- The GuideMe structure preview memory fix, since GuideMe 26.1.14 fixes it and the old fix crashed the game on any page with a 3D scene (AppliedEnergistics/GuideME#106)

### Changed

- The Productive Bees Breeding Chamber picks a new random offspring after every breed, instead of repeating its first pick for the same parents
- Ice and Fire dragons, cyclopes, trolls and death worms no longer break or burn blocks inside claimed chunks, unless the claim allows mob griefing
- Frost Bears no longer spawn as igloo guardians
- The Fabricator holds 100,000,000 FE, up from 1,000,000
- The Evolution Pyramid is now the FTB Skyline; ones already placed or carried convert automatically
- The FTB Fabricator and FTB Skyline have their own working, crafting, launch and liftoff sounds instead of borrowed vanilla ones (FTBTesting/Testing-Issues#4443)
- Updated the FTB Skyline model and textures, with collision matching the new model
- The winged horse armor names in JEI and the FTB Skyline progress numbers use lang keys, so every piece of player-facing text can be translated
- Scythe sweep damage follows the scythe's own attack damage
