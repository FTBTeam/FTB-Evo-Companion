package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.dynamicresources.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ftb.mods.ftbevolutioncompanion.compat.dynamicresources.ClosedResources;
import java.util.List;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBlockStateModel", remap = false)
public abstract class EnderIOConduitStaleBakerMixin {
    @WrapMethod(
            method =
                    "collectParts(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;"
                            + "Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Ljava/util/List;)V")
    private void ftbevo$skipClosedResources(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            RandomSource random,
            List<BlockStateModelPart> parts,
            Operation<Void> original) {
        int before = parts.size();
        try {
            original.call(level, pos, state, random, parts);
        } catch (RuntimeException e) {
            if (!ClosedResources.causedBy(e)) {
                throw e;
            }
            parts.subList(before, parts.size()).clear();
        }
    }
}
