package dev.ftb.mods.ftbevolutioncompanion.compat.colossalreactors;

import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class DeferredFluidJournal extends SnapshotJournal<List<DeferredFluidJournal.Op>> {
    public record Op(boolean drain, FluidStack stack) {}

    private final IFluidHandler delegate;
    private List<Op> ops = new ArrayList<>();

    public DeferredFluidJournal(IFluidHandler delegate) {
        this.delegate = delegate;
    }

    public int extract(FluidResource resource, int amount, TransactionContext transaction) {
        if (amount <= 0 || resource.isEmpty()) return 0;
        int pending = pending(resource, true);
        FluidStack simulated =
                delegate.drain(resource.toStack(saturatedAdd(amount, pending)), IFluidHandler.FluidAction.SIMULATE);
        int available = simulated.isEmpty() || !resource.matches(simulated) ? 0 : simulated.getAmount() - pending;
        int result = Math.max(0, Math.min(amount, available));
        if (result > 0) record(new Op(true, resource.toStack(result)), transaction);
        return result;
    }

    public int insert(FluidResource resource, int amount, TransactionContext transaction) {
        if (amount <= 0 || resource.isEmpty()) return 0;
        int pending = pending(resource, false);
        int simulated =
                delegate.fill(resource.toStack(saturatedAdd(amount, pending)), IFluidHandler.FluidAction.SIMULATE);
        int result = Math.max(0, Math.min(amount, simulated - pending));
        if (result > 0) record(new Op(false, resource.toStack(result)), transaction);
        return result;
    }

    private void record(Op op, TransactionContext transaction) {
        updateSnapshots(transaction);
        ops.add(op);
    }

    private int pending(FluidResource resource, boolean drain) {
        int total = 0;
        for (Op op : ops) {
            if (op.drain() == drain && resource.matches(op.stack())) {
                total = saturatedAdd(total, op.stack().getAmount());
            }
        }
        return total;
    }

    private static int saturatedAdd(int a, int b) {
        long sum = (long) a + b;
        return sum > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sum;
    }

    @Override
    protected List<Op> createSnapshot() {
        return List.copyOf(ops);
    }

    @Override
    protected void revertToSnapshot(List<Op> snapshot) {
        ops = new ArrayList<>(snapshot);
    }

    @Override
    protected void onRootCommit(List<Op> originalState) {
        List<Op> committed = ops;
        ops = new ArrayList<>();
        for (Op op : committed) {
            if (op.drain()) {
                delegate.drain(op.stack().copy(), IFluidHandler.FluidAction.EXECUTE);
            } else {
                delegate.fill(op.stack().copy(), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }
}
