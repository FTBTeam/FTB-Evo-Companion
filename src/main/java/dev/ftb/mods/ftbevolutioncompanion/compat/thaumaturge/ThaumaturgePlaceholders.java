package dev.ftb.mods.ftbevolutioncompanion.compat.thaumaturge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThaumaturgePlaceholders {
    private static final String MOD_ID = "thaumaturge";

    private static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MOD_ID);

    static {
        ATTRIBUTES.register(
                "vis_discount",
                () -> new PercentageAttribute("attributes.thaumaturge.vis_discount", 0, 0, 1).setSyncable(true));
    }

    private ThaumaturgePlaceholders() {}

    public static void register(IEventBus eventBus) {
        if (!ModList.get().isLoaded(MOD_ID)) {
            ATTRIBUTES.register(eventBus);
        }
    }
}
