package dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft;

import net.minecraft.util.RandomSource;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class HedgeCraftMagic {
    private HedgeCraftMagic() {
    }

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(CovenMagic::onServerStopped);
    }

    static int wholeLevels(double value) {
        return value <= 0.0 ? 0 : (int) Math.floor(value + 1.0E-6);
    }

    static int roundRandomly(RandomSource random, double value) {
        int whole = (int) Math.floor(value);
        double fraction = value - whole;
        if (fraction > 1.0E-9 && random.nextDouble() < fraction) {
            whole++;
        }
        return whole;
    }
}
