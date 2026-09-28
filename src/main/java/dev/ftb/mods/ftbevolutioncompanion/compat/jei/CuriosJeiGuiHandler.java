package dev.ftb.mods.ftbevolutioncompanion.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;

import top.theillusivec4.curios.client.screen.CuriosScreen;
import top.theillusivec4.curios.common.integration.CuriosExclusionAreas;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public final class CuriosJeiGuiHandler implements IGuiContainerHandler<CuriosScreen> {
    @Override
    public List<Rect2i> getGuiExtraAreas(CuriosScreen screen) {
        List<Rect2i> areas = new ArrayList<>(CuriosExclusionAreas.create(screen));
        // The upstream panel bounds omit the eight-pixel offset on paged menus.
        // Include each actual slot so JEI never treats an equip click as deletion.
        for (Slot slot : screen.getMenu().slots) {
            if (slot instanceof CurioSlot) {
                areas.add(new Rect2i(screen.getLeftPos() + slot.x - 1,
                        screen.getTopPos() + slot.y - 1, 18, 18));
            }
        }
        return areas;
    }
}
