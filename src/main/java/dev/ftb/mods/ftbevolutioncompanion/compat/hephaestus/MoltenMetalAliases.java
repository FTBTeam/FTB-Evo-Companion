package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MoltenMetalAliases {
    private static final List<String> METALS = List.of(
            "adamantite",
            "aeternium",
            "aurichalcum",
            "titanium",
            "chromium",
            "wrought_iron",
            "conductive_alloy",
            "redstone_alloy",
            "energetic_alloy",
            "pulsating_alloy",
            "soularium",
            "vibrant_alloy",
            "dark_steel",
            "end_steel",
            "energetic_silver",
            "vivid_alloy",
            "energized_copper",
            "energized_gold",
            "advanced_alloy",
            "energized_alloy",
            "crystallized_alloy",
            "niobium",
            "slatesteel",
            "tungsten_slatesteel",
            "desh",
            "ferricore",
            "blazegold",
            "eclipse_alloy",
            "thaumium",
            "void_metal",
            "hellforged",
            "iesnium",
            "atlantic_gold",
            "aquarine_steel",
            "dragonsteel_fire",
            "dragonsteel_ice",
            "dragonsteel_lightning",
            "adamant",
            "duratium",
            "prometheum",
            "ruby",
            "sapphire",
            "peridot",
            "topaz",
            "onyx",
            "garnet",
            "moonstone",
            "prismarine",
            "certus_quartz",
            "fluix",
            "entro",
            "fluxite",
            "xychorium",
            "spirit_attuned",
            "dark_gem",
            "celestine",
            "olivine",
            "dimensional_shard");

    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, "ftb");
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "ftb");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("ftb");

    private MoltenMetalAliases() {}

    public static void register(IEventBus eventBus) {
        for (String metal : METALS) {
            alias(FLUIDS, "molten_" + metal);
            alias(FLUIDS, "flowing_molten_" + metal);
            alias(BLOCKS, "molten_" + metal + "_block");
            alias(ITEMS, "molten_" + metal + "_bucket");
        }
        FLUIDS.register(eventBus);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    private static void alias(DeferredRegister<?> register, String path) {
        register.addAlias(
                Identifier.fromNamespaceAndPath("ftb", path), Identifier.fromNamespaceAndPath("hephaestus", path));
    }
}
