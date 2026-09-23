package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.EnergyTask;
import dev.ftb.mods.ftbquests.quest.task.FluidTask;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.Task;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jspecify.annotations.Nullable;

public final class PyramidHandlers {
    private final EvolutionPyramidBlockEntity machine;
    private final Items items = new Items();
    private final Fluids fluids = new Fluids();
    private final Energy energy = new Energy();

    PyramidHandlers(EvolutionPyramidBlockEntity machine) {
        this.machine = machine;
    }

    public ResourceHandler<ItemResource> items() {
        return items;
    }

    public ResourceHandler<FluidResource> fluids() {
        return fluids;
    }

    public EnergyHandler energy() {
        return energy;
    }

    @Nullable
    private <T extends Task> T target(Class<T> type) {
        Task task = machine.activeTask();
        return type.isInstance(task) ? type.cast(task) : null;
    }

    private long space(TeamData data, Task task, long pending) {
        return Math.max(0L, task.getMaxProgress() - data.getProgress(task) - pending);
    }

    private final class Pending extends SnapshotJournal<Long> {
        private long amount;

        long amount() {
            return amount;
        }

        void add(long delta, TransactionContext transaction) {
            updateSnapshots(transaction);
            amount += delta;
        }

        @Override
        protected Long createSnapshot() {
            return amount;
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            amount = snapshot;
        }

        @Override
        protected void onRootCommit(Long originalState) {
            long delivered = amount;
            amount = 0L;
            if (delivered > 0L) {
                machine.commitDelivery(delivered);
            }
        }
    }

    private final class Items implements ResourceHandler<ItemResource> {
        private final Pending pending = new Pending();

        @Override
        public int size() {
            return 1;
        }

        @Override
        public ItemResource getResource(int index) {
            return ItemResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index) {
            return 0L;
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            ItemTask task = target(ItemTask.class);
            TeamData data = machine.deliveryData(task);
            if (task == null || data == null || resource.isEmpty() || !task.test(resource.toStack())) return 0L;
            return space(data, task, pending.amount());
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            ItemTask task = target(ItemTask.class);
            return task != null && machine.deliveryData(task) != null && !resource.isEmpty() && task.test(resource.toStack());
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            ItemTask task = target(ItemTask.class);
            TeamData data = machine.deliveryData(task);
            if (task == null || data == null || amount <= 0 || resource.isEmpty() || !task.test(resource.toStack())) return 0;
            int accepted = (int) Math.min(amount, space(data, task, pending.amount()));
            if (accepted > 0) {
                pending.add(accepted, transaction);
            }
            return accepted;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }
    }

    private final class Fluids implements ResourceHandler<FluidResource> {
        private final Pending pending = new Pending();

        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            return FluidResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index) {
            return 0L;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            FluidTask task = target(FluidTask.class);
            TeamData data = machine.deliveryData(task);
            if (task == null || data == null || !matches(task, resource)) return 0L;
            return space(data, task, pending.amount());
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            FluidTask task = target(FluidTask.class);
            return task != null && machine.deliveryData(task) != null && matches(task, resource);
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            FluidTask task = target(FluidTask.class);
            TeamData data = machine.deliveryData(task);
            if (task == null || data == null || amount <= 0 || !matches(task, resource)) return 0;
            int accepted = (int) Math.min(amount, space(data, task, pending.amount()));
            if (accepted > 0) {
                pending.add(accepted, transaction);
            }
            return accepted;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        private static boolean matches(FluidTask task, FluidResource resource) {
            return !resource.isEmpty() && resource.getFluid() == task.getFluid();
        }
    }

    private final class Energy implements EnergyHandler {
        private final Pending pending = new Pending();

        @Override
        public long getAmountAsLong() {
            EnergyTask task = target(EnergyTask.class);
            TeamData data = machine.deliveryData(task);
            return task == null || data == null ? 0L : Math.min(task.getMaxProgress(), data.getProgress(task) + pending.amount());
        }

        @Override
        public long getCapacityAsLong() {
            EnergyTask task = target(EnergyTask.class);
            return task == null || machine.deliveryData(task) == null ? 0L : task.getMaxProgress();
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            EnergyTask task = target(EnergyTask.class);
            TeamData data = machine.deliveryData(task);
            if (task == null || data == null || amount <= 0) return 0;
            long limit = Math.min(task.getMaxInput(), space(data, task, pending.amount()));
            int accepted = (int) Math.min(amount, limit);
            if (accepted > 0) {
                pending.add(accepted, transaction);
            }
            return accepted;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            return 0;
        }
    }
}
