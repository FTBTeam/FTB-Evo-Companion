package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyReturnValue(
            method = "processDurabilityChange(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)I",
            at = @At("RETURN"))
    private int ftbevo$skillDurability(int newAmount, int amount, ServerLevel level, LivingEntity owner) {
        if (newAmount <= 0 || !(owner instanceof Player player)) {
            return newAmount;
        }
        Holder<Attribute> attribute = SkillsHelper.durabilityAttribute((ItemStack) (Object) this);
        if (attribute == null) {
            return newAmount;
        }
        int virtualLevel = (int) SkillsHelper.attr(player, attribute);
        if (virtualLevel <= 0) {
            return newAmount;
        }
        int kept = 0;
        for (int i = 0; i < newAmount; i++) {
            if (level.getRandom().nextInt(virtualLevel + 1) == 0) {
                kept++;
            }
        }
        return kept;
    }
}
