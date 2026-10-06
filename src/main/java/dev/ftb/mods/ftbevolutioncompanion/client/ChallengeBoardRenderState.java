package dev.ftb.mods.ftbevolutioncompanion.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.network.chat.Component;

public class ChallengeBoardRenderState extends BlockEntityRenderState {
    float rotationAngle;
    int width = 1;
    int height = 1;
    boolean hasEntry;
    Component title = Component.empty();
    Component headline = Component.empty();
    Component percentText = Component.empty();
    Component countText = Component.empty();
    Component emptyText = Component.empty();
    float progress;
    int barColor;
}
