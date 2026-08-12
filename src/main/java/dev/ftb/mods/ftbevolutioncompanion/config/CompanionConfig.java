package dev.ftb.mods.ftbevolutioncompanion.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CompanionConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue METEORITE_SPACING;
    public static final ModConfigSpec.IntValue METEORITE_SEPARATION;
    public static final ModConfigSpec.BooleanValue NETHER_METEORITE_RELOCATE;
    public static final ModConfigSpec.IntValue NETHER_METEORITE_MIN_Y;
    public static final ModConfigSpec.IntValue NETHER_METEORITE_MAX_Y;
    public static final ModConfigSpec.BooleanValue NETHER_METEORITE_SUPPRESS_CRATER;

    public static final ModConfigSpec.IntValue ODD_BERRY_BUSH_GROWTH_CHANCE;
    public static final ModConfigSpec.BooleanValue ODD_BERRY_BUSH_SPREAD;
    public static final ModConfigSpec.IntValue ODD_BERRY_BUSH_SPREAD_CHANCE;
    public static final ModConfigSpec.IntValue ODD_BERRY_BUSH_MAX_NEARBY;

    public static final ModConfigSpec.DoubleValue AXE_FRENZY_THRESHOLD;
    public static final ModConfigSpec.DoubleValue AXE_LIFESTEAL_THRESHOLD;
    public static final ModConfigSpec.DoubleValue DEATH_BLOW_THRESHOLD;
    public static final ModConfigSpec.DoubleValue LIGHTS_SHIELD_THRESHOLD;
    public static final ModConfigSpec.IntValue CHEAT_DEATH_COOLDOWN;
    public static final ModConfigSpec.IntValue CHEAT_DEATH_INVULN_TICKS;
    public static final ModConfigSpec.IntValue LIGHTNING_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MULTISHOT_SPREAD;
    public static final ModConfigSpec.BooleanValue MULTISHOT_X_PATTERN;
    public static final ModConfigSpec.DoubleValue EXTRA_JUMP_HEIGHT;
    public static final ModConfigSpec.IntValue SHIELD_STUN_COOLDOWN;
    public static final ModConfigSpec.IntValue SHADOW_STEP_COOLDOWN;
    public static final ModConfigSpec.DoubleValue SHADOW_STEP_RANGE;
    public static final ModConfigSpec.IntValue LIGHTS_SHIELD_COOLDOWN;
    public static final ModConfigSpec.IntValue NINJA_COOLDOWN;
    public static final ModConfigSpec.DoubleValue ARCHER_RAMP_PER_STACK;
    public static final ModConfigSpec.IntValue ARCHER_RAMP_TIMEOUT;
    public static final ModConfigSpec.IntValue FASTER_STRIKES_MAX_STACKS;
    public static final ModConfigSpec.IntValue FASTER_STRIKES_TIMEOUT;
    public static final ModConfigSpec.IntValue UNARMED_RAMP_MAX_STACKS;
    public static final ModConfigSpec.IntValue STUN_DURATION_TICKS;
    public static final ModConfigSpec.IntValue SHIELD_HEAL_INTERVAL_TICKS;
    public static final ModConfigSpec.DoubleValue GROUND_SLAM_RADIUS;
    public static final ModConfigSpec.DoubleValue GROUND_SLAM_MIN_FALL;
    public static final ModConfigSpec.IntValue LIGHTS_SHIELD_RESISTANCE_TICKS;
    public static final ModConfigSpec.IntValue LIGHTS_SHIELD_RESISTANCE_AMPLIFIER;
    public static final ModConfigSpec.DoubleValue BLEED_FRACTION;
    public static final ModConfigSpec.IntValue BLEED_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue BLEED_DURATION_TICKS;
    public static final ModConfigSpec.IntValue BLEED_MAX_STACKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Applied Energistics 2 meteorite worldgen.").push("meteorites");

        METEORITE_SPACING = builder
                .comment("Average distance in chunks between meteorites. AE2's own default is 32.")
                .defineInRange("spacing", 48, 1, 4096);

        METEORITE_SEPARATION = builder
                .comment("Minimum distance in chunks between meteorites. Must be less than spacing.")
                .defineInRange("separation", 12, 0, 4095);

        NETHER_METEORITE_RELOCATE = builder
                .comment("Place meteorites in a low Y band near bedrock instead of on the world surface heightmap.",
                        "Without this, Nether meteorites bury themselves in the ceiling because the world-surface",
                        "heightmap returns the roof rather than the walkable floor.")
                .define("relocate_in_nether", true);

        NETHER_METEORITE_MIN_Y = builder
                .comment("Lowest Y the core of a Nether meteorite may be placed at.")
                .defineInRange("nether_min_y", 8, -64, 320);

        NETHER_METEORITE_MAX_Y = builder
                .comment("Highest Y the core of a Nether meteorite may be placed at.",
                        "The Nether lava sea sits at Y 31, so staying below that keeps meteorites buried under it.")
                .defineInRange("nether_max_y", 24, -64, 320);

        NETHER_METEORITE_SUPPRESS_CRATER = builder
                .comment("Skip crater excavation and decay for meteorites in dimensions with a ceiling.",
                        "AE2 clears every non-bedrock block from the impact site up to the dimension height limit,",
                        "which in the Nether would carve a shaft through the lava sea and the ceiling.")
                .define("suppress_crater_in_ceiling_dimensions", true);

        builder.pop();

        builder.comment("Odd Berry Bush, the Roots Classic berry source that replaces berries dropping from leaves.",
                "A bush placed by a player is tended and spreads; right clicking it toggles that off again.").push("odd_berry_bush");

        ODD_BERRY_BUSH_GROWTH_CHANCE = builder
                .comment("A bush advances one growth stage on 1 in this many random ticks, in light level 9 or brighter.",
                        "Higher is slower. Bone meal always advances a stage.")
                .defineInRange("growth_chance", 12, 1, 4096);

        ODD_BERRY_BUSH_SPREAD = builder
                .comment("Allow tended bushes to spread onto nearby blocks in the",
                        "ftbevolutioncompanion:odd_berry_bush_spreadable tag.")
                .define("spread", true);

        ODD_BERRY_BUSH_SPREAD_CHANCE = builder
                .comment("A tended bush attempts to spread on 1 in this many random ticks. Higher is slower.")
                .defineInRange("spread_chance", 25, 1, 4096);

        ODD_BERRY_BUSH_MAX_NEARBY = builder
                .comment("Bushes allowed in the 9x3x9 area around a bush before it stops spreading.")
                .defineInRange("max_nearby", 5, 1, 256);

        builder.pop();

        builder.comment("Skill node mechanics. Magnitudes come from the ftb: attributes granted by the skill tree;",
                "these settings control conditions, timings, and stack caps.").push("skills");

        AXE_FRENZY_THRESHOLD = builder
                .comment("Health fraction the player must be below for the axe frenzy damage bonus.")
                .defineInRange("axe_frenzy_threshold", 0.50, 0.0, 1.0);

        AXE_LIFESTEAL_THRESHOLD = builder
                .comment("Health fraction the player must be below for axe desperation lifesteal.")
                .defineInRange("axe_lifesteal_threshold", 0.40, 0.0, 1.0);

        DEATH_BLOW_THRESHOLD = builder
                .comment("Target health fraction below which death blow bonus damage applies.")
                .defineInRange("death_blow_threshold", 0.25, 0.0, 1.0);

        LIGHTS_SHIELD_THRESHOLD = builder
                .comment("Health fraction below which Light's Shield triggers while blocking.")
                .defineInRange("lights_shield_threshold", 0.25, 0.0, 1.0);

        CHEAT_DEATH_COOLDOWN = builder
                .comment("Cooldown in ticks after cheat death triggers.")
                .defineInRange("cheat_death_cooldown", 2400, 0, 1728000);

        CHEAT_DEATH_INVULN_TICKS = builder
                .comment("Duration in ticks of the resistance granted when cheat death triggers.")
                .defineInRange("cheat_death_invuln_ticks", 60, 0, 12000);

        LIGHTNING_COOLDOWN = builder
                .comment("Cooldown in ticks between lightning strike procs.")
                .defineInRange("lightning_cooldown", 100, 0, 1728000);

        SHADOW_STEP_COOLDOWN = builder
                .comment("Cooldown in ticks between shadow step teleports.")
                .defineInRange("shadow_step_cooldown", 200, 0, 1728000);

        SHADOW_STEP_RANGE = builder
                .comment("Maximum distance in blocks a shadow step can reach a target.")
                .defineInRange("shadow_step_range", 10.0, 1.0, 64.0);

        MULTISHOT_SPREAD = builder
                .comment("Maximum fan half-angle in degrees for multishot volleys. Vanilla multishot uses 10.")
                .defineInRange("multishot_spread", 3.0, 0.0, 45.0);

        MULTISHOT_X_PATTERN = builder
                .comment("Arrange multishot volleys in an X instead of the vanilla horizontal fan.")
                .define("multishot_x_pattern", true);

        EXTRA_JUMP_HEIGHT = builder
                .comment("Height of mid-air extra jumps as a multiple of a normal jump.")
                .defineInRange("extra_jump_height", 2.0, 0.5, 8.0);

        SHIELD_STUN_COOLDOWN = builder
                .comment("Cooldown in ticks between shield stun procs, to stop stun locking.")
                .defineInRange("shield_stun_cooldown", 100, 0, 1728000);

        LIGHTS_SHIELD_COOLDOWN = builder
                .comment("Internal cooldown in ticks for Light's Shield.")
                .defineInRange("lights_shield_cooldown", 600, 0, 1728000);

        NINJA_COOLDOWN = builder
                .comment("Cooldown in ticks for the ninja stealth activation.")
                .defineInRange("ninja_cooldown", 1200, 0, 1728000);

        ARCHER_RAMP_PER_STACK = builder
                .comment("Bonus damage fraction per ramping shots stack.")
                .defineInRange("archer_ramp_per_stack", 0.10, 0.0, 10.0);

        ARCHER_RAMP_TIMEOUT = builder
                .comment("Ticks without an arrow hit before ramping shots stacks reset.")
                .defineInRange("archer_ramp_timeout", 200, 1, 72000);

        FASTER_STRIKES_MAX_STACKS = builder
                .comment("Maximum faster strikes attack speed stacks.")
                .defineInRange("faster_strikes_max_stacks", 5, 1, 100);

        FASTER_STRIKES_TIMEOUT = builder
                .comment("Ticks without an unarmed hit before faster strikes stacks reset.")
                .defineInRange("faster_strikes_timeout", 200, 1, 72000);

        UNARMED_RAMP_MAX_STACKS = builder
                .comment("Maximum unarmed ramp damage stacks.")
                .defineInRange("unarmed_ramp_max_stacks", 10, 1, 100);

        STUN_DURATION_TICKS = builder
                .comment("Duration in ticks of the stun applied by shield bash.")
                .defineInRange("stun_duration_ticks", 30, 1, 12000);

        SHIELD_HEAL_INTERVAL_TICKS = builder
                .comment("Interval in ticks between shield recovery heals.")
                .defineInRange("shield_heal_interval_ticks", 100, 1, 72000);

        GROUND_SLAM_RADIUS = builder
                .comment("Radius in blocks of the ground slam shockwave.")
                .defineInRange("ground_slam_radius", 4.0, 0.5, 32.0);

        GROUND_SLAM_MIN_FALL = builder
                .comment("Minimum fall distance in blocks to trigger ground slam.")
                .defineInRange("ground_slam_min_fall", 4.0, 0.0, 256.0);

        LIGHTS_SHIELD_RESISTANCE_TICKS = builder
                .comment("Duration in ticks of the resistance granted by Light's Shield.")
                .defineInRange("lights_shield_resistance_ticks", 100, 1, 12000);

        LIGHTS_SHIELD_RESISTANCE_AMPLIFIER = builder
                .comment("Amplifier of the resistance granted by Light's Shield (3 = Resistance IV).")
                .defineInRange("lights_shield_resistance_amplifier", 3, 0, 4);

        BLEED_FRACTION = builder
                .comment("Fraction of max health dealt per bleed tick per stack.")
                .defineInRange("bleed_fraction", 0.01, 0.0, 1.0);

        BLEED_INTERVAL_TICKS = builder
                .comment("Ticks between bleed damage ticks.")
                .defineInRange("bleed_interval_ticks", 40, 1, 12000);

        BLEED_DURATION_TICKS = builder
                .comment("Duration in ticks of the bleed effect when applied or refreshed.")
                .defineInRange("bleed_duration_ticks", 120, 1, 72000);

        BLEED_MAX_STACKS = builder
                .comment("Maximum bleed stacks on a single target.")
                .defineInRange("bleed_max_stacks", 3, 1, 100);

        builder.pop();

        SPEC = builder.build();
    }

    private CompanionConfig() {
    }

    public static int netherMinY() {
        return Math.min(NETHER_METEORITE_MIN_Y.get(), NETHER_METEORITE_MAX_Y.get());
    }

    public static int netherMaxY() {
        return Math.max(NETHER_METEORITE_MIN_Y.get(), NETHER_METEORITE_MAX_Y.get());
    }
}
