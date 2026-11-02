package dev.ftb.mods.ftbevolutioncompanion.compat.apotheosis;

import dev.ftb.mods.ftbevolutioncompanion.mixin.BaseSpawnerAccessor;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ArmorySpawnerTiers {
    private static final Map<Identifier, Tier> TIERS = Map.of(
            Identifier.parse("ftbarmory:adamantite_block"),
            new Tier("item.apotheosis.pinnacle_spawner_upgrade_rune", 40, 100, 14, 32, 48),
            Identifier.parse("ftbarmory:aeternium_block"),
            new Tier("block.ftbarmory.adamantite_block", 30, 80, 16, 40, 56),
            Identifier.parse("ftbarmory:aurichalcum_block"),
            new Tier("block.ftbarmory.aeternium_block", 25, 60, 18, 48, 64));

    private ArmorySpawnerTiers() {}

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        Tier tier = TIERS.get(BuiltInRegistries.ITEM.getKey(held.getItem()));
        if (tier == null) {
            return;
        }
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof SpawnerBlockEntity spawner)) {
            return;
        }
        if (tier.isMetBy((BaseSpawnerAccessor) spawner.getSpawner())) {
            return;
        }
        if (!event.getLevel().isClientSide()) {
            event.getEntity()
                    .sendOverlayMessage(Component.translatable(
                                    "ftbevolutioncompanion.spawner_tier.requires_previous",
                                    Component.translatable(tier.previousKey()))
                            .withStyle(ChatFormatting.RED));
        }
        event.setCancellationResult(InteractionResult.FAIL);
        event.setCanceled(true);
    }

    private record Tier(
            String previousKey, int minDelay, int maxDelay, int spawnCount, int maxNearby, int playerRange) {
        boolean isMetBy(BaseSpawnerAccessor spawner) {
            return spawner.ftbevo$getMinSpawnDelay() <= minDelay
                    && spawner.ftbevo$getMaxSpawnDelay() <= maxDelay
                    && spawner.ftbevo$getSpawnCount() >= spawnCount
                    && spawner.ftbevo$getMaxNearbyEntities() >= maxNearby
                    && spawner.ftbevo$getRequiredPlayerRange() >= playerRange;
        }
    }
}
