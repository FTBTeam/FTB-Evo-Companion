package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(targets = "com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity", remap = false)
public abstract class ConduitBundleBlockEntityMixin {
    @Unique
    private static final Logger ftbevo$LOGGER = LoggerFactory.getLogger("FTBEvoCompanion/ConduitUnloadGuard");

    @WrapOperation(method = "onChunkUnloaded",
            at = @At(value = "INVOKE",
                    target = "Lcom/enderio/enderio/content/conduits/network/ConduitNodeImpl;detach()V"))
    private void ftbevo$guardDetach(@Coerce Object node, Operation<Void> original) {
        if (node != null) {
            original.call(node);
        } else {
            ftbevo$LOGGER.warn("Skipping detach of missing conduit node at {} during chunk unload",
                    ((BlockEntity) (Object) this).getBlockPos());
        }
    }

    @WrapOperation(method = "onChunkUnloaded",
            at = @At(value = "INVOKE",
                    target = "Lcom/enderio/enderio/content/conduits/network/ConduitNetworkSavedData;returnNode(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lcom/enderio/enderio/content/conduits/network/ConduitNodeImpl;)V"))
    private void ftbevo$guardReturnNode(@Coerce Object savedData, Holder<?> conduit, BlockPos pos,
                                        @Coerce Object node, Operation<Void> original) {
        if (node != null) {
            original.call(savedData, conduit, pos, node);
        }
    }
}
