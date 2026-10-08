package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.xycraft;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.WorldDataConfiguration;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftServer.class)
public abstract class XyCraftForcedFeaturesMixin {
    @ModifyVariable(method = "configurePackRepository", at = @At("HEAD"), argsOnly = true)
    private static WorldDataConfiguration ftbevo$forceXyCraftFeatures(WorldDataConfiguration config) {
        List<Identifier> flags = new ArrayList<>();
        if (ModList.get().isLoaded("xycraft_core")) {
            flags.add(Identifier.fromNamespaceAndPath("xycraft_core", "experimental"));
        }
        if (ModList.get().isLoaded("xycraft_machines")) {
            flags.add(Identifier.fromNamespaceAndPath("xycraft_machines", "logistics"));
        }
        if (flags.isEmpty()) {
            return config;
        }
        FeatureFlagSet forced = FeatureFlags.REGISTRY.fromNames(flags, id -> {});
        return config.expandFeatures(forced);
    }
}
