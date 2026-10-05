package dev.ftb.mods.ftbevolutioncompanion.compat.oceanmobs;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

public final class RiftArena {
    public static final Identifier RIFT_WEAVER = Identifier.fromNamespaceAndPath("ftboceanmobs", "rift_weaver");
    public static final int TRACKING_RANGE = 128;

    private static final int SCAN_START = 8;
    private static final int SCAN_DEPTH = 48;

    private static Level activeLevel;
    private static BlockPos anchor;

    private RiftArena() {}

    public static boolean isRiftWeaver(EntityType<?> type) {
        return RIFT_WEAVER.equals(EntityType.getKey(type));
    }

    public static void begin(Level level, BlockPos spawnPos) {
        activeLevel = level;
        anchor = spawnPos;
    }

    public static void end() {
        activeLevel = null;
        anchor = null;
    }

    public static int height(Level level, Heightmap.Types type, int x, int z, int original) {
        BlockPos home = anchor;
        if (home == null || level != activeLevel) {
            return original;
        }
        int start = home.getY() + SCAN_START;
        if (original <= start) {
            return original;
        }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, start, z);
        if (!level.getBlockState(pos).isAir()) {
            return home.getY();
        }
        int bottom = Math.max(level.getMinY(), start - SCAN_DEPTH);
        for (int y = start - 1; y >= bottom; y--) {
            pos.setY(y);
            if (type.isOpaque().test(level.getBlockState(pos))) {
                return y + 1;
            }
        }
        return home.getY();
    }

    public static BlockPos heightmapPos(Level level, Heightmap.Types type, BlockPos pos, BlockPos original) {
        int y = height(level, type, pos.getX(), pos.getZ(), original.getY());
        return y == original.getY() ? original : new BlockPos(pos.getX(), y, pos.getZ());
    }

    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getSpawnType() != EntitySpawnReason.TRIAL_SPAWNER || !isRiftWeaver(event.getEntityType())) {
            return;
        }
        ServerLevel level = event.getLevel().getLevel();
        AABB area = new AABB(event.getPos()).inflate(TRACKING_RANGE);
        boolean alreadyAlive = !level.getEntities((Entity) null, area, e -> e.isAlive() && isRiftWeaver(e.getType()))
                .isEmpty();
        event.setResult(
                alreadyAlive
                        ? MobSpawnEvent.SpawnPlacementCheck.Result.FAIL
                        : MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED);
    }
}
