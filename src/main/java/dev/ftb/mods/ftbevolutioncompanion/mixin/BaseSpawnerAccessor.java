package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {
    @Accessor("minSpawnDelay")
    int ftbevo$getMinSpawnDelay();

    @Accessor("maxSpawnDelay")
    int ftbevo$getMaxSpawnDelay();

    @Accessor("spawnCount")
    int ftbevo$getSpawnCount();

    @Accessor("maxNearbyEntities")
    int ftbevo$getMaxNearbyEntities();

    @Accessor("requiredPlayerRange")
    int ftbevo$getRequiredPlayerRange();
}
