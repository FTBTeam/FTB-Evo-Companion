package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.extendedae.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        targets = {
            "com.glodblock.github.extendedae.client.gui.GuiThresholdExportBus",
            "com.glodblock.github.extendedae.client.gui.GuiPreciseExportBus",
            "com.glodblock.github.extendedae.client.gui.GuiPreciseStorageBus"
        },
        remap = false)
public abstract class ExtendedAESetAmountTooltipMixin {
    @Redirect(
            method = "extractTooltip",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/core/localization/Tooltips;getSetAmountTooltip()Lnet/minecraft/network/chat/Component;"))
    private Component ftbevo$setAmountTooltip() {
        return Component.translatable(
                        "gui.tooltips.ae2.ModifyAmountAction", Component.translatable("gui.tooltips.ae2.MiddleClick"))
                .withStyle(ChatFormatting.DARK_GRAY);
    }
}
