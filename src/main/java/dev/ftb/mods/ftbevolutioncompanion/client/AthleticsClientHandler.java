package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.athletics.network.AthleticsPayloads;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class AthleticsClientHandler {
    private static final double DASH_STRENGTH = 1.5;
    private static final double DASH_LIFT = 0.25;
    private static final double CLING_GRIP = 0.6;
    private static final double SLIDE_SPEED = -0.15;

    private static boolean jumpWasDown;
    private static int jumpsUsed;
    private static int dashesUsed;
    private static int clingTicks;

    private AthleticsClientHandler() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) {
            jumpWasDown = false;
            jumpsUsed = 0;
            dashesUsed = 0;
            clingTicks = 0;
            drainKeys();
            return;
        }

        while (AthleticsKeys.TOGGLE_EXTRA_JUMPS.consumeClick()) {
            ClientPacketDistributor.sendToServer(
                    new AthleticsPayloads.ToggleAbility(AthleticsAbilities.Ability.EXTRA_JUMPS.ordinal()));
        }
        while (AthleticsKeys.TOGGLE_WALL_CLIMB.consumeClick()) {
            ClientPacketDistributor.sendToServer(
                    new AthleticsPayloads.ToggleAbility(AthleticsAbilities.Ability.WALL_CLIMB.ordinal()));
        }
        while (AthleticsKeys.TOGGLE_AIR_DASH.consumeClick()) {
            ClientPacketDistributor.sendToServer(
                    new AthleticsPayloads.ToggleAbility(AthleticsAbilities.Ability.AIR_DASH.ordinal()));
        }

        if (isGroundedState(player)) {
            jumpsUsed = 0;
            dashesUsed = 0;
            clingTicks = 0;
        }

        tickWallCling(player);

        while (AthleticsKeys.AIR_DASH.consumeClick()) {
            tryAirDash(player);
        }

        boolean jumpDown = mc.options.keyJump.isDown();
        if (jumpDown && !jumpWasDown) {
            tryExtraJump(player);
        }
        jumpWasDown = jumpDown;
    }

    private static boolean isGroundedState(LocalPlayer player) {
        return player.onGround() || player.isInWater() || player.isInLava() || player.onClimbable();
    }

    private static void tickWallCling(LocalPlayer player) {
        if (!AthleticsAbilities.isWallClinging(player)) {
            return;
        }

        clingTicks++;
        Vec3 delta = player.getDeltaMovement();
        if (clingTicks <= AthleticsAbilities.maxClingTicks(player)) {
            player.setDeltaMovement(delta.x * CLING_GRIP, 0.0, delta.z * CLING_GRIP);
        } else {
            player.setDeltaMovement(delta.x, Math.max(delta.y, SLIDE_SPEED), delta.z);
        }
        player.resetFallDistance();
    }

    private static boolean isAirborne(LocalPlayer player) {
        return !isGroundedState(player)
                && !player.isFallFlying()
                && !player.getAbilities().flying
                && !player.isPassenger()
                && !player.isSpectator();
    }

    private static void tryExtraJump(LocalPlayer player) {
        if (!isAirborne(player) || jumpsUsed >= AthleticsAbilities.maxExtraJumps(player)) {
            return;
        }
        jumpsUsed++;
        player.jumpFromGround();
        ClientPacketDistributor.sendToServer(
                new AthleticsPayloads.AbilityAction(AthleticsAbilities.Ability.EXTRA_JUMPS.ordinal()));
    }

    private static void tryAirDash(LocalPlayer player) {
        if (!isAirborne(player) || dashesUsed >= AthleticsAbilities.maxAirDashes(player)) {
            return;
        }

        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            horizontal = Vec3.directionFromRotation(0.0F, player.getYRot());
        }
        horizontal = horizontal.normalize();

        dashesUsed++;
        player.setDeltaMovement(horizontal.x * DASH_STRENGTH,
                Math.max(player.getDeltaMovement().y, DASH_LIFT),
                horizontal.z * DASH_STRENGTH);
        ClientPacketDistributor.sendToServer(
                new AthleticsPayloads.AbilityAction(AthleticsAbilities.Ability.AIR_DASH.ordinal()));
    }

    private static void drainKeys() {
        while (AthleticsKeys.TOGGLE_EXTRA_JUMPS.consumeClick()
                || AthleticsKeys.TOGGLE_WALL_CLIMB.consumeClick()
                || AthleticsKeys.TOGGLE_AIR_DASH.consumeClick()
                || AthleticsKeys.AIR_DASH.consumeClick()) {
        }
    }
}
