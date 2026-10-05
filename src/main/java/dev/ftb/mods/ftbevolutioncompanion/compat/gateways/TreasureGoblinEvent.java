package dev.ftb.mods.ftbevolutioncompanion.compat.gateways;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.compat.kubejs.PlayerEventToggles;
import dev.ftb.mods.ftbevolutioncompanion.compat.sgeconomy.ShopCoins;
import dev.ftb.mods.ftbevolutioncompanion.mixin.MobAccessor;
import dev.shadowsoffire.gateways.entity.GatewayEntity;
import dev.shadowsoffire.gateways.event.GateEvent;
import dev.shadowsoffire.gateways.gate.Gateway;
import dev.shadowsoffire.gateways.gate.GatewayRegistry;
import dev.shadowsoffire.placebo.dynreg.DynamicHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class TreasureGoblinEvent {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FTBEvolutionCompanion.MOD_ID);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> LAST_EVENT = ATTACHMENTS.register(
            "treasure_goblin_last",
            () -> AttachmentType.builder(() -> Long.MIN_VALUE)
                    .serialize(Codec.LONG.fieldOf("last"))
                    .copyOnDeath()
                    .build());

    private static final Identifier GATEWAY = Identifier.fromNamespaceAndPath("ftb", "treasure_goblin");
    private static final String GOBLIN_TAG = "ftbevo.treasure_goblin";
    private static final String EVENT_NAME = "ftb:treasure_goblin";
    private static final float CHANCE = 0.01F;
    private static final long COOLDOWN_TICKS = 2L * 60L * 60L * 20L;
    private static final double FIGHT_RANGE = 16.0;
    private static final int MIN_HOSTILES = 3;
    private static final int ESCAPE_TICKS = 200;
    private static final int MAX_COINS = 50;
    private static final int MIN_COINS = 1;
    private static final double GOBLIN_SPEED = 1.4;
    private static final double ESCAPE_DISTANCE_SQ = 2.25;

    private static final Map<UUID, Active> ACTIVE = new HashMap<>();

    private TreasureGoblinEvent() {}

    public static void onDamage(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Enemy)
                || !(target.level() instanceof ServerLevel level)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || player.isSpectator()) {
            return;
        }
        if (level.getRandom().nextFloat() >= CHANCE
                || hasActiveEvent(player)
                || PlayerEventToggles.isDisabled(player, EVENT_NAME)) {
            return;
        }
        long now = level.getGameTime();
        long last = player.getData(LAST_EVENT);
        if (last != Long.MIN_VALUE && now - last < COOLDOWN_TICKS) {
            return;
        }
        int hostiles = level.getEntitiesOfClass(
                        Mob.class, player.getBoundingBox().inflate(FIGHT_RANGE), m -> m instanceof Enemy && m.isAlive())
                .size();
        if (hostiles < MIN_HOSTILES) {
            return;
        }
        if (start(player)) {
            player.setData(LAST_EVENT, now);
        }
    }

    public static boolean start(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level) || hasActiveEvent(player)) {
            return false;
        }
        BlockPos portalPos = findOpenSpot(level, player.blockPosition(), 12, 16);
        if (portalPos == null) {
            return false;
        }
        DynamicHolder<Gateway> holder = GatewayRegistry.INSTANCE.holder(GATEWAY);
        if (!holder.isBound()) {
            return false;
        }
        GatewayEntity gate = holder.get().createEntity(level, player);
        gate.setPos(portalPos.getX() + 0.5, portalPos.getY(), portalPos.getZ() + 0.5);
        if (!level.addFreshEntity(gate)) {
            return false;
        }
        ACTIVE.put(gate.getUUID(), new Active(player.getUUID(), level.dimension()));
        player.connection.send(new ClientboundSetTitlesAnimationPacket(5, 50, 10));
        player.connection.send(new ClientboundSetTitleTextPacket(
                Component.translatable("ftbevolutioncompanion.treasure_goblin.title").withStyle(ChatFormatting.GOLD)));
        player.connection.send(new ClientboundSetSubtitleTextPacket(
                Component.translatable("ftbevolutioncompanion.treasure_goblin.subtitle")
                        .withStyle(ChatFormatting.YELLOW)));
        return true;
    }

    public static void onWaveEntitySpawned(GateEvent.WaveEntitySpawned event) {
        GatewayEntity gate = event.getEntity();
        Active active = ACTIVE.get(gate.getUUID());
        if (active == null
                || !(event.getWaveEntity() instanceof PathfinderMob goblin)
                || !(gate.level() instanceof ServerLevel level)) {
            return;
        }
        goblin.addTag(GOBLIN_TAG);
        ServerPlayer player = level.getServer().getPlayerList().getPlayer(active.player);
        if (player != null && player.level() == level) {
            BlockPos start = findOpenSpot(level, player.blockPosition(), 2, 4);
            if (start != null) {
                goblin.snapTo(start.getX() + 0.5, start.getY(), start.getZ() + 0.5, goblin.getYRot(), 0.0F);
            }
        }
        MobAccessor accessor = (MobAccessor) goblin;
        accessor.ftbevo$getGoalSelector().removeAllGoals(goal -> true);
        accessor.ftbevo$getTargetSelector().removeAllGoals(goal -> true);
        accessor.ftbevo$getGoalSelector().addGoal(0, new RunToPortalGoal(goblin, gate, GOBLIN_SPEED));
        active.goblin = goblin.getUUID();
        active.waveStart = level.getGameTime();
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.entityTags().contains(GOBLIN_TAG) || !(entity.level() instanceof ServerLevel level)) {
            return;
        }
        Active active = findByGoblin(entity.getUUID());
        if (active == null) {
            return;
        }
        active.killed = true;
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) {
            return;
        }
        long elapsed = Math.max(0L, level.getGameTime() - active.waveStart);
        int coins = Mth.clamp(
                Math.round(MAX_COINS - (MAX_COINS - MIN_COINS) * (elapsed / (float) ESCAPE_TICKS)), MIN_COINS, MAX_COINS);
        if (ShopCoins.deposit(killer, coins)) {
            killer.sendSystemMessage(Component.translatable(
                            "ftbevolutioncompanion.treasure_goblin.coins",
                            coins,
                            String.format("%.1f", elapsed / 20.0F))
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget().entityTags().contains(GOBLIN_TAG)) {
            event.setCanceled(true);
        }
    }

    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget().entityTags().contains(GOBLIN_TAG)) {
            event.setCanceled(true);
        }
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (ACTIVE.isEmpty()) {
            return;
        }
        MinecraftServer server = event.getServer();
        Iterator<Map.Entry<UUID, Active>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Active> entry = it.next();
            Active active = entry.getValue();
            ServerLevel level = server.getLevel(active.dimension);
            Entity gate = level == null ? null : level.getEntity(entry.getKey());
            Entity goblin = level == null || active.goblin == null ? null : level.getEntity(active.goblin);
            if (gate == null || gate.isRemoved()) {
                if (!active.killed && !active.escaped) {
                    notifyEscaped(server, active);
                }
                it.remove();
                continue;
            }
            if (goblin != null && goblin.isAlive() && !active.escaped && goblin.distanceToSqr(gate) < ESCAPE_DISTANCE_SQ) {
                active.escaped = true;
                notifyEscaped(server, active);
                goblin.discard();
            }
        }
    }

    private static void notifyEscaped(MinecraftServer server, Active active) {
        ServerPlayer player = server.getPlayerList().getPlayer(active.player);
        if (player != null) {
            player.sendSystemMessage(Component.translatable("ftbevolutioncompanion.treasure_goblin.escaped")
                    .withStyle(ChatFormatting.RED));
        }
    }

    private static boolean hasActiveEvent(ServerPlayer player) {
        for (Active active : ACTIVE.values()) {
            if (active.player.equals(player.getUUID())) {
                return true;
            }
        }
        return false;
    }

    private static Active findByGoblin(UUID goblin) {
        for (Active active : ACTIVE.values()) {
            if (goblin.equals(active.goblin)) {
                return active;
            }
        }
        return null;
    }

    private static BlockPos findOpenSpot(ServerLevel level, BlockPos origin, int minDistance, int maxDistance) {
        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
            double distance = minDistance + level.getRandom().nextDouble() * (maxDistance - minDistance);
            int x = origin.getX() + Mth.floor(Math.cos(angle) * distance);
            int z = origin.getZ() + Mth.floor(Math.sin(angle) * distance);
            for (int dy : new int[] {0, 1, -1, 2, -2, 3, -3}) {
                BlockPos pos = new BlockPos(x, origin.getY() + dy, z);
                if (level.isLoaded(pos)
                        && level.getBlockState(pos).isAir()
                        && level.getBlockState(pos.above()).isAir()
                        && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                    return pos;
                }
            }
        }
        return null;
    }

    private static final class Active {
        private final UUID player;
        private final ResourceKey<Level> dimension;
        private UUID goblin;
        private long waveStart;
        private boolean killed;
        private boolean escaped;

        private Active(UUID player, ResourceKey<Level> dimension) {
            this.player = player;
            this.dimension = dimension;
        }
    }
}
