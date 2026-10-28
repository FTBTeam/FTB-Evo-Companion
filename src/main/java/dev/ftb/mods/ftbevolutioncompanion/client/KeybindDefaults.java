package dev.ftb.mods.ftbevolutioncompanion.client;

import com.mojang.blaze3d.platform.InputConstants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class KeybindDefaults {
    private static final Map<String, String> DEFAULTS = Map.of(
            "key.guideme.guide", "key.keyboard.g",
            "key.logisticsnetworks.primary_interaction", "key.mouse.left",
            "key.logisticsnetworks.secondary_interaction", "key.mouse.right");
    private static final String MARKER = "keybind_defaults_1";

    private static boolean checked;

    private KeybindDefaults() {}

    public static void onClientTick(ClientTickEvent.Post event) {
        if (checked) {
            return;
        }
        checked = true;
        Path marker = FMLPaths.CONFIGDIR.get().resolve("ftbevolutioncompanion").resolve(MARKER);
        if (Files.exists(marker)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        boolean changed = false;
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            String key = DEFAULTS.get(mapping.getName());
            if (key != null && mapping.isUnbound()) {
                mapping.setKey(InputConstants.getKey(key));
                changed = true;
            }
        }
        if (changed) {
            KeyMapping.resetMapping();
            minecraft.options.save();
        }
        try {
            Files.createDirectories(marker.getParent());
            Files.writeString(marker, "");
        } catch (IOException ignored) {
        }
    }
}
