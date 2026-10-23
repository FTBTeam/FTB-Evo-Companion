package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.appflux;

import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.glodblock.github.appflux.common.me.strategy.FEStackExportStrategy", remap = false)
public abstract class FEStackExportProbeMixin {
    @SuppressWarnings("deprecation")
    @Inject(
            method = "transfer(Lappeng/api/behaviors/StackTransferContext;Lappeng/api/stacks/AEKey;J)J",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/api/storage/StorageHelper;poweredExtraction(Lappeng/api/networking/energy/IEnergySource;Lappeng/api/storage/MEStorage;Lappeng/api/stacks/AEKey;JLappeng/api/networking/security/IActionSource;Lappeng/api/config/Actionable;)J",
                            ordinal = 1))
    private void ftbevo$closeProbeBeforeExtract(CallbackInfoReturnable<Long> cir) {
        if (Transaction.getCurrentOpenedTransaction() instanceof Transaction probe) {
            probe.close();
        }
    }
}
