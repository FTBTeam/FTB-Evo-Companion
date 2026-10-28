package dev.ftb.mods.ftbevolutioncompanion.mixin;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMissingEquipmentMixin {
    @Shadow
    private SpawnData nextSpawnData;

    @Shadow
    private WeightedList<SpawnData> spawnPotentials;

    @Inject(method = "load", at = @At("TAIL"))
    private void ftbevo$dropMissingEquipment(Level level, BlockPos pos, ValueInput input, CallbackInfo ci) {
        if (nextSpawnData != null) {
            ftbevo$clean(nextSpawnData.entityToSpawn());
        }
        if (spawnPotentials != null) {
            spawnPotentials.unwrap().forEach(entry -> ftbevo$clean(entry.value().entityToSpawn()));
        }
    }

    @Unique
    private static void ftbevo$clean(CompoundTag entity) {
        entity.getCompound("equipment").ifPresent(equipment -> {
            for (String slot : new ArrayList<>(equipment.keySet())) {
                boolean known = equipment
                        .getCompound(slot)
                        .flatMap(stack -> stack.getString("id"))
                        .map(Identifier::tryParse)
                        .map(BuiltInRegistries.ITEM::containsKey)
                        .orElse(true);
                if (!known) {
                    equipment.remove(slot);
                }
            }
            if (equipment.isEmpty()) {
                entity.remove("equipment");
            }
        });
        entity.getList("Passengers").ifPresent(passengers -> {
            for (Tag passenger : passengers) {
                if (passenger instanceof CompoundTag compound) {
                    ftbevo$clean(compound);
                }
            }
        });
    }
}
