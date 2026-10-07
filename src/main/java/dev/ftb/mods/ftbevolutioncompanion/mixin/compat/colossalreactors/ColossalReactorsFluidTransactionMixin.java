package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.colossalreactors;

import dev.ftb.mods.ftbevolutioncompanion.compat.colossalreactors.DeferredFluidJournal;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.unfamily.colossal_reactors.transfer.LegacyIFluidHandlerResourceHandler", remap = false)
public abstract class ColossalReactorsFluidTransactionMixin {
    @Shadow
    @Final
    private IFluidHandler delegate;

    @Unique
    private DeferredFluidJournal ftbevo$journal;

    @Unique
    private DeferredFluidJournal ftbevo$journal() {
        if (ftbevo$journal == null) ftbevo$journal = new DeferredFluidJournal(delegate);
        return ftbevo$journal;
    }

    @Inject(
            method =
                    "extract(ILnet/neoforged/neoforge/transfer/fluid/FluidResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$deferIndexedExtract(
            int index,
            FluidResource resource,
            int amount,
            TransactionContext transaction,
            CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ftbevo$journal().extract(resource, amount, transaction));
    }

    @Inject(
            method =
                    "extract(Lnet/neoforged/neoforge/transfer/fluid/FluidResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$deferExtract(
            FluidResource resource, int amount, TransactionContext transaction, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ftbevo$journal().extract(resource, amount, transaction));
    }

    @Inject(
            method =
                    "insert(ILnet/neoforged/neoforge/transfer/fluid/FluidResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$deferIndexedInsert(
            int index,
            FluidResource resource,
            int amount,
            TransactionContext transaction,
            CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ftbevo$journal().insert(resource, amount, transaction));
    }

    @Inject(
            method =
                    "insert(Lnet/neoforged/neoforge/transfer/fluid/FluidResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$deferInsert(
            FluidResource resource, int amount, TransactionContext transaction, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ftbevo$journal().insert(resource, amount, transaction));
    }
}
