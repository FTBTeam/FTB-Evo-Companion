package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.anima.SoulMagic;
import dev.anima.Souls;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoulMagic.class, remap = false)
public abstract class AnimaSoulMagicMixin {
    @WrapOperation(
            method = "cast(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ldev/anima/SoulMagic;spend(Lnet/minecraft/world/entity/player/Player;Ldev/anima/Souls;)V"))
    private static void ftbevo$soulThrift(Player player, Souls cost, Operation<Void> original) {
        if (!OtherworldMagic.refundsSouls(player)) {
            original.call(player, cost);
        }
    }

    @ModifyExpressionValue(
            method = "rest(Lnet/minecraft/world/entity/player/Player;Ldev/anima/Spell;)V",
            at = @At(value = "INVOKE", target = "Ldev/anima/Spell;cooldown()I"))
    private static int ftbevo$spellHaste(int cooldown, @Local(argsOnly = true) Player player) {
        return OtherworldMagic.hastenedCooldown(player, cooldown);
    }
}
