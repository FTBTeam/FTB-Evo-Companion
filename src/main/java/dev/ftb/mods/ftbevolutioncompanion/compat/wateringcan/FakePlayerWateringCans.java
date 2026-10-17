package dev.ftb.mods.ftbevolutioncompanion.compat.wateringcan;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class FakePlayerWateringCans {
    private static final TagKey<Item> WATERING_CANS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("ftb", "watering_cans"));

    private FakePlayerWateringCans() {}

    private static boolean blocked(Player player, ItemStack stack) {
        return player.isFakePlayer() && stack.is(WATERING_CANS);
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (blocked(event.getEntity(), event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (blocked(event.getEntity(), event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player && blocked(player, event.getItem())) {
            event.setCanceled(true);
        }
    }

    public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof Player player && blocked(player, event.getItem())) {
            event.setCanceled(true);
        }
    }
}
