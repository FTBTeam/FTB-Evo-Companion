package dev.ftb.mods.ftbevolutioncompanion.compat.solar;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public final class FixedTimeSunlight {
    public static final long NOON = 6000L;

    private FixedTimeSunlight() {}

    public static boolean isLit(Level level) {
        DimensionType type = level.dimensionType();
        return type.hasFixedTime() && type.hasSkyLight() && level.getSkyDarken() < 4;
    }

    public static boolean isBrightOutside(Level level, boolean original) {
        return original || isLit(level);
    }
}
