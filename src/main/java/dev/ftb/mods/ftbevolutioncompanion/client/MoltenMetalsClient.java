package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.MoltenMetals;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;

public final class MoltenMetalsClient {
    private static final Material STILL =
            new Material(Identifier.fromNamespaceAndPath("hephaestus", "fluid/liquid/molten_metal"));
    private static final Material FLOWING =
            new Material(Identifier.fromNamespaceAndPath("hephaestus", "fluid/liquid/molten_metal_flow"));

    private MoltenMetalsClient() {}

    public static void onRegisterFluidModels(RegisterFluidModelsEvent event) {
        for (MoltenMetals.Entry entry : MoltenMetals.ENTRIES) {
            int tint = entry.type.get().tint();
            event.register(new FluidModel.Unbaked(STILL, FLOWING, null, state -> tint), entry.source, entry.flowing);
        }
    }
}
