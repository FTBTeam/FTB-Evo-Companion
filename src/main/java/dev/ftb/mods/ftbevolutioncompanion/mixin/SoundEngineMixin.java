package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Shadow @Final private List<TickableSoundInstance> tickingSounds;

    @Inject(
        method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ftbevolutioncompanion$skipMutedSilentSound(SoundInstance sound, CallbackInfo ci) {
        if (sound.canStartSilent()
                && Minecraft.getInstance().options.getSoundSourceVolume(sound.getSource()) <= 0.0F) {
            ci.cancel();
        }
    }

    @Inject(
        method = "tickNonPaused()V",
        at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V", ordinal = 0)
    )
    private void ftbevolutioncompanion$pruneTickingOnSilentRemoval(
            CallbackInfo ci,
            @Local SoundInstance sound) {
        if (sound instanceof TickableSoundInstance ticking) {
            this.tickingSounds.remove(ticking);
        }
    }
}
