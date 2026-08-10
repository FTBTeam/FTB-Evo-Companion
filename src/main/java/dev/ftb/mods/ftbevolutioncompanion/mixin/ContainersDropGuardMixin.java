package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.compat.sable.SubLevelMoveGuard;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Containers.class)
public abstract class ContainersDropGuardMixin {

    @Inject(method = "dropItemStack", at = @At("HEAD"), cancellable = true)
    private static void ftbevolutioncompanion$skipDropDuringSubLevelMove(Level level, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (SubLevelMoveGuard.isActive()) {
            ci.cancel();
        }
    }
}
