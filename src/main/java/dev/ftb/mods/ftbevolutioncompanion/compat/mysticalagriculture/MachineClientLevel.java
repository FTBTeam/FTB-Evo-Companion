package dev.ftb.mods.ftbevolutioncompanion.compat.mysticalagriculture;

import dev.ftb.mods.ftbevolutioncompanion.client.ClientLevelAccess;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

public final class MachineClientLevel {
    private MachineClientLevel() {}

    public static Supplier<Level> supplier() {
        return FMLEnvironment.getDist().isClient() ? ClientLevelAccess::level : () -> null;
    }
}
