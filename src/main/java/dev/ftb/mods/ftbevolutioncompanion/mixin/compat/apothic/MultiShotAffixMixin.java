package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.apothic;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import ianm1647.apothic_compats.affix.irons_artifice.MultiShotAffix;
import liedge.ltxindustries.item.weapon.WeaponItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MultiShotAffix.class, remap = false)
public abstract class MultiShotAffixMixin {
    @ModifyReturnValue(method = "canApplyTo", at = @At("RETURN"))
    private boolean ftbevo$notOnLtxWeapons(boolean original, ItemStack stack, LootCategory cat, LootRarity rarity) {
        return original && !(stack.getItem() instanceof WeaponItem);
    }
}
