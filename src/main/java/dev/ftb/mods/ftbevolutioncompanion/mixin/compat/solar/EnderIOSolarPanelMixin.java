package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.solar;

import dev.ftb.mods.ftbevolutioncompanion.compat.solar.FixedTimeSunlight;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.enderio.enderio.content.machines.solar_panel.SolarPanelBlockEntity", remap = false)
public abstract class EnderIOSolarPanelMixin {
    @Inject(method = "getOutputScale(Lnet/minecraft/world/level/Level;)F", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$fixedTimeFullOutput(Level level, CallbackInfoReturnable<Float> cir) {
        if (FixedTimeSunlight.isLit(level)) {
            cir.setReturnValue(1.0F);
        }
    }
}
