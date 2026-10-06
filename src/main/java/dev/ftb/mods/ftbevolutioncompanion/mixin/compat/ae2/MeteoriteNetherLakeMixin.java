package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ae2;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "appeng.worldgen.meteorite.MeteoritePlacer", remap = false)
public abstract class MeteoriteNetherLakeMixin {
    @Shadow
    @Final
    private LevelAccessor level;

    @Inject(method = "placeCraterLake()V", at = @At("HEAD"), cancellable = true)
    private void ftbevo$noNetherCraterLake(CallbackInfo ci) {
        if (this.level instanceof ServerLevelAccessor accessor && accessor.getLevel().dimension() == Level.NETHER) {
            ci.cancel();
        }
    }
}
