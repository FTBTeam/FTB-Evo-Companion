package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.anima.SpellScreen;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.client.OtherworldTooltips;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SpellScreen.class, remap = false)
public abstract class AnimaSpellScreenMixin {
    @ModifyExpressionValue(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
            at = @At(value = "INVOKE", target = "Ldev/anima/Spell;cooldown()I", ordinal = 0))
    private int ftbevo$skillTooltipLines(int cooldown, @Local List<Component> lines, @Local Player player) {
        Component thrift = OtherworldTooltips.soulThrift(player);
        if (thrift != null) {
            lines.add(thrift);
        }
        Component hastened = OtherworldTooltips.spellScreenCooldown(player, cooldown);
        if (hastened == null) {
            return cooldown;
        }
        lines.add(hastened);
        return 0;
    }

    @ModifyExpressionValue(
            method = "cooling(Lnet/minecraft/world/entity/player/Player;Ldev/anima/Spell;)F",
            at = @At(value = "INVOKE", target = "Ldev/anima/Spell;cooldown()I", ordinal = 1))
    private static int ftbevo$hastenedCooldownFill(int cooldown, @Local(argsOnly = true) Player player) {
        return OtherworldTooltips.spellCooldown(player, cooldown);
    }
}
