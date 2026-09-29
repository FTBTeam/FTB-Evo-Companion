package dev.ftb.mods.ftbevolutioncompanion.compat.mysticalagriculture;

import dev.ftb.mods.ftbevolutioncompanion.client.ClientLevelAccess;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public final class MachineClientLevel {
    private MachineClientLevel() {
    }

    public static Supplier<Level> supplier() {
        return FMLEnvironment.getDist().isClient() ? ClientLevelAccess::level : () -> null;
    }
}
