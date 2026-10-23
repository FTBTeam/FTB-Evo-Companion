package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.appflux;

import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        targets = "com.glodblock.github.appflux.common.me.strategy.FEExternalStorageStrategy$FEStorageWrapper",
        remap = false)
public abstract class FEStorageNestedTransactionMixin {
    @Inject(
            method = {
                "insert(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J",
                "extract(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J"
            },
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$skipWhileClosing(CallbackInfoReturnable<Long> cir) {
        if (Transaction.getLifecycle() == Transaction.Lifecycle.CLOSING) {
            cir.setReturnValue(0L);
        }
    }

    @SuppressWarnings("deprecation")
    @Redirect(
            method = {
                "insert(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J",
                "extract(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J"
            },
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/neoforged/neoforge/transfer/transaction/Transaction;openRoot()Lnet/neoforged/neoforge/transfer/transaction/Transaction;"))
    private Transaction ftbevo$nestInOpenTransaction() {
        return Transaction.open(Transaction.getCurrentOpenedTransaction());
    }
}
