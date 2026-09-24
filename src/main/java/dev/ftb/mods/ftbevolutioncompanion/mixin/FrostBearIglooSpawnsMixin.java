package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.neoforged.neoforge.event.level.ChunkEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.example.frostbear.registry.IglooSpawns", remap = false)
public abstract class FrostBearIglooSpawnsMixin {
    @Inject(method = "onChunkLoad", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$skipIglooGuardian(ChunkEvent.Load event, CallbackInfo ci) {
        ci.cancel();
    }
}
