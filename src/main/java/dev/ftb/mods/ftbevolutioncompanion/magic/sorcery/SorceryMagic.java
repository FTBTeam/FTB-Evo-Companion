package dev.ftb.mods.ftbevolutioncompanion.magic.sorcery;

import at.minecraftschurli.mods.arsmagicalegacy.api.event.ManaBurnoutCostEvent;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class SorceryMagic {
    private SorceryMagic() {}

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(SorceryMagic::onManaBurnoutCost);
        ThaumaturgeHooks.registerResearchListener();
        ThaumaturgeHooks.registerSpellListeners();
    }

    public static void onManaBurnoutCost(ManaBurnoutCostEvent event) {
        double reduction = MagicRegistry.value(event.getEntity(), MagicRegistry.ARS_COST_REDUCTION);
        if (reduction <= 0.0) {
            return;
        }
        double factor = 1.0 - reduction;
        event.setMana(event.getMana() * factor);
        event.setBurnout(event.getBurnout() * factor);
    }
}
