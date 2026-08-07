package dev.ftb.mods.ftbevolutioncompanion.skills;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Optional;

public final class ApothicHooks {
    private static Optional<Holder.Reference<Attribute>> lifeSteal;

    private ApothicHooks() {
    }

    public static Optional<Holder.Reference<Attribute>> lifeSteal() {
        if (lifeSteal == null) {
            lifeSteal = BuiltInRegistries.ATTRIBUTE.get(
                    Identifier.fromNamespaceAndPath("apothic_attributes", "life_steal"));
        }
        return lifeSteal;
    }
}
