package dev.ftb.mods.ftbevolutioncompanion.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(targets = "com.enderio.enderio.content.broken_spawner.BrokenSpawnerLootModifier", remap = false)
public abstract class BrokenSpawnerLootModifierMixin {
    @Unique
    private static final Map<String, Long> ftbevo$lastRoll = new LinkedHashMap<>(32, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            return size() > 32;
        }
    };

    @Inject(method = "doApply", at = @At("HEAD"), cancellable = true)
    private void ftbevo$onePerBreak(ObjectArrayList<ItemStack> generatedLoot, LootContext context,
                                    CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        if (!(context.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof SpawnerBlockEntity spawner)) {
            return;
        }

        ServerLevel level = context.getLevel();
        BlockPos pos = spawner.getBlockPos();
        String key = level.dimension().identifier() + "@" + pos.asLong();
        long tick = level.getGameTime();

        synchronized (ftbevo$lastRoll) {
            Long previous = ftbevo$lastRoll.get(key);

            if (previous != null && previous == tick) {
                cir.setReturnValue(generatedLoot);
                return;
            }

            ftbevo$lastRoll.put(key, tick);
        }
    }
}
