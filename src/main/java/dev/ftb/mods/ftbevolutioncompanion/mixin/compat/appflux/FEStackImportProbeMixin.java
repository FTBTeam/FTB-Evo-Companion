package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.appflux;

import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.glodblock.github.appflux.common.me.strategy.FEStackImportStrategy", remap = false)
public abstract class FEStackImportProbeMixin {
    @SuppressWarnings("deprecation")
    @Inject(
            method = "transfer(Lappeng/api/behaviors/StackTransferContext;)Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/api/storage/MEStorage;insert(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J",
                            ordinal = 1))
    private void ftbevo$closeProbeBeforeInsert(CallbackInfoReturnable<Boolean> cir) {
        if (Transaction.getCurrentOpenedTransaction() instanceof Transaction probe) {
            probe.close();
        }
    }
}
