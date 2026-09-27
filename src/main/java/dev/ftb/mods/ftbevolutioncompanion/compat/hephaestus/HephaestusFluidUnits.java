package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class HephaestusFluidUnits {
    private static final int NUGGET_MB = 10;
    private static final int INGOT_MB = 90;
    private static final int BLOCK_MB = 810;

    private HephaestusFluidUnits() {
    }

    public static MutableComponent breakdown(int amount) {
        int blocks = amount / BLOCK_MB;
        int ingots = amount % BLOCK_MB / INGOT_MB;
        int nuggets = amount % INGOT_MB / NUGGET_MB;
        int mb = amount % NUGGET_MB;
        StringBuilder sb = new StringBuilder();
        if (blocks > 0) {
            sb.append(blocks).append(" ").append(Component.translatable("gui.hephaestus.unit.blocks").getString()).append(" ");
        }
        if (ingots > 0) {
            sb.append(ingots).append(" ").append(Component.translatable("gui.hephaestus.unit.ingots").getString()).append(" ");
        }
        if (nuggets > 0) {
            sb.append(nuggets).append(" ").append(Component.translatable("gui.hephaestus.unit.nuggets").getString()).append(" ");
        }
        if (mb > 0 || sb.isEmpty()) {
            sb.append(mb).append(" mB");
        }
        return Component.literal(sb.toString().trim());
    }
}
