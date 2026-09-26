package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorMenu;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRecipe;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Locale;

public final class FabricatorScreen extends AbstractContainerScreen<FabricatorMenu> {
    private static final Identifier TEXTURE = FTBEvolutionCompanion.id("textures/gui/container/ftb_fabricator.png");
    private static final String LANG = "fabricator.ftbevolutioncompanion.";
    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 220, 214);
        inventoryLabelX = 30;
        inventoryLabelY = 120;
    }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        FabricatorBlockEntity machine = menu.machine();
        int energyHeight = (int) (52L * machine.energy().getAmountAsLong() / FabricatorBlockEntity.ENERGY_CAPACITY);
        graphics.fill(leftPos + 12, topPos + 86 - energyHeight, leftPos + 19, topPos + 86, 0xFFFFC768);
        int progressWidth = machine.duration() == 0 ? 0 : 40 * machine.progress() / machine.duration();
        graphics.fill(leftPos + 89, topPos + 57, leftPos + 89 + progressWidth, topPos + 63, 0xFF4BCFCD);
        for (int tank = 0; tank < 3; tank++) {
            int x = leftPos + 93 + tank * 23;
            int height = 20 * machine.fluids().getAmountAsInt(tank) / FabricatorBlockEntity.TANK_CAPACITY;
            if (height > 0) {
                var fluid = machine.fluids().getResource(tank).getFluid().defaultFluidState();
                var model = minecraft.getModelManager().getFluidStateModelSet().get(fluid);
                int tint = 0xFF000000 | (model.tintSource() == null ? 0xFFFFFF : model.tintSource().color(fluid.createLegacyBlock()));
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, model.stillMaterial().sprite(), x, topPos + 92 - height, 14, height, tint);
            }
        }
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 12, 10, 0xFFDAFFFF, false);
        graphics.text(font, Component.translatable(LANG + "inputs"), 30, 23, 0xFFC7CFCF, false);
        graphics.text(font, Component.translatable(LANG + "outputs"), 140, 36, 0xFFC7CFCF, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFA1A9A9, false);
        FabricatorBlockEntity machine = menu.machine();
        Component status = Component.translatable(LANG + "status." + machine.status().name().toLowerCase(Locale.ROOT));
        graphics.text(font, status, 12, 100, machine.status() == FabricatorBlockEntity.Status.WORKING ? 0xFF4BCFCD : 0xFFFFC768, false);
        if (!machine.requiredStage().isEmpty()) {
            String stage = Component.translatable(LANG + "stage", FabricatorRecipe.stageName(machine.requiredStage())).getString();
            graphics.text(font, font.plainSubstrByWidth(stage, 196), 12, 110, 0xFFC7CFCF, false);
        }
    }
    @Override protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int x = mouseX - leftPos;
        int y = mouseY - topPos;
        FabricatorBlockEntity machine = menu.machine();
        Component tooltip = null;
        if (x >= 10 && x <= 21 && y >= 32 && y <= 88) {
            tooltip = Component.translatable(LANG + "energy", machine.energy().getAmountAsInt(), FabricatorBlockEntity.ENERGY_CAPACITY);
        } else if (x >= 89 && x <= 131 && y >= 53 && y <= 65) {
            tooltip = Component.translatable(LANG + "process", machine.progress(), machine.duration(), machine.powerUsage());
        } else if (y >= 70 && y <= 94) {
            for (int tank = 0; tank < 3; tank++) {
                if (x < 91 + tank * 23 || x > 109 + tank * 23) continue;
                FluidStack fluid = machine.fluids().getResource(tank).toStack(machine.fluids().getAmountAsInt(tank));
                Component name = fluid.isEmpty() ? Component.translatable(LANG + "empty") : fluid.getHoverName();
                tooltip = Component.translatable(LANG + (tank < 2 ? "fluid_input" : "fluid_output"), name, fluid.getAmount(), FabricatorBlockEntity.TANK_CAPACITY);
            }
        } else if (y >= 100 && y <= 119) {
            tooltip = Component.translatable(LANG + "owner_stage", machine.ownerName(), machine.requiredStage().isEmpty() ? Component.literal("—") : FabricatorRecipe.stageName(machine.requiredStage()));
        }
        if (tooltip != null) graphics.setTooltipForNextFrame(font, tooltip, mouseX, mouseY);
    }
}
