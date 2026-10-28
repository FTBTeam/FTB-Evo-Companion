package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.LootTableDropTracker;
import java.util.Collection;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityLootTableDropsMixin implements LootTableDropTracker {
    @Unique
    private List<ItemEntity> ftbevo$lootTableDrops = List.of();

    @Override
    public List<ItemEntity> ftbevo$lootTableDrops() {
        return this.ftbevo$lootTableDrops;
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void ftbevo$resetLootTableDrops(ServerLevel level, DamageSource source, CallbackInfo ci) {
        this.ftbevo$lootTableDrops = List.of();
    }

    @WrapOperation(
            method = "dropAllDeathLoot",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V"))
    private void ftbevo$recordLootTableDrops(
            LivingEntity self, ServerLevel level, DamageSource source, boolean playerKilled, Operation<Void> original) {
        Collection<ItemEntity> captured = self.captureDrops();
        int before = captured == null ? 0 : captured.size();
        original.call(self, level, source, playerKilled);
        if (captured != null) {
            this.ftbevo$lootTableDrops = captured.stream().skip(before).toList();
        }
    }
}
