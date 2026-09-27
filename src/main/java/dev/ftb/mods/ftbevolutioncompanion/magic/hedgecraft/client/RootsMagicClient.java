package dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.client;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.RootsMagic;

import elucent.rootsclassic.component.ComponentBase;
import elucent.rootsclassic.component.ComponentBaseRegistry;
import elucent.rootsclassic.component.components.ComponentOrangeTulip;
import elucent.rootsclassic.datacomponent.SpellData;
import elucent.rootsclassic.item.SylvanArmorItem;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public final class RootsMagicClient {
    private static final EquipmentSlot[] SYLVAN_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private RootsMagicClient() {
    }

    public static int staffPotency(SpellData spell, int potency, int staffBonus) {
        Player player = Minecraft.getInstance().player;
        if (player == null || spell == null) {
            return potency;
        }
        Identifier id = Identifier.tryParse(spell.effect());
        ComponentBase component = id == null ? null : ComponentBaseRegistry.COMPONENTS.getValue(id);
        if (component == null) {
            return potency;
        }
        int castPotency = component instanceof ComponentOrangeTulip
                ? potency
                : potency + staffBonus + sylvanBonus(player);
        return potency + RootsMagic.potencyBonus(component, player, castPotency);
    }

    private static int sylvanBonus(Player player) {
        for (EquipmentSlot slot : SYLVAN_SLOTS) {
            if (!(player.getItemBySlot(slot).getItem() instanceof SylvanArmorItem)) {
                return 0;
            }
        }
        return 1;
    }
}
