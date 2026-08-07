# FTB Evolution 2 Companion

The FTB Evolution 2 Companion mod is a bespoke, custom-tailored mod built to work hand-in-hand with the [FTB Evolution 2](https://go.ftb.team/support-modpack) modpack.

## About `'Companion'` mods

FTB Evolution 2 Companion is one of FTB's `'Companion'` mods - custom tailored, bespoke mods designed to work hand-in-hand with a specific FTB Modpack. Although these mods are visible source and released to CurseForge, we **do not** recommend the use of these mods inside other modpacks.

Please feel free to contribute to this project but **always** open an issue first before opening feature specific pull requests.

Companion mods are provided `as is`. If you opt to use this mod inside another modpack we **will not** provide support, and any issues opened regarding problems due to use in another modpack will be closed!

## Custom Attributes

All companion attributes are registered under the `ftb:` namespace, attach to players only, default to `0`, and are granted in-game by Puffish Skills tree nodes (`puffish_skills:attribute` rewards with `add_value`). Every mechanic can be tested without a skill tree via `/attribute @s ftb:<name> base set <value>`.

The attribute stores the final magnitude (a fraction, level count, or point value) — the skill tree decides how much each node adds. Conditions, timings, and stack caps live in the mod config (`skills` section of `ftbevolutioncompanion-common.toml`).

### Athletics

| Attribute | Range | Effect |
|---|---|---|
| `ftb:extra_jumps` | 0–16 | Number of mid-air jumps. Toggle: J |
| `ftb:wall_climb` | 0–16 | Wall cling while sneaking against a wall; value = seconds of cling before sliding. Toggle: H |
| `ftb:air_dash` | 0–16 | Number of mid-air dashes (G to dash). Toggle: K |
| `ftb:instant_portals` | 0–1 | 1 = no portal transition delay (nether portals act instantly) |

### Ranged / Archer

| Attribute | Range | Effect |
|---|---|---|
| `ftb:arrow_save` | 0–1 | Chance to not consume ammo when firing a bow/crossbow (0.2 = 20%). Works like Infinity for that shot |
| `ftb:multishot_chance` | 0–1 | Chance a shot fires 3 projectiles instead of 1; extra arrows are free (vanilla Multishot behavior) |
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
| `ftb:lightning_strikes` | 0–1 | 1 = every 3rd axe hit calls a lightning strike on the target (5s cooldown). Toggle keybind: apostrophe |
| `ftb:bloodlust` | 0–100 | Flat HP healed per axe kill (2.0 = 1 heart) |
| `ftb:axe_durability` | 0–20 | Virtual Unbreaking levels for axes |

### Shield (shield item = has `blocks_attacks` component)

| Attribute | Range | Effect |
|---|---|---|
| `ftb:shield_stun` | 0–1 | Chance to apply `ftb:stunned` (1.5s) to an attacker when blocking their melee hit |
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
| `ftb:shadow_step` | 0–1 | 1 = attacking while sneaking teleports you behind the target facing it, then the hit lands. 10s cooldown. Toggleable |
| `ftb:echo_strikes` | 0–1 | Chance a sword hit deals double damage (with crit sound) |
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

## Support

- For **FTB Evolution 2** modpack issues, please go here: https://go.ftb.team/support-modpack
- For **FTB Evolution 2 Companion** mod issues, please go here: https://go.ftb.team/support-mod-issues
- Just got a question? Check out our Discord: https://go.ftb.team/discord

## Licence

All Rights Reserved to Feed The Beast Ltd. Source code is `visible source`, please see our [LICENSE.md](/LICENSE.md) for more information. Any Pull Requests made to this mod must have the CLA (Contributor Licence Agreement) signed and agreed to before the request will be considered.

## Keep up to date

[![FTB Socials](https://cdn.feed-the-beast.com/assets/socials/icons/socials-scaled-cf.webp?ref=curseforge)](https://feed-the-beast.com/links)
