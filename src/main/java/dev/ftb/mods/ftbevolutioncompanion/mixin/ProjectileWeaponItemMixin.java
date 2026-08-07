package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {
    @ModifyExpressionValue(
            method = "useAmmo(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Z)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processAmmoUse(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;I)I"))
    private static int ftbevo$arrowSave(int ammoToUse, ItemStack weapon, ItemStack projectile, LivingEntity holder, boolean forceInfinite) {
        if (ammoToUse > 0 && holder instanceof Player player) {
            double chance = SkillsHelper.attr(player, SkillsRegistry.ARROW_SAVE);
            if (chance > 0.0 && player.getRandom().nextDouble() < chance) {
                return 0;
            }
        }
        return ammoToUse;
    }

    @ModifyExpressionValue(
            method = "draw(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/List;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processProjectileCount(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;I)I"))
    private static int ftbevo$multishot(int count, ItemStack weapon, ItemStack projectile, LivingEntity shooter) {
        if (shooter instanceof Player player) {
            double chance = SkillsHelper.attr(player, SkillsRegistry.MULTISHOT_CHANCE);
            if (chance > 0.0 && player.getRandom().nextDouble() < chance) {
                return Math.max(count, 3);
            }
        }
        return count;
    }
}
