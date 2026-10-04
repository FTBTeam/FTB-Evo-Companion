package dev.ftb.mods.ftbevolutioncompanion.compat.streams;

import net.minecraft.world.entity.Entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ReelingCatches {
    private static final Class<?> REELING_CATCH = findType();
    private static final MethodHandle BEGIN_REEL = findBeginReel();

    private ReelingCatches() {}

    public static void mark(Entity entity) {
        if (BEGIN_REEL == null || !REELING_CATCH.isInstance(entity)) {
            return;
        }
        try {
            BEGIN_REEL.invoke(entity);
        } catch (Throwable ignored) {
        }
    }

    private static Class<?> findType() {
        try {
            return Class.forName("dev.streamsreflowing.flow.ReelingCatch");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static MethodHandle findBeginReel() {
        if (REELING_CATCH == null) {
            return null;
        }
        try {
            return MethodHandles.publicLookup()
                    .findVirtual(REELING_CATCH, "streamsreflowing$beginReel", MethodType.methodType(void.class));
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
