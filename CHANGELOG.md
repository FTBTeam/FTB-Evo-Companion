# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.1.2.0]

### Added

- Challenge Board: a task-screen-style leaderboard billboard, any size up to 9 x 9, that shows one of the top teams and their progress through the Pyramid Challenge quest chapter, re-ranked every minute
- FTB Skyline: a machine you pipe resources into to complete Pyramid Challenge quests
- `/givehat <targets> <hat|random>` (permission level 2) unlocks a Hats (Classic) hat, or a random one the player does not have yet

### Fixed

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
- Mystical Agriculture's Soul Extraction page in JEI giving no way to reach the empty Soul Jar's recipe (BlakeBr0/MysticalAgriculture#882)

### Removed
### Changed

- Ice and Fire dragons, cyclopes, trolls and death worms no longer break or burn blocks inside claimed chunks, unless the claim allows mob griefing
- Frost Bears no longer spawn as igloo guardians
- The Fabricator holds 100,000,000 FE, up from 1,000,000
- The Evolution Pyramid is now the FTB Skyline; ones already placed or carried convert automatically
- Updated the FTB Skyline model and textures, with collision matching the new model
