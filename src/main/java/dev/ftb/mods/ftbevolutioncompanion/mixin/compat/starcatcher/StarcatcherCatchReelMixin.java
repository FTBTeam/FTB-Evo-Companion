package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.starcatcher;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.compat.streams.ReelingCatches;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.wdiscute.starcatcher.fish.FishApi", remap = false)
public abstract class StarcatcherCatchReelMixin {
    @WrapOperation(
            method = "spawnFishFromPlayerFishing(Lnet/minecraft/server/level/ServerPlayer;ZIZZI)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private static void ftbevo$markReeledFish(Entity entity, Vec3 motion, Operation<Void> original) {
        original.call(entity, motion);
        ReelingCatches.mark(entity);
    }

    @WrapOperation(
            method = "spawnFishFromPlayerFishing(Lnet/minecraft/server/level/ServerPlayer;ZIZZI)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private static void ftbevo$markReeledTreasure(ItemEntity item, Vec3 motion, Operation<Void> original) {
        original.call(item, motion);
        ReelingCatches.mark(item);
    }
}
