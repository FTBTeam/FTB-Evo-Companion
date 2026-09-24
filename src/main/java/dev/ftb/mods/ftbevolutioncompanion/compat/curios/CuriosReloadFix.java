package dev.ftb.mods.ftbevolutioncompanion.compat.curios;

import net.neoforged.neoforge.event.TagsUpdatedEvent;

import top.theillusivec4.curios.common.data.CuriosSlotResources;

public final class CuriosReloadFix {
    private CuriosReloadFix() {
    }

    public static void onServerDataLoad(TagsUpdatedEvent.ServerDataLoad event) {
        CuriosSlotResources slots = CuriosSlotResources.SERVER;
        if (slots != null && slots.getSlots().isEmpty()) {
            slots.populateData();
        }
    }
}
