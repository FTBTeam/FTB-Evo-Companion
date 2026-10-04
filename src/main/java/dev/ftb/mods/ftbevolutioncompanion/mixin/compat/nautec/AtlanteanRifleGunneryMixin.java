package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.nautec;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryRegistry;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.breakinblocks.nautec.content.items.AtlanteanRifleItem", remap = false)
public abstract class AtlanteanRifleGunneryMixin {
    @ModifyReturnValue(method = "chargeTicks(Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"))
    private static int ftbevo$reloadSpeed(int ticks, @Local(argsOnly = true) LivingEntity holder) {
        double speed = SkillsHelper.attr(holder, GunneryRegistry.GUN_RELOAD_SPEED);
        return speed > 0.0 ? (int) Math.round(ticks / (1.0 + speed)) : ticks;
    }

    @ModifyReturnValue(method = "rampTicks(Lnet/minecraft/world/entity/LivingEntity;)F", at = @At("RETURN"))
    private static float ftbevo$fireRate(float ticks, @Local(argsOnly = true) LivingEntity holder) {
        double rate = SkillsHelper.attr(holder, GunneryRegistry.GUN_FIRE_RATE);
        return rate > 0.0 ? (float) (ticks / (1.0 + rate)) : ticks;
    }

    @ModifyReturnValue(
            method = "drainFor(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)I",
            at = @At("RETURN"))
    private static int ftbevo$ammoSave(int drain, @Local(argsOnly = true) LivingEntity holder) {
        double save = SkillsHelper.attr(holder, GunneryRegistry.GUN_AMMO_SAVE);
        return save > 0.0 ? (int) Math.round(drain * Math.max(0.0, 1.0 - save)) : drain;
    }

    @ModifyReturnValue(method = "pierceCount(Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"))
    private static int ftbevo$piercing(int count, @Local(argsOnly = true) LivingEntity holder) {
        return count + (int) Math.floor(SkillsHelper.attr(holder, GunneryRegistry.GUN_PIERCING));
    }

    @ModifyReturnValue(method = "ricochetCount(Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"))
    private static int ftbevo$ricochet(int count, @Local(argsOnly = true) LivingEntity holder) {
        return count + (int) Math.floor(SkillsHelper.attr(holder, GunneryRegistry.GUN_RICOCHET));
    }

    @ModifyReturnValue(method = "maxRamp(Lnet/minecraft/world/entity/LivingEntity;)F", at = @At("RETURN"))
    private static float ftbevo$extraBullet(float ramp, @Local(argsOnly = true) LivingEntity holder) {
        return ramp + (float) SkillsHelper.attr(holder, GunneryRegistry.GUN_EXTRA_BULLET);
    }

    @ModifyReturnValue(method = "shakeScale(Lnet/minecraft/world/entity/LivingEntity;)F", at = @At("RETURN"))
    private static float ftbevo$accuracy(float scale, @Local(argsOnly = true) LivingEntity holder) {
        return scale * (float) Math.max(0.0, 1.0 - SkillsHelper.attr(holder, GunneryRegistry.GUN_ACCURACY));
    }
}
