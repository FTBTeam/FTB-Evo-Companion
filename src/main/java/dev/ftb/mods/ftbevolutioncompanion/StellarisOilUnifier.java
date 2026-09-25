package dev.ftb.mods.ftbevolutioncompanion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

public final class StellarisOilUnifier {
    private static final Identifier STELLARIS_OIL = Identifier.fromNamespaceAndPath("stellaris", "oil");
    private static final Identifier ORITECH_OIL = Identifier.fromNamespaceAndPath("oritech", "still_oil");

    private StellarisOilUnifier() {
    }

    public static boolean isOritechOilFor(Fluid actual, Fluid expected) {
        return STELLARIS_OIL.equals(BuiltInRegistries.FLUID.getKey(expected))
                && ORITECH_OIL.equals(BuiltInRegistries.FLUID.getKey(actual));
    }
}
