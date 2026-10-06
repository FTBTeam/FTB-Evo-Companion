package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery.client;

import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.client.SorceryClientDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.leclowndu93150.thaumaturge.client.screen.casters.FocalManipulatorScreen", remap = false)
public abstract class ThaumFocusDamageDisplayMixin {
    @ModifyArg(
            method = "genPartText(Lnet/minecraft/resources/Identifier;"
                    + "Lcom/leclowndu93150/thaumaturge/api/casters/FocusElement;I)Ljava/util/List;",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lcom/leclowndu93150/thaumaturge/api/casters/FocusEffect;damageForDisplay("
                                    + "Lcom/leclowndu93150/thaumaturge/api/casters/FocusSettings;F)F"),
            index = 1)
    private float ftbevo$showEmpoweredFocus(float power) {
        return SorceryClientDisplay.thaumFocusPower(power);
    }
}
