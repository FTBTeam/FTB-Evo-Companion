package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.energizedpower;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "me.jddev0.ep.block.entity.CableBlockEntity", remap = false)
public abstract class CableNetworkExtractedEnergyMixin {
    @WrapOperation(
            method = "tick",
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/neoforged/neoforge/transfer/energy/EnergyHandler;"
                                + "extract(ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
                        ordinal = 1),
                @At(
                        value = "INVOKE",
                        target = "Lnet/neoforged/neoforge/transfer/energy/EnergyHandler;"
                                + "extract(ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
                        ordinal = 2)
            })
    private static int ftbevo$countExtracted(
            EnergyHandler handler,
            int amount,
            TransactionContext transaction,
            Operation<Integer> original,
            @Share("extracted") LocalIntRef extracted) {
        int actual = original.call(handler, amount, transaction);
        if (actual > 0) {
            extracted.set(extracted.get() + actual);
        }
        return actual;
    }

    @WrapOperation(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/neoforged/neoforge/transfer/energy/EnergyHandler;"
                                    + "insert(ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
                            ordinal = 1))
    private static int ftbevo$insertOnlyExtracted(
            EnergyHandler handler,
            int amount,
            TransactionContext transaction,
            Operation<Integer> original,
            @Share("extracted") LocalIntRef extracted) {
        int budget = extracted.get();
        if (budget <= 0) {
            return 0;
        }
        int inserted = original.call(handler, Math.min(amount, budget), transaction);
        extracted.set(budget - Math.max(inserted, 0));
        return inserted;
    }
}
