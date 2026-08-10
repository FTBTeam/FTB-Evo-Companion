package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public abstract class InvalidBlockEntityLoadGuardMixin {

    @Unique
    private static final Logger ftbevolutioncompanion$LOGGER = LogUtils.getLogger();

    @WrapMethod(method = "loadStatic")
    private static BlockEntity ftbevolutioncompanion$skipInvalidBlockEntity(BlockPos pos, BlockState state, CompoundTag tag, HolderLookup.Provider registries, Operation<BlockEntity> original) {
        try {
            return original.call(pos, state, tag, registries);
        } catch (Throwable t) {
            ftbevolutioncompanion$LOGGER.warn("Dropping invalid block entity at {} (block {}) on load: {}", pos, state, t.toString());
            return null;
        }
    }
}
