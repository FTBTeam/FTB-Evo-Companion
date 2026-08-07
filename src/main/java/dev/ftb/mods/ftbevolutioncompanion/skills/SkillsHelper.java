package dev.ftb.mods.ftbevolutioncompanion.skills;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class SkillsHelper {
    private SkillsHelper() {
    }

    public static double attr(LivingEntity entity, Holder<Attribute> attribute) {
        return entity.getAttribute(attribute) != null ? entity.getAttributeValue(attribute) : 0.0;
    }

    public static boolean isUnarmed(Player player) {
        return player.getMainHandItem().isEmpty();
    }

    public static boolean isAxe(ItemStack stack) {
        return stack.is(ItemTags.AXES);
    }

    public static boolean isSword(ItemStack stack) {
        return stack.is(ItemTags.SWORDS);
    }

    public static boolean isPickaxe(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES);
    }

    public static boolean isBow(ItemStack stack) {
        return stack.getItem() instanceof BowItem;
    }

    public static boolean isCrossbow(ItemStack stack) {
        return stack.getItem() instanceof CrossbowItem;
    }

    public static boolean isShield(ItemStack stack) {
        return stack.has(DataComponents.BLOCKS_ATTACKS);
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.has(DataComponents.EQUIPPABLE);
    }

    public static boolean isBehind(LivingEntity target, Entity attacker) {
        Vec3 toAttacker = attacker.position().subtract(target.position());
        if (toAttacker.lengthSqr() < 1.0E-4) {
            return false;
        }
        return target.getLookAngle().dot(toAttacker.normalize()) < -0.5;
    }

    public static double healthFraction(LivingEntity entity) {
        return entity.getMaxHealth() > 0.0F ? entity.getHealth() / entity.getMaxHealth() : 1.0;
    }

    public static Holder<Attribute> durabilityAttribute(ItemStack stack) {
        if (isSword(stack)) {
            return SkillsRegistry.SWORD_DURABILITY;
        }
        if (isAxe(stack)) {
            return SkillsRegistry.AXE_DURABILITY;
        }
        if (isPickaxe(stack)) {
            return SkillsRegistry.PICKAXE_DURABILITY;
        }
        if (isBow(stack)) {
            return SkillsRegistry.BOW_DURABILITY;
        }
        if (isCrossbow(stack)) {
            return SkillsRegistry.CROSSBOW_DURABILITY;
        }
        if (isShield(stack)) {
            return SkillsRegistry.SHIELD_DURABILITY;
        }
        if (isArmor(stack)) {
            return SkillsRegistry.ARMOR_DURABILITY;
        }
        return null;
    }
}
