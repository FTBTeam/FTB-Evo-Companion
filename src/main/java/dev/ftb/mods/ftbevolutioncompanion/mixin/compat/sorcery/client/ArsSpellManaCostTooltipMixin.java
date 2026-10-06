package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery.client;

import at.minecraftschurli.mods.arsmagicalegacy.api.spell.Spell;
import at.minecraftschurli.mods.arsmagicalegacy.item.SpellItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.client.SorceryClientDisplay;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SpellItem.class, remap = false)
public abstract class ArsSpellManaCostTooltipMixin {
    @WrapOperation(
            method =
                    "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;"
                            + "Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;"
                            + "Lnet/minecraft/world/item/TooltipFlag;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lat/minecraftschurli/mods/arsmagicalegacy/api/spell/Spell;getManaCost("
                                    + "Lnet/minecraft/core/RegistryAccess;)D"))
    private double ftbevo$showReducedManaCost(Spell spell, RegistryAccess access, Operation<Double> original) {
        return SorceryClientDisplay.arsManaCost(spell, original.call(spell, access));
    }
}
