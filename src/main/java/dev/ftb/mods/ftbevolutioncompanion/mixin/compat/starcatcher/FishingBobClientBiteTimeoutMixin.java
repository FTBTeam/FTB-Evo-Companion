package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.starcatcher;

import com.wdiscute.starcatcher.bobentity.FishingBobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobEntity.class)
public abstract class FishingBobClientBiteTimeoutMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void ftbevo$holdClientBiteTimer(CallbackInfo ci) {
        FishingBobEntity bob = (FishingBobEntity) (Object) this;
        if (bob.level().isClientSide() && bob.timeBiting > 79) {
            bob.timeBiting = 79;
        }
    }
}
