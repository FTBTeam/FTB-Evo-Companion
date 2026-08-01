package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.client.KeyMapping;

import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import org.lwjgl.glfw.GLFW;

public final class AthleticsKeys {
    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(FTBEvolutionCompanion.id("athletics"));

    public static final KeyMapping TOGGLE_EXTRA_JUMPS =
            new KeyMapping("key.ftbevolutioncompanion.toggle_extra_jumps", GLFW.GLFW_KEY_J, CATEGORY);
    public static final KeyMapping TOGGLE_WALL_CLIMB =
            new KeyMapping("key.ftbevolutioncompanion.toggle_wall_climb", GLFW.GLFW_KEY_H, CATEGORY);
    public static final KeyMapping TOGGLE_AIR_DASH =
            new KeyMapping("key.ftbevolutioncompanion.toggle_air_dash", GLFW.GLFW_KEY_K, CATEGORY);
    public static final KeyMapping AIR_DASH =
            new KeyMapping("key.ftbevolutioncompanion.air_dash", GLFW.GLFW_KEY_G, CATEGORY);

    private AthleticsKeys() {
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(TOGGLE_EXTRA_JUMPS);
        event.register(TOGGLE_WALL_CLIMB);
        event.register(TOGGLE_AIR_DASH);
        event.register(AIR_DASH);
    }
}
