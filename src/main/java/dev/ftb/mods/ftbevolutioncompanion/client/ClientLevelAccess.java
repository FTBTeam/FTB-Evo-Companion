package dev.ftb.mods.ftbevolutioncompanion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public final class ClientLevelAccess {
    private ClientLevelAccess() {}

    public static Level level() {
        return Minecraft.getInstance().level;
    }
}
