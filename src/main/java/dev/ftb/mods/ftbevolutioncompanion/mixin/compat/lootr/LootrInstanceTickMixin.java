package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.lootr;

import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrContainerInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LootrAPI.class, remap = false)
public abstract class LootrInstanceTickMixin {
    @Inject(method = "handleInstanceTick", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$skipIdleTicks(ILootrContainerInstance instance, CallbackInfo ci) {
        if (instance == null) {
            return;
        }
        Level level = instance.getDataLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        if (!LootrAPI.isAnythingDecaying() && !LootrAPI.isAnythingRefreshing()) {
            ci.cancel();
            return;
        }
        if (LootrAPI.getCurrentTicks() % (LootrAPI.getTickDelay() + instance.getRandomOffset()) != 0) {
            ci.cancel();
        }
    }
}
