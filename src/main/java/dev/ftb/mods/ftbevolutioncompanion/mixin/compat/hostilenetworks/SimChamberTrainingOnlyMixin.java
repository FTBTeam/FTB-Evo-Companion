package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hostilenetworks;

import dev.shadowsoffire.hostilenetworks.tile.SimChamberTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimChamberTileEntity.class, remap = false)
public abstract class SimChamberTrainingOnlyMixin {
    @Shadow
    protected SimChamberTileEntity.SimMode mode;

    @Inject(
            method =
                    "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At("HEAD"))
    private void ftbevo$forceTraining(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (this.mode != SimChamberTileEntity.SimMode.TRAINING) {
            this.mode = SimChamberTileEntity.SimMode.TRAINING;
            ((BlockEntity) (Object) this).setChanged();
        }
    }

    @Inject(
            method = "setSimMode(Ldev/shadowsoffire/hostilenetworks/tile/SimChamberTileEntity$SimMode;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$keepTraining(SimChamberTileEntity.SimMode requested, CallbackInfo ci) {
        if (requested != SimChamberTileEntity.SimMode.TRAINING) {
            ci.cancel();
        }
    }
}
