package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus.client;

import com.titammods.hephaestus_tools.tables.screen.WorkbenchScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorkbenchScreen.class, remap = false)
public abstract class WorkbenchSwapSlotMixin {
    @Inject(method = "repairVisible", at = @At("RETURN"), cancellable = true)
    private void ftbevo$showSlotForUndamagedTool(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()
                && ((WorkbenchScreen) (Object) this).getMenu().isRepair()) {
            cir.setReturnValue(true);
        }
    }
}
