package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.ArsMagicaHooks;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.apiimpl.MagicHelperImpl", remap = false)
public abstract class ArsMagicXpMixin {
    @ModifyVariable(method = "addXp(Lnet/minecraft/world/entity/player/Player;D)V", at = @At("HEAD"), argsOnly = true)
    private double ftbevo$scaleMagicXp(double xp, @Local(argsOnly = true) Player player) {
        return ArsMagicaHooks.xpGain(player, xp);
    }
}
