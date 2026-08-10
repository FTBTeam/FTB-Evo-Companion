package dev.ftb.mods.ftbevolutioncompanion.compat.elevatorid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class ShipElevators {
    public static final String MOD_ID = "elevatorid";

    public static final int ACTIVATION_RANGE = 6;
    public static final int RANGE = 384;
    public static final boolean SAME_COLOR = false;

    private static final String BLOCK_PREFIX = "elevator_";

    private ShipElevators() {}

    @Nullable
    public static DyeColor colorOf(BlockState state) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!MOD_ID.equals(key.getNamespace()) || !key.getPath().startsWith(BLOCK_PREFIX)) {
            return null;
        }
        return DyeColor.byName(key.getPath().substring(BLOCK_PREFIX.length()), null);
    }

    public static boolean isElevator(BlockState state) {
        return colorOf(state) != null;
    }

    public static boolean hasHeadroom(BlockGetter level, BlockPos pos) {
        BlockPos above = pos.above();
        return !level.getBlockState(above).isSuffocating(level, above);
    }

    public static boolean colorsMatch(BlockState from, BlockState to) {
        return !SAME_COLOR || colorOf(from) == colorOf(to);
    }
}
