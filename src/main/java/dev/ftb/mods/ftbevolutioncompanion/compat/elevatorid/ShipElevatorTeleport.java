package dev.ftb.mods.ftbevolutioncompanion.compat.elevatorid;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Set;

public final class ShipElevatorTeleport {
    private static final ResourceLocation TELEPORT_SOUND =
            ResourceLocation.fromNamespaceAndPath(ShipElevators.MOD_ID, "teleport");

    private ShipElevatorTeleport() {}

    public static void teleport(ServerPlayer player, BlockPos from, BlockPos to) {
        if (!player.isAlive() || player.isRemoved() || player.isSpectator()) {
            return;
        }
        if (from.getX() != to.getX() || from.getZ() != to.getZ() || from.getY() == to.getY()) {
            return;
        }
        if (Math.abs(to.getY() - from.getY()) > ShipElevators.RANGE) {
            return;
        }

        SubLevel subLevel = Sable.HELPER.getTrackingSubLevel(player);
        if (subLevel == null) {
            return;
        }
        Level level = subLevel.getLevel();
        if (!(level instanceof ServerLevel serverLevel) || serverLevel != player.level()) {
            return;
        }
        if (!level.isLoaded(from) || !level.isLoaded(to)) {
            return;
        }

        Vec3 local = subLevel.logicalPose().transformPositionInverse(player.position());
        if (BlockPos.containing(local).distManhattan(from) > ShipElevators.ACTIVATION_RANGE + 2) {
            return;
        }

        BlockState fromState = level.getBlockState(from);
        BlockState toState = level.getBlockState(to);
        if (!ShipElevators.isElevator(fromState) || !ShipElevators.isElevator(toState)) {
            return;
        }
        if (!ShipElevators.colorsMatch(fromState, toState) || !ShipElevators.hasHeadroom(level, to)) {
            return;
        }

        VoxelShape support = toState.getBlockSupportShape(level, to);
        double top = support.isEmpty() ? 1.0 : support.max(Direction.Axis.Y);
        Vec3 localTarget = new Vec3(to.getX() + 0.5, to.getY() + top, to.getZ() + 0.5);
        Vec3 worldTarget = subLevel.logicalPose().transformPosition(localTarget);

        player.teleportTo(serverLevel, worldTarget.x, worldTarget.y, worldTarget.z,
                Set.of(), player.getYRot(), player.getXRot());
        player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 0.0, 1.0));
        player.hasImpulse = true;

        BuiltInRegistries.SOUND_EVENT.getOptional(TELEPORT_SOUND).ifPresent(sound ->
                serverLevel.playSound(null, BlockPos.containing(worldTarget), sound, SoundSource.BLOCKS, 1.0F, 1.0F));
    }
}
