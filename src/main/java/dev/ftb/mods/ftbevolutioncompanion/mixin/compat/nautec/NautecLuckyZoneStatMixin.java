package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.nautec;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.breakinblocks.nautec.content.entities.NautecFishingHook", remap = false)
public abstract class NautecLuckyZoneStatMixin {
    @Shadow
    private boolean minigameSucceeded;

    @Inject(
            method =
                    "awardBonus(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;DDD)V",
            at = @At("HEAD"))
    private void ftbevo$countMinigameWin(
            ServerLevel level, Player owner, ItemStack rod, double x, double y, double z, CallbackInfo ci) {
        if (this.minigameSucceeded && owner instanceof ServerPlayer player) {
            MagicRegistry.award(player, MagicRegistry.LUCKY_ZONE_MINIGAMES_WON);
        }
    }
}
