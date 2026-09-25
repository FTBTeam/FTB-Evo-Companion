package dev.ftb.mods.ftbevolutioncompanion.compat.curios;

import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import top.theillusivec4.curios.common.data.CuriosSlotResources;

public final class CuriosReloadFix {
    private CuriosReloadFix() {
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        CuriosSlotResources slots = CuriosSlotResources.SERVER;
        if (slots != null && slots.getSlots().isEmpty()) {
            slots.populateData();
        }
    }
}
