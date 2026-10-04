package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.nautec;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.compat.streams.ReelingCatches;
import net.minecraft.world.entity.item.ItemEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.breakinblocks.nautec.content.entities.NautecFishingHook", remap = false)
public abstract class NautecCatchReelMixin {
    @WrapOperation(
            method =
                    "dropTowards(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;DDD)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"))
    private static void ftbevo$markReeledCatch(
            ItemEntity item, double x, double y, double z, Operation<Void> original) {
        original.call(item, x, y, z);
        ReelingCatches.mark(item);
    }
}
