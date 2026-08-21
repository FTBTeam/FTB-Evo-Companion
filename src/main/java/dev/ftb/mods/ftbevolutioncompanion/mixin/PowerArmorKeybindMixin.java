package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.mojang.blaze3d.platform.InputConstants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.portingdeadmods.power_armor.client.PAKeybinds", remap = false)
public abstract class PowerArmorKeybindMixin {
    @ModifyArg(method = "lambda$keyBind$0(Ljava/lang/String;I)Lnet/minecraft/client/KeyMapping;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;<init>(Ljava/lang/String;"
                            + "Lcom/mojang/blaze3d/platform/InputConstants$Type;I"
                            + "Lnet/minecraft/client/KeyMapping$Category;)V"),
            index = 2)
    private static int ftbevo$unbindInvalidKey(int keyCode) {
        return keyCode == 0 ? InputConstants.UNKNOWN.getValue() : keyCode;
    }
}
