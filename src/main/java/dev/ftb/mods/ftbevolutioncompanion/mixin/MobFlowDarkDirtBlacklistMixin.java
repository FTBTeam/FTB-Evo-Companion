package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.content.CompanionTags;

import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.MobSpawnSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.misterd.mobflowutilities.block.custom.DarkDirtBlock", remap = false)
public abstract class MobFlowDarkDirtBlacklistMixin {
    @WrapOperation(method = "spawnOnce(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;"
            + "Lnet/minecraft/util/RandomSource;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/random/WeightedList$Builder;build()"
                            + "Lnet/minecraft/util/random/WeightedList;"))
    private WeightedList<MobSpawnSettings.SpawnerData> ftbevo$dropBlacklistedSpawns(
            WeightedList.Builder<MobSpawnSettings.SpawnerData> builder,
            Operation<WeightedList<MobSpawnSettings.SpawnerData>> original) {
        WeightedList<MobSpawnSettings.SpawnerData> spawns = original.call(builder);

        WeightedList.Builder<MobSpawnSettings.SpawnerData> kept = WeightedList.builder();
        boolean dropped = false;
        for (Weighted<MobSpawnSettings.SpawnerData> entry : spawns.unwrap()) {
            if (entry.value().type().builtInRegistryHolder().is(CompanionTags.DARK_DIRT_BLACKLIST)) {
                dropped = true;
            } else {
                kept.add(entry.value(), entry.weight());
            }
        }

        return dropped ? kept.build() : spawns;
    }
}
