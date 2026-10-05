package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.oceanmobs;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.compat.oceanmobs.RiftArena;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.ftb.mods.ftboceanmobs.entity.riftweaver.RiftWeaverBoss", remap = false)
public abstract class RiftWeaverBossMixin {
    @Shadow
    private BlockPos spawnPos;

    @WrapMethod(method = "customServerAiStep")
    private void ftbevo$arenaContext(ServerLevel level, Operation<Void> original) {
        RiftArena.begin(level, spawnPos != null ? spawnPos : ((Entity) (Object) this).blockPosition());
        try {
            original.call(level);
        } finally {
            RiftArena.end();
        }
    }

    @WrapOperation(
            method = "customServerAiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"))
    private int ftbevo$arenaHeight(Level level, Heightmap.Types type, int x, int z, Operation<Integer> original) {
        return RiftArena.height(level, type, x, z, original.call(level, type, x, z));
    }
}
