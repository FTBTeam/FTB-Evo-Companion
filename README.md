# FTB Evolution 2 Companion

The FTB Evolution 2 Companion mod is a bespoke, custom-tailored mod built to work hand-in-hand with the [FTB Evolution 2](https://go.ftb.team/support-modpack) modpack.

## About `'Companion'` mods

FTB Evolution 2 Companion is one of FTB's `'Companion'` mods - custom tailored, bespoke mods designed to work hand-in-hand with a specific FTB Modpack. Although these mods are visible source and released to CurseForge, we **do not** recommend the use of these mods inside other modpacks.

Please feel free to contribute to this project but **always** open an issue first before opening feature specific pull requests.

Companion mods are provided `as is`. If you opt to use this mod inside another modpack we **will not** provide support, and any issues opened regarding problems due to use in another modpack will be closed!

## Custom Attributes

All companion attributes are registered under the `ftb:` namespace, attach to players only, default to `0`, and are granted in-game by Puffish Skills tree nodes (`puffish_skills:attribute` rewards with `add_value`). Every mechanic can be tested without a skill tree via `/attribute @s ftb:<name> base set <value>`.

The attribute stores the final magnitude (a fraction, level count, or point value), and the skill tree decides how much each node adds. Conditions, timings, and stack caps live in the mod config (`skills` section of `ftbevolutioncompanion-common.toml`).

Values applied directly to the player survive death. Vanilla only carries permanent attribute modifiers across a respawn when keepInventory is on, so the mod copies every `ftb:` attribute (base value and modifiers) onto the new player itself.

### Athletics

| Attribute | Range | Effect |
|---|---|---|
| `ftb:extra_jumps` | 0–16 | Number of mid-air jumps. Each one carries you twice as high as a ground jump to make up for the fall you are already in (config `extra_jump_height`). Toggle: J |
| `ftb:wall_climb` | 0–16 | Wall cling while sneaking against a wall; value = seconds of a single hang before you slide. Clinging refills your mid-air jumps and jumping off a cling restarts the hang timer, so you can jump and cling your way up a wall of any height. Toggle: H |
| `ftb:air_dash` | 0–16 | Number of mid-air dashes (G to dash). Toggle: K |
| `ftb:instant_portals` | 0–1 | 1 = no portal transition delay (nether portals act instantly) |

### Ranged / Archer

| Attribute | Range | Effect |
|---|---|---|
| `ftb:arrow_save` | 0–1 | Chance to not consume ammo when firing a bow/crossbow (0.2 = 20%). Works like Infinity for that shot |
| `ftb:multishot` | 0–16 | Number of projectiles fired per shot, so a value of 10 fires 10 arrows. Extra arrows are free. The volley forms an X around your crosshair instead of the vanilla horizontal fan (config `multishot_x_pattern`), sized by `multishot_spread` (default 3 degrees out from center, against vanilla Multishot's 10) |
| `ftb:ramping_shots` | 0–10 | Max stacks of consecutive-hit ramp. Each arrow hit adds a stack; each stack adds +10% arrow damage (config `archer_ramp_per_stack`). Stacks reset when an arrow hits a block or after 10s without a hit |
| `ftb:first_strike` | 0–2 | Bonus arrow damage fraction vs targets at full health (0.2 = +20%) |
| `ftb:bow_durability` | 0–20 | Virtual Unbreaking levels for bows (no enchantment applied; stacks with real Unbreaking) |
| `ftb:crossbow_durability` | 0–20 | Same, for crossbows |
| `ftb:homing_arrows` | 0–1 | 1 = your arrows lock onto the living target you aimed at (closest to your aim within ~25°, up to 48 blocks). An arrow that hits a block instead of the target redirects mid-air toward it, up to 3 times, then flies normally. Applies to bows and crossbows, not tridents |

### Brawler (unarmed = empty main hand)

| Attribute | Range | Effect |
|---|---|---|
| `ftb:unarmed_resistance` | 0–1 | Damage taken multiplied by (1 − value) while unarmed (0.1 = 10% less) |
| `ftb:unarmed_kill_heal` | 0–1 | Unarmed kills heal this fraction of max health |
| `ftb:combo_punch` | 0–2 | Every 3rd consecutive unarmed hit deals +value bonus damage (0.2 = +20%) |
| `ftb:faster_strikes` | 0–1 | Per-stack attack speed bonus; consecutive unarmed hits stack up to 5. Resets on any non-unarmed damage dealt or after 10s. Toggleable |
| `ftb:unarmed_ramp` | 0–1 | Per-stack damage bonus; consecutive unarmed hits stack up to 10, same reset rules. Toggleable |

### Axe (main hand in `#minecraft:axes`)

| Attribute | Range | Effect |
|---|---|---|
| `ftb:axe_frenzy` | 0–2 | Bonus axe damage while below 50% health (config threshold) |
| `ftb:axe_desperation` | 0–10 | Grants this much `apothic_attributes:life_steal` while below 40% health holding an axe. No-ops if Apothic Attributes is absent |
| `ftb:cheat_death` | 0–1 | 1 = fatal hits leave you at 1 HP with brief invulnerability + Resistance V instead of dying. 2-minute cooldown (persists across relogs). Toggleable |
| `ftb:death_blow` | 0–3 | Bonus axe damage vs targets below 25% health |
| `ftb:dual_wield` | 0–2 | Bonus damage while holding axes in both hands |
| `ftb:lightning_strikes` | 0–1 | 1 = every 3rd axe hit calls a lightning strike on the target (5s cooldown). The bolt never burns or damages the player who called it. Toggle keybind: apostrophe |
| `ftb:bloodlust` | 0–100 | Flat HP healed per axe kill (2.0 = 1 heart) |
| `ftb:axe_durability` | 0–20 | Virtual Unbreaking levels for axes |

### Shield (shield item = has `blocks_attacks` component)

| Attribute | Range | Effect |
|---|---|---|
| `ftb:shield_stun` | 0–1 | Chance to apply `ftb:stunned` (1.5s) to an attacker when blocking their melee hit. 5s cooldown between stuns so a crowd cannot be locked down (config `shield_stun_cooldown`) |
| `ftb:shield_recovery` | 0–1 | Heals this fraction of max health every 5s while a shield is in the offhand |
| `ftb:ground_slam` | 0–3 | Falling 4+ blocks damages everything within 4 blocks for value × your fall damage, with knockback. You still take the fall damage |
| `ftb:lights_shield` | 0–1 | 1 = dropping below 25% health while blocking grants Resistance IV for 5s. 30s internal cooldown |
| `ftb:shield_mastery` | 0–20 | While blocking: +2 armor and +0.5 armor toughness per point |
| `ftb:shield_durability` | 0–20 | Virtual Unbreaking levels for shields |
| `ftb:day_damage` | 0–2 | Bonus melee damage fraction while the sun is out |

### Sword (main hand in `#minecraft:swords`)

| Attribute | Range | Effect |
|---|---|---|
| `ftb:backstab` | 0–3 | Bonus sword damage when striking from behind the target |
| `ftb:shadow_step` | 0–1 | 1 = attacking while sneaking teleports you behind the target facing it. Reaches any living target you can see up to 10 blocks away (config `shadow_step_range`), and does nothing if there is no clear space behind the target. From melee range the hit still lands as normal; from further out the swing only carries you there and deals no damage, so extra reach cannot be used to hit and teleport at once. 10s cooldown. Toggleable |
| `ftb:echo_strikes` | 0–1 | Chance a sword hit immediately repeats as a second full strike (with crit sound). The echo ignores the target's invulnerability frames, so both hits land, and each is mitigated by armor separately |
| `ftb:sword_durability` | 0–20 | Virtual Unbreaking levels for swords |
| `ftb:stealth` | 0–1 | Passively reduces mob detection range; 1.0 = mobs effectively cannot see you |
| `ftb:ninja` | 0–60 | Activated skill (V): Invisibility + maximum stealth for this many seconds. 60s cooldown |
| `ftb:shakedown` | 0–1 | Chance sword kills drop one duplicated item |
| `ftb:night_damage` | 0–2 | Bonus sword damage fraction at night |
| `ftb:blademaster` | 0–1 | 1 = sword hits apply `ftb:bleeding`, stacking to 3. Bleed deals 1% of max health per stack every 2s for 6s. Toggleable |

### Athletic / Mining

| Attribute | Range | Effect |
|---|---|---|
| `ftb:armor_durability` | 0–20 | Virtual Unbreaking levels for worn armor |
| `ftb:mining_fortune` | 0–20 | Invisible Fortune levels added to block drops (stacks with the tool's real Fortune) |
| `ftb:pickaxe_durability` | 0–20 | Virtual Unbreaking levels for pickaxes |

### Mob Effects

| Effect | Behavior |
|---|---|
| `ftb:stunned` | −100% movement and attack speed; attacks by the stunned entity are cancelled outright |
| `ftb:bleeding` | Deals `bleed_fraction` (default 1%) of max health × (amplifier + 1) as magic damage every `bleed_interval_ticks` (default 2s) |

### Toggles & Commands

Toggleable skills (Faster Strikes, Flurry, Undying Rage, Storm Caller, Shadow Step, Blademaster) default to on, persist across death, and can be switched with keybinds (most unbound by default, under the "FTB Evolution Skills" category) or `/ftbskills toggle <skill>`.

Skill-tree nodes that need no companion support: Loot Goblin grants `minecraft:luck`, and arrow armor-piercing grants `apothic_attributes:armor_shred` directly.

## Content

### Odd Berry Bush

`ftbevolutioncompanion:odd_berry_bush` is the pack's source of Roots Classic berries. The pack sets Roots' own `berriesDropChance` to `0`, so berries no longer fall out of every leaf block in the game; you gather them from this bush instead.

| Behavior | Detail |
|---|---|
| Growth | Three stages on the vanilla `age` (`AGE_2`) property: sprout, leafy, ripe. Grows in light level 9 or brighter, and bone meal advances a stage |
| Harvesting | Right click a ripe bush for 1–3 berries picked at random from the `rootsclassic:berries` tag (nightshade, blackcurrant, redcurrant, whitecurrant, elderberry). The bush drops back to the leafy stage and ripens again |
| Breaking | A ripe bush broken by hand or tool drops the same 1–3 berries; younger bushes drop nothing |
| Shearing | Drops the bush itself at any stage, so it can be moved and replanted |
| Placement | Survives on any block in `ftbevolutioncompanion:odd_berry_bush_spreadable` (grass, dirt, podzol, moss, mud, farmland by default) |
| Tended state | A bush placed by a player is `tended=true` and, once ripe, spreads to nearby valid blocks the way mushrooms do. Sneak and right click to toggle between tended and wild |
| Worldgen | Ripe, wild bushes in patches through `#ftbevolutioncompanion:has_feature/odd_berry_bush`, at the same rarity and patch size Pantry for Blockheads uses for its grapevines |

The biome tag covers `#minecraft:is_forest` and `#minecraft:is_taiga` (Terralith folds its own forest and taiga biomes into both), plus plains, sunflower plains, meadow, cherry grove, grove, sparse jungle and, optionally, `#terralith:reference/plains`. The plains-side entries matter more than they look: in a Terralith world most surface chunks are plains or Terralith highlands, so a forest-only tag leaves the bush almost unfindable.

Because the block is a `BonemealableBlock` carrying a vanilla `AGE_2` property, Jade's own crop progress provider shows its growth percentage with no Jade plugin or dependency on our side. The item carries a three-line tooltip covering harvesting, shearing and spreading.

Growth rate, spread rate, the nearby-bush cap and the spread toggle are in the `odd_berry_bush` section of `ftbevolutioncompanion-common.toml`. Retargeting worldgen, changing which blocks it spreads onto, or retexturing needs no code: override the biome tag, the block tag or the textures from a pack datapack or resource pack.

## Support

- For **FTB Evolution 2** modpack issues, please go here: https://go.ftb.team/support-modpack
- For **FTB Evolution 2 Companion** mod issues, please go here: https://go.ftb.team/support-mod-issues
- Just got a question? Check out our Discord: https://go.ftb.team/discord

## Licence

All Rights Reserved to Feed The Beast Ltd. Source code is `visible source`, please see our [LICENSE.md](/LICENSE.md) for more information. Any Pull Requests made to this mod must have the CLA (Contributor Licence Agreement) signed and agreed to before the request will be considered.

## Keep up to date

[![FTB Socials](https://cdn.feed-the-beast.com/assets/socials/icons/socials-scaled-cf.webp?ref=curseforge)](https://feed-the-beast.com/links)
