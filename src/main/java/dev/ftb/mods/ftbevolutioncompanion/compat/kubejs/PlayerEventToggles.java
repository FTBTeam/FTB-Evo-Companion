package dev.ftb.mods.ftbevolutioncompanion.compat.kubejs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Method;

public final class PlayerEventToggles {
    private static final Method PERSISTENT_DATA = findPersistentData();

    private PlayerEventToggles() {}

    public static boolean isDisabled(Entity player, String eventName) {
        if (PERSISTENT_DATA == null) {
            return false;
        }
        try {
            CompoundTag data = (CompoundTag) PERSISTENT_DATA.invoke(player);
            ListTag disabled = data.getListOrEmpty("disabledEvents");
            for (int i = 0; i < disabled.size(); i++) {
                if (eventName.equals(disabled.getStringOr(i, ""))) {
                    return true;
                }
            }
            return false;
        } catch (ReflectiveOperationException | ClassCastException e) {
            return false;
        }
    }

    private static Method findPersistentData() {
        try {
            return Entity.class.getMethod("kjs$getPersistentData");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
