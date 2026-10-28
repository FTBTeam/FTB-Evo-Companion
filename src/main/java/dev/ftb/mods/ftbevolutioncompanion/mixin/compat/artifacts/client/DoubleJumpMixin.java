package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.artifacts.client;

import dev.ftb.mods.ftbevolutioncompanion.client.AthleticsClientHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "artifacts.component.ability.DoubleJump", remap = false)
public abstract class DoubleJumpMixin {
    @Inject(method = "jump(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private static void ftbevo$shareExtraJumps(Player player, CallbackInfo ci) {
        if (player.level().isClientSide()) {
            AthleticsClientHandler.onArtifactsDoubleJump(player);
        }
    }
}
