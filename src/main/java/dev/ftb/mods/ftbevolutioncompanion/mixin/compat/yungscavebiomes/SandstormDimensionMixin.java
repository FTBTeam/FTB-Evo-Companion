package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.yungscavebiomes;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.yungnickyoung.minecraft.yungscavebiomes.sandstorm.SandstormServerData", remap = false)
public abstract class SandstormDimensionMixin {
    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;)V", at = @At("HEAD"), cancellable = true)
    private void ftbevo$onlyVanillaDimensions(ServerLevel level, CallbackInfo ci) {
        ResourceKey<Level> dimension = level.dimension();
        if (dimension != Level.OVERWORLD && dimension != Level.NETHER && dimension != Level.END) {
            ci.cancel();
        }
    }
}
