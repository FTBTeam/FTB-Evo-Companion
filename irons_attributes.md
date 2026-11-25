# Iron's Artifice Gun Attributes

Player attributes added by FTB Evolution Companion for Iron's Artifice guns. All are in the `ftb:` namespace, default to 0, and are registered on players only. The skill trees grant them; the mod turns them into changes to the gun's shot profile.

Registered in `gunnery/GunneryRegistry.java`. Applied in `gunnery/GunneryHooks.java`, except Quickdraw, which is a mixin on the Cowboy Hat (`mixin/compat/artifice/CowboyHatQuickdrawMixin.java`).

## How the values apply

Iron's Artifice computes each shot value as `(base + sum of ADD modifiers) * (1 + a) * (1 + b) * ...` for every multiplier.

- **Multiplier** attributes add a `MULTIPLY_TOTAL` modifier of `+value` (the stat goes up) or `-value` (the stat goes down). They stack multiplicatively with gun modifiers like Hair Trigger or Gun Oil.
- **Flat** attributes add an `ADD` modifier. Piercing, Ricochet and Ramping are rounded down to whole numbers first, so 0.9 does nothing.

All of these are applied during `ComposeShotEvent` at HIGH priority, except Extra Bullet, which rolls in `GunShootEvent.Pre`.

## Summary

| Attribute | Name | Max | Type | Effect at value `x` |
|---|---|---|---|---|
| `ftb:gun_damage` | Gun Damage | 2.0 | Multiplier | Bullet damage × (1 + x) |
| `ftb:gun_fire_rate` | Gun Fire Rate | 1.0 | Multiplier | Fire rate × (1 + x) |
| `ftb:gun_reload_speed` | Gun Reload Speed | 2.0 | Multiplier | Reload speed × (1 + x) |
| `ftb:gun_bullet_speed` | Bullet Speed | 2.0 | Multiplier | Muzzle velocity × (1 + x) |
| `ftb:gun_accuracy` | Gun Accuracy | 0.9 | Multiplier | Spread × (1 - x) |
| `ftb:gun_ammo_save` | Ammo Save Chance | 0.9 | Multiplier | Ammo consume chance × (1 - x) | <- Keep FTB
| `ftb:gun_extra_bullet` | Extra Bullet Chance | 1.0 | Chance | `x` chance per shot of one extra projectile | <- Keep FTB
| `ftb:gun_knockback` | Bullet Knockback | 5.0 | Flat | +x knockback on hit |
| `ftb:gun_seeking` | Bullet Seeking | 1.0 | Flat | +x homing strength | <- Keep FTB
| `ftb:gun_ramping` | Ramping Fire | 5.0 | Flat (whole) | +floor(x) Mechanical Accelerator levels |
| `ftb:gun_quickdraw` | Quickdraw | 0.9 | Reduction | Cowboy Hat reload cooldown × (1 - x) | <- Keep FTB
| `ftb:gun_dead_eye` | Dead Eye | 3.0 | Bonus | Tricorne full-magazine bonus raised from +25% to +(25% + x) | <- Keep FTB
| `ftb:gun_piercing` | Bullet Piercing | 5.0 | Flat (whole) | +floor(x) entities pierced |
| `ftb:gun_ricochet` | Bullet Ricochet | 5.0 | Flat (whole) | +floor(x) block bounces | <- Keep FTB
| `ftb:gun_recoil_control` | Recoil Control | 0.9 | Multiplier | Camera recoil × (1 - x) |
| `ftb:gun_air_accuracy` | Airborne Accuracy | 0.9 | Multiplier | Airborne spread penalty × (1 - x) |

## Details

### Gun Damage (`ftb:gun_damage`)

Multiplies the shot's `damage` component by `1 + x`. At the 2.0 cap a gun does triple damage. Damage is shared across all projectiles in a shot, so this scales shotguns and single-shot guns alike.

### Gun Fire Rate (`ftb:gun_fire_rate`)

Multiplies `fire_rate` by `1 + x`. The gun's delay between shots is `fire_delay / fire_rate`, so 1.0 (the cap) halves the delay. The shooting animation also speeds up a little, since Artifice plays it at `(fire_rate + 2) / 3`.

### Gun Reload Speed (`ftb:gun_reload_speed`)

Multiplies `reload_speed_multiplier` by `1 + x`. Reload time is `reload_ticks / multiplier`, so 1.0 halves it and the 2.0 cap cuts it to a third. The reload time shown in the gun tooltip reflects this.

### Bullet Speed (`ftb:gun_bullet_speed`)

Multiplies `bullet_speed` by `1 + x`. Faster bullets drop less and reach targets sooner. Artifice also scales bullet damage by current speed, reaching full damage at 2 blocks per tick, so a faster bullet keeps full damage over a longer distance.

### Gun Accuracy (`ftb:gun_accuracy`)

Multiplies `spread` by `1 - x`. At the 0.9 cap spread is 10% of normal. Crouching, moving and airborne penalties are applied on top of this reduced value.

### Ammo Save Chance (`ftb:gun_ammo_save`)

Multiplies `ammo_consume_chance` (base 1.0) by `1 - x`. Each time a shot would use ammo, Artifice rolls against that chance and refunds the round if the roll fails, with the "refunded ammo" message and sound. At 0.9 about 90% of rounds are free. This goes through the same check the Enchanted Bullet modifier uses, which fires on every gun, so it does not need that modifier. The two stack: Enchanted Bullet (-12.5%) plus 0.5 Ammo Save gives a consume chance of 0.875 × 0.5 = 0.4375.

### Extra Bullet Chance (`ftb:gun_extra_bullet`)

Rolled once per shot before firing. On success the shot gets one more projectile, and damage is raised by `(n + 1) / n` so every projectile still does its normal per-projectile damage. A single-shot gun fires two full bullets; a 6-pellet shotgun fires 7 full pellets. It does not use extra ammo. At 1.0 it always triggers.

### Bullet Knockback (`ftb:gun_knockback`)

Adds `x` to the shot's `knockback`, applied to living targets on hit in the bullet's direction of travel, using vanilla knockback strength. Most guns start at 0, so any value gives knockback where there was none.

### Bullet Seeking (`ftb:gun_seeking`)

Adds `x` to `seeking`, the same value the Seeking modifier sets. The bullet looks for the closest target in front of it (roughly within 80 degrees of its path, up to 40 blocks ahead) with line of sight, within `7 × (1 + strength)` blocks of its line, and steers toward it. Strength is clamped to 1.0, which is also the cap, giving a 14 block search radius.

### Ramping Fire (`ftb:gun_ramping`)

Adds `floor(x)` levels of `accelerating`, the Mechanical Accelerator mechanic. Each level gives +5% damage for every shot fired in the last second (20 ticks). At the 5 cap each recent shot adds +25%, so the fifth shot in a second does +100%. Stacks with an actual Mechanical Accelerator modifier on the gun.

### Quickdraw (`ftb:gun_quickdraw`)

Only matters with the Cowboy Hat worn. The hat instantly refills the magazine on a bullet kill, then goes on a 5 second (100 tick) cooldown. Quickdraw cuts that cooldown by fraction `x`, never below 1 tick. At the 0.9 cap it is 0.5 seconds.

### Dead Eye (`ftb:gun_dead_eye`)

Only matters with the Tricorne worn, and only on shots fired from a full magazine, which is when the Tricorne gives its own +25% damage. Dead Eye adds `x` to that bonus instead of multiplying on top of it: at 0.5 a full-magazine shot does +75% instead of +25%, and at the 3.0 cap it does +325%. The code divides by the Tricorne's 1.25 so the result is additive with the hat's bonus.

### Bullet Piercing (`ftb:gun_piercing`)

Adds `floor(x)` to `piercing`. Each point lets the bullet pass through one more entity, hitting each entity once. Stacks with the Steel Core modifier (+2).

### Bullet Ricochet (`ftb:gun_ricochet`)

Adds `floor(x)` to `ricochet`. Each point lets the bullet bounce off one block surface instead of stopping. Stacks with the Trickshot modifier (+3).

### Recoil Control (`ftb:gun_recoil_control`)

Multiplies `camera_recoil_multiplier` by `1 - x`, reducing how far the camera kicks per shot. At the 0.9 cap recoil is 10% of normal. This is camera recoil only; it does not change spread.

### Airborne Accuracy (`ftb:gun_air_accuracy`)

Multiplies `in_air_penalty` (base 1.5, the factor spread is multiplied by while not on the ground) by `1 - x`. At 1/3 the penalty is gone (1.5 × 0.667 = 1.0). Past that the multiplier goes below 1, so airborne shots become more accurate than grounded ones: at the 0.9 cap it is 0.15, meaning spread in the air is 15% of the grounded value. If that is not intended, the cap should be about 0.33, or the result should be clamped to a minimum of 1.0.
