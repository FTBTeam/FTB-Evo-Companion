package dev.ftb.mods.ftbevolutioncompanion.athletics;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class AthleticsRegistry {
    public static final String ATTRIBUTE_NAMESPACE = "ftb";

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, ATTRIBUTE_NAMESPACE);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FTBEvolutionCompanion.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> EXTRA_JUMPS = ATTRIBUTES.register("extra_jumps",
            () -> new RangedAttribute("attribute.name.ftb.extra_jumps", 0.0, 0.0, 16.0).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> WALL_CLIMB = ATTRIBUTES.register("wall_climb",
            () -> new RangedAttribute("attribute.name.ftb.wall_climb", 0.0, 0.0, 16.0).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> AIR_DASH = ATTRIBUTES.register("air_dash",
            () -> new RangedAttribute("attribute.name.ftb.air_dash", 0.0, 0.0, 16.0).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> INSTANT_PORTALS = ATTRIBUTES.register("instant_portals",
            () -> new RangedAttribute("attribute.name.ftb.instant_portals", 0.0, 0.0, 1.0).setSyncable(true));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AthleticsToggles>> TOGGLES =
            ATTACHMENTS.register("athletics_toggles",
                    () -> AttachmentType.builder(() -> AthleticsToggles.DEFAULT)
                            .serialize(AthleticsToggles.CODEC)
                            .copyOnDeath()
                            .build());

    private AthleticsRegistry() {
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, EXTRA_JUMPS);
        event.add(EntityType.PLAYER, WALL_CLIMB);
        event.add(EntityType.PLAYER, AIR_DASH);
        event.add(EntityType.PLAYER, INSTANT_PORTALS);
    }
}
