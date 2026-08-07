package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.client.KeyMapping;

import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import org.lwjgl.glfw.GLFW;

public final class SkillsKeys {
    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(FTBEvolutionCompanion.id("skills"));

    public static final KeyMapping ACTIVATE_NINJA =
            new KeyMapping("key.ftbevolutioncompanion.activate_ninja", GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping TOGGLE_LIGHTNING =
            new KeyMapping("key.ftbevolutioncompanion.toggle_lightning", GLFW.GLFW_KEY_APOSTROPHE, CATEGORY);
    public static final KeyMapping TOGGLE_FASTER_STRIKES =
            new KeyMapping("key.ftbevolutioncompanion.toggle_faster_strikes", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping TOGGLE_UNARMED_RAMP =
            new KeyMapping("key.ftbevolutioncompanion.toggle_unarmed_ramp", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping TOGGLE_CHEAT_DEATH =
            new KeyMapping("key.ftbevolutioncompanion.toggle_cheat_death", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping TOGGLE_SHADOW_STEP =
            new KeyMapping("key.ftbevolutioncompanion.toggle_shadow_step", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping TOGGLE_BLADEMASTER =
            new KeyMapping("key.ftbevolutioncompanion.toggle_blademaster", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

    private SkillsKeys() {
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(ACTIVATE_NINJA);
        event.register(TOGGLE_LIGHTNING);
        event.register(TOGGLE_FASTER_STRIKES);
        event.register(TOGGLE_UNARMED_RAMP);
        event.register(TOGGLE_CHEAT_DEATH);
        event.register(TOGGLE_SHADOW_STEP);
        event.register(TOGGLE_BLADEMASTER);
    }
}
