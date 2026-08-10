package dev.ftb.mods.ftbevolutioncompanion.compat.elevatorid.client;

import dev.ftb.mods.ftbevolutioncompanion.compat.elevatorid.ShipElevators;
import dev.ftb.mods.ftbevolutioncompanion.network.ShipElevatorTeleportPayload;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public final class ShipElevatorClientHandler {
    private static boolean lastSneaking;
    private static boolean lastJumping;

    private ShipElevatorClientHandler() {}

    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || !player.isAlive() || player.input == null) {
            return;
        }

        boolean sneaking = player.input.shiftKeyDown;
        if (sneaking != lastSneaking) {
            lastSneaking = sneaking;
            if (sneaking) {
                tryTeleport(player, Direction.DOWN);
            }
        }

        boolean jumping = player.input.jumping;
        if (jumping != lastJumping) {
            lastJumping = jumping;
            if (jumping) {
                tryTeleport(player, Direction.UP);
            }
        }
    }

    private static void tryTeleport(LocalPlayer player, Direction direction) {
        SubLevel subLevel = Sable.HELPER.getTrackingSubLevel(player);
        if (subLevel == null) {
            return;
        }
        Level level = subLevel.getLevel();
        if (level != player.level()) {
            return;
        }

        BlockPos origin = findOrigin(player, subLevel, level);
        if (origin == null) {
            return;
        }
        BlockState originState = level.getBlockState(origin);

        BlockPos.MutableBlockPos cursor = origin.mutable();
        while (true) {
            cursor.setY(cursor.getY() + direction.getStepY());
            if (level.isOutsideBuildHeight(cursor)) {
                return;
            }
            if (Math.abs(cursor.getY() - origin.getY()) > ShipElevators.RANGE) {
                return;
            }
            BlockState state = level.getBlockState(cursor);
            if (!ShipElevators.isElevator(state)
                    || !ShipElevators.hasHeadroom(level, cursor)
                    || !ShipElevators.colorsMatch(originState, state)) {
                continue;
            }
            PacketDistributor.sendToServer(new ShipElevatorTeleportPayload(origin, cursor.immutable()));
            return;
        }
    }

    @Nullable
    private static BlockPos findOrigin(LocalPlayer player, SubLevel subLevel, Level level) {
        Vec3 local = subLevel.logicalPose().transformPositionInverse(player.position());
        BlockPos pos = BlockPos.containing(local);
        for (int i = 0; i < ShipElevators.ACTIVATION_RANGE; i++) {
            if (ShipElevators.isElevator(level.getBlockState(pos))) {
                return ShipElevators.hasHeadroom(level, pos) ? pos : null;
            }
            pos = pos.below();
        }
        return null;
    }
}
