package dev.ftb.mods.ftbevolutioncompanion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.network.Connection$1")
public abstract class ConnectionTimeoutMixin {
    @ModifyConstant(method = "initChannel", constant = @Constant(intValue = 30))
    private int ftbevo$raiseReadTimeout(int original) {
        return 120;
    }
}
