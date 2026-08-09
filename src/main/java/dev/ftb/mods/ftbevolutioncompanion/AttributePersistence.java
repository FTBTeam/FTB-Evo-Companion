package dev.ftb.mods.ftbevolutioncompanion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

public final class AttributePersistence {
    private static final String NAMESPACE = "ftb";

    private static List<Holder<Attribute>> attributes;

    private AttributePersistence() {
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        Player original = event.getOriginal();
        Player player = event.getEntity();
        for (Holder<Attribute> holder : ownedAttributes()) {
            AttributeInstance from = original.getAttribute(holder);
            AttributeInstance to = player.getAttribute(holder);
            if (from == null || to == null) {
                continue;
            }
            to.setBaseValue(from.getBaseValue());
            for (AttributeModifier modifier : from.getModifiers()) {
                to.addOrReplacePermanentModifier(modifier);
            }
        }
    }

    private static List<Holder<Attribute>> ownedAttributes() {
        if (attributes == null) {
            List<Holder<Attribute>> found = new ArrayList<>();
            for (Identifier id : BuiltInRegistries.ATTRIBUTE.keySet()) {
                if (id.getNamespace().equals(NAMESPACE)) {
                    BuiltInRegistries.ATTRIBUTE.get(id).ifPresent(found::add);
                }
            }
            attributes = List.copyOf(found);
        }
        return attributes;
    }
}
