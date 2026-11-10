package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.sakurain.enderio_evolution.common.blockentity.EternalCreativePowerBlockEntity", remap = false)
public abstract class EnderIOEvolutionEternalPowerRemovalMixin extends BlockEntity {
    private EnderIOEvolutionEternalPowerRemovalMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void ftbevo$removeEternalPower(CallbackInfo ci) {
        if (level != null && !level.isClientSide()) {
            level.removeBlock(worldPosition, false);
        }
        ci.cancel();
    }
}
