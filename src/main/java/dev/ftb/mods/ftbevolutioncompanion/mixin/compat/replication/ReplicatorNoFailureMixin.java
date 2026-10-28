package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.replication;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.buuz135.replication.block.tile.ReplicatorBlockEntity", remap = false)
public abstract class ReplicatorNoFailureMixin {
    @Inject(method = "getFailureChance()I", at = @At("HEAD"), cancellable = true)
    private void ftbevo$noMotorFailure(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
