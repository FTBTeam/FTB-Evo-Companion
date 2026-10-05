package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.solar;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.compat.solar.FixedTimeSunlight;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "rearth.oritech.block.entity.generators.BigSolarPanelEntity", remap = false)
public abstract class OritechBigSolarPanelMixin {
    @WrapOperation(
            method = {"getProductionRate", "isProducing"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isBrightOutside()Z"))
    private boolean ftbevo$fixedTimeSunlight(Level level, Operation<Boolean> original) {
        return FixedTimeSunlight.isBrightOutside(level, original.call(level));
    }

    @Inject(method = "getAdjustedTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void ftbevo$fixedTimeNoon(CallbackInfoReturnable<Long> cir) {
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level != null && FixedTimeSunlight.isLit(level)) {
            cir.setReturnValue(FixedTimeSunlight.NOON);
        }
    }
}
