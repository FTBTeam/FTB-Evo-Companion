package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.gunnery.LtxGunnery;

import liedge.ltxindustries.entity.CompoundHitResult;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.ToDoubleFunction;

@Mixin(value = CompoundHitResult.class, remap = false)
public abstract class LTXITracePathSeekingMixin {
    @ModifyVariable(
            method = "tracePath(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lliedge/ltxindustries/lib/upgrades/Upgrades;DDIDLliedge/ltxindustries/entity/DynamicClipContext$FluidCollisionPredicate;Ljava/util/function/ToDoubleFunction;)Lliedge/ltxindustries/entity/CompoundHitResult;",
            at = @At("HEAD"),
            argsOnly = true)
    private static ToDoubleFunction<Entity> ftbevo$seeking(
            ToDoubleFunction<Entity> expansion, @Local(argsOnly = true) LivingEntity source) {
        double bonus = LtxGunnery.seekingHitbox(source);
        return bonus > 0.0 ? entity -> expansion.applyAsDouble(entity) + bonus : expansion;
    }
}
