package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.EnchantingHooks;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ApothEnchantmentScreen.class, remap = false)
public abstract class ApothEnchantmentScreenMixin {
    @ModifyExpressionValue(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Ldev/shadowsoffire/apothic_enchanting/util/MiscUtil;getExpCostForSlot(II)I"))
    private int ftbevo$showDiscountedExperience(int original) {
        return EnchantingHooks.discount(Minecraft.getInstance().player, original);
    }
}
