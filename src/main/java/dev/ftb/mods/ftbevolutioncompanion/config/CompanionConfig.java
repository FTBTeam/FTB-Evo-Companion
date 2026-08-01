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
