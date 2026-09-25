package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import dev.ftb.mods.ftbevolutioncompanion.CompanionSounds;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class SkylineBlockEntity extends BlockEntity implements GeoBlockEntity {
    public static final String CONTROLLER = "main";
    public static final String LAUNCH_ANIMATION = "objective_complete";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.ftb_skyline.idle");
    private static final RawAnimation TRANSPORTING = RawAnimation.begin().thenLoop("animation.ftb_skyline.transporting");
    private static final RawAnimation LAUNCH = RawAnimation.begin().thenPlay("animation.ftb_skyline.objective_complete");
    private static final int REMOVE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS;
    private static final int TRANSPORT_LINGER_TICKS = 60;
    private static final int LAUNCH_COMPLETE_TICK = 50;
    private static final int LAUNCH_END_TICK = 160;
    private static final int SYNC_INTERVAL = 5;
    private static final int REFRESH_INTERVAL = 20;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final PyramidHandlers handlers = new PyramidHandlers(this);

    @Nullable
    private UUID owner;
    private long activeTaskId;
    private long displayProgress;
    private boolean transporting;
    private long lastDeliveryTick;
    private long launchQuestId;
    private int launchTicks = -1;
    @Nullable
    private UUID launchPlayer;

    private boolean syncPending;
    @Nullable
    private TeamData cachedData;
    @Nullable
    private Chapter cachedChapter;
    private long cachedDataTick = Long.MIN_VALUE;

    public SkylineBlockEntity(BlockPos pos, BlockState state) {
        super(PyramidRegistry.SKYLINE.get(), pos, state);
    }

    public PyramidHandlers handlers() {
        return handlers;
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        cachedDataTick = Long.MIN_VALUE;
        sync();
    }

    public long getActiveTaskId() {
        return activeTaskId;
    }

    public long getDisplayProgress() {
        return displayProgress;
    }

    public boolean isTransporting() {
        return transporting;
    }

    public boolean isLaunching() {
        return launchTicks >= 0;
    }

    @Nullable
    Task activeTask() {
        if (activeTaskId == 0L || level == null || level.isClientSide() || !ServerQuestFile.exists()) return null;
        return ServerQuestFile.getInstance().getTask(activeTaskId);
    }

    private void refreshCache() {
        if (level == null) return;
        long now = level.getGameTime();
        if (cachedDataTick != now) {
            cachedData = PyramidQuests.teamData(owner);
            cachedChapter = PyramidQuests.chapter();
            cachedDataTick = now;
        }
    }

    @Nullable
    TeamData teamData() {
        if (level == null || level.isClientSide()) return null;
        refreshCache();
        return cachedData;
    }

    @Nullable
    Chapter chapter() {
        if (level == null || level.isClientSide()) return null;
        refreshCache();
        return cachedChapter;
    }

    @Nullable
    TeamData deliveryData(@Nullable Task task) {
        if (task == null || isLaunching()) return null;
        TeamData data = teamData();
        if (data == null) return null;
        if (!PyramidQuests.inChapter(task.getQuest(), chapter()) || !PyramidQuests.canDeliver(data, task)) return null;
        return data;
    }

    void commitDelivery(long amount) {
        Task task = activeTask();
        TeamData data = deliveryData(task);
        if (task == null || data == null || level == null) return;
        data.setProgress(task, data.getProgress(task) + amount);
        lastDeliveryTick = level.getGameTime();
        if (!transporting) {
            transporting = true;
            syncPending = true;
        }
        if (data.isCompleted(task)) {
            advance(data, task.getQuest());
        }
        refreshProgress(data);
    }

    public boolean selectTask(long taskId) {
        TeamData data = teamData();
        if (data == null || isLaunching() || !ServerQuestFile.exists()) return false;
        Task task = ServerQuestFile.getInstance().getTask(taskId);
        if (task == null || !PyramidQuests.inChapter(task.getQuest(), chapter()) || !PyramidQuests.canDeliver(data, task)) return false;
        activeTaskId = task.id;
        refreshProgress(data);
        sync();
        return true;
    }

    public boolean launch(ServerPlayer player, long questId) {
        TeamData data = teamData();
        if (data == null || isLaunching() || !ServerQuestFile.exists() || !(level instanceof ServerLevel serverLevel)) return false;
        Quest quest = ServerQuestFile.getInstance().getQuest(questId);
        if (quest == null || !PyramidQuests.inChapter(quest, chapter()) || !PyramidQuests.isReadyToLaunch(data, quest)) return false;
        launchQuestId = quest.id;
        launchPlayer = player.getUUID();
        launchTicks = 0;
        triggerAnim(CONTROLLER, LAUNCH_ANIMATION);
        serverLevel.playSound(null, worldPosition.above(3), CompanionSounds.SKYLINE_LAUNCH_START.get(), SoundSource.BLOCKS, 2F, 1F);
        sync();
        return true;
    }

    private void completeLaunch(ServerLevel serverLevel) {
        TeamData data = teamData();
        Quest quest = ServerQuestFile.exists() ? ServerQuestFile.getInstance().getQuest(launchQuestId) : null;
        LaunchTask task = quest == null ? null : PyramidQuests.launchTask(quest);
        if (data != null && task != null && PyramidQuests.isReadyToLaunch(data, quest)) {
            ServerPlayer player = launchPlayer == null ? null : serverLevel.getServer().getPlayerList().getPlayer(launchPlayer);
            if (player != null) {
                ServerQuestFile.getInstance().withPlayerContext(player, () -> data.setProgress(task, 1L));
            } else {
                data.setProgress(task, 1L);
            }
        }
        serverLevel.playSound(null, worldPosition.above(5), CompanionSounds.SKYLINE_LIFTOFF.get(), SoundSource.BLOCKS, 3F, 1F);
        Task active = activeTask();
        if (active != null && active.getQuest().id == launchQuestId) {
            activeTaskId = 0L;
            displayProgress = 0L;
        }
    }

    private void advance(TeamData data, Quest quest) {
        Task next = PyramidQuests.nextTask(data, quest);
        long nextId = next == null ? 0L : next.id;
        if (nextId != activeTaskId) {
            activeTaskId = nextId;
            syncPending = true;
        }
    }

    private void refreshProgress(TeamData data) {
        Task task = activeTask();
        long progress = task == null ? 0L : data.getProgress(task);
        if (progress != displayProgress) {
            displayProgress = progress;
            syncPending = true;
        }
    }

    private void validateActiveTask(TeamData data) {
        Task task = activeTask();
        if (task == null) {
            if (activeTaskId != 0L) {
                activeTaskId = 0L;
                syncPending = true;
            }
            return;
        }
        Quest quest = task.getQuest();
        if (!PyramidQuests.inChapter(quest, chapter()) || !PyramidQuests.isAvailable(data, quest)) {
            activeTaskId = 0L;
            syncPending = true;
        } else if (data.isCompleted(task)) {
            advance(data, quest);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SkylineBlockEntity machine) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        long now = level.getGameTime();

        if (machine.transporting && now - machine.lastDeliveryTick > TRANSPORT_LINGER_TICKS) {
            machine.transporting = false;
            machine.syncPending = true;
        }

        if (machine.launchTicks >= 0) {
            machine.launchTicks++;
            if (machine.launchTicks == LAUNCH_COMPLETE_TICK) {
                machine.completeLaunch(serverLevel);
                machine.syncPending = true;
            }
            if (machine.launchTicks >= LAUNCH_END_TICK) {
                machine.launchTicks = -1;
                machine.launchQuestId = 0L;
                machine.launchPlayer = null;
                machine.syncPending = true;
            }
            machine.setChanged();
        }

        if (now % REFRESH_INTERVAL == 0L && !machine.isLaunching()) {
            TeamData data = machine.teamData();
            if (data != null) {
                machine.validateActiveTask(data);
                machine.refreshProgress(data);
            }
        }

        if (machine.syncPending && now % SYNC_INTERVAL == 0L) {
            machine.sync();
        }
    }

    private void sync() {
        syncPending = false;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel) || !state.hasProperty(SkylineBlock.FACING)) return;
        for (BlockPos partPos : PyramidLayout.positions(pos, state.getValue(SkylineBlock.FACING))) {
            if (!partPos.equals(pos) && SkylineBlock.isPartOf(serverLevel, partPos, pos)) {
                serverLevel.setBlock(partPos, Blocks.AIR.defaultBlockState(), REMOVE_FLAGS);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<SkylineBlockEntity>(CONTROLLER, 5,
                test -> test.setAndContinue(test.animatable().isTransporting() ? TRANSPORTING : IDLE))
                .triggerableAnim(LAUNCH_ANIMATION, LAUNCH));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        owner = input.read("owner", UUIDUtil.CODEC).orElse(null);
        activeTaskId = input.getLongOr("active_task", 0L);
        displayProgress = input.getLongOr("progress", 0L);
        transporting = input.getBooleanOr("transporting", false);
        launchQuestId = input.getLongOr("launch_quest", 0L);
        launchTicks = input.getIntOr("launch_ticks", -1);
        launchPlayer = input.read("launch_player", UUIDUtil.CODEC).orElse(null);
        cachedDataTick = Long.MIN_VALUE;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("owner", UUIDUtil.CODEC, owner);
        output.putLong("active_task", activeTaskId);
        output.putLong("progress", displayProgress);
        output.putBoolean("transporting", transporting);
        output.putLong("launch_quest", launchQuestId);
        output.putInt("launch_ticks", launchTicks);
        output.storeNullable("launch_player", UUIDUtil.CODEC, launchPlayer);
    }
}
