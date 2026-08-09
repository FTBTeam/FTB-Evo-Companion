package dev.ftb.mods.ftbevolutioncompanion.skills;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.skills.network.SkillsPayloads;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SkillsAbilities {
    public static final int ACTIVATE_NINJA = 0;
    public static final int ACTIVATE_SHADOW_STEP = 1;

    private static final double MELEE_REACH = 3.0;

    private SkillsAbilities() {
    }

    public static SkillToggles toggles(Player player) {
        return player.getData(SkillsRegistry.TOGGLES);
    }

    public static void handleToggle(ServerPlayer player, SkillToggles.Toggle toggle) {
        SkillToggles next = toggles(player).toggled(toggle);
        player.setData(SkillsRegistry.TOGGLES, next);
        PacketDistributor.sendToPlayer(player, new SkillsPayloads.SyncSkillToggles(next));
        String state = next.get(toggle) ? "on" : "off";
        player.sendOverlayMessage(
                Component.translatable("ftbevolutioncompanion.skills.toggle." + toggle.key() + "." + state));
    }

    public static void handleActivate(ServerPlayer player, int skill) {
        if (skill == ACTIVATE_NINJA) {
            activateNinja(player);
        } else if (skill == ACTIVATE_SHADOW_STEP) {
            activateShadowStep(player);
        }
    }

    private static void activateShadowStep(ServerPlayer player) {
        if (!player.isShiftKeyDown() || !SkillsHelper.isSword(player.getMainHandItem())
                || SkillsHelper.attr(player, SkillsRegistry.SHADOW_STEP) <= 0.0
                || !toggles(player).shadowStep()
                || !SkillCooldowns.ready(player, SkillCooldowns.SHADOW_STEP)) {
            return;
        }
        LivingEntity target = raycastTarget(player, CompanionConfig.SHADOW_STEP_RANGE.get());
        if (target != null) {
            shadowStep(player, target);
        }
    }

    private static LivingEntity raycastTarget(ServerPlayer player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 view = player.getViewVector(1.0F);
        Vec3 end = eye.add(view.scale(range));
        AABB search = player.getBoundingBox().expandTowards(view.scale(range)).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player, eye, end, search,
                entity -> entity instanceof LivingEntity && entity != player && entity.isAlive()
                        && !entity.isSpectator() && entity.isPickable(),
                range * range);
        if (hit == null || !(hit.getEntity() instanceof LivingEntity target)) {
            return null;
        }
        BlockHitResult blocked = player.level().clip(new ClipContext(eye, hit.getLocation(),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (blocked.getType() != HitResult.Type.MISS
                && blocked.getLocation().distanceToSqr(eye) < hit.getLocation().distanceToSqr(eye)) {
            return null;
        }
        return target;
    }

    public static boolean shadowStep(ServerPlayer player, LivingEntity target) {
        if (!SkillCooldowns.ready(player, SkillCooldowns.SHADOW_STEP)
                || !(player.level() instanceof ServerLevel level)) {
            return false;
        }
        Vec3 look = target.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            horizontal = Vec3.directionFromRotation(0.0F, target.getYRot());
        }
        horizontal = horizontal.normalize();

        Vec3 destination = null;
        for (double distance = 1.5; distance >= 0.5; distance -= 0.5) {
            Vec3 candidate = target.position().subtract(horizontal.scale(distance));
            if (level.noCollision(player, player.getBoundingBox().move(candidate.subtract(player.position())))) {
                destination = candidate;
                break;
            }
        }
        if (destination == null) {
            return false;
        }

        Vec3 origin = player.position();
        player.teleportTo(destination.x, destination.y, destination.z);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, target, EntityAnchorArgument.Anchor.EYES);
        SkillCooldowns.start(player, SkillCooldowns.SHADOW_STEP, CompanionConfig.SHADOW_STEP_COOLDOWN.get());
        level.playSound(null, origin.x, origin.y, origin.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, destination.x, destination.y, destination.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    public static boolean isWithinMeleeReach(ServerPlayer player, LivingEntity target) {
        double range = MELEE_REACH + target.getPickRadius();
        return target.getBoundingBox().distanceToSqr(player.getEyePosition()) <= range * range;
    }

    private static void activateNinja(ServerPlayer player) {
        double seconds = SkillsHelper.attr(player, SkillsRegistry.NINJA);
        if (seconds <= 0.0) {
            return;
        }
        if (!SkillCooldowns.ready(player, SkillCooldowns.NINJA)) {
            sendCooldown(player, "attribute.name.ftb.ninja", SkillCooldowns.remaining(player, SkillCooldowns.NINJA));
            return;
        }
        int ticks = (int) (seconds * 20.0);
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, ticks, 0, false, false, true));
        player.getData(SkillsRegistry.COMBAT_STATE).ninjaUntil = player.level().getGameTime() + ticks;
        SkillCooldowns.start(player, SkillCooldowns.NINJA, CompanionConfig.NINJA_COOLDOWN.get());
        player.sendOverlayMessage(Component.translatable("ftbevolutioncompanion.skills.ninja.activated"));
    }

    public static void sendCooldown(ServerPlayer player, String nameKey, long remainingTicks) {
        player.sendOverlayMessage(Component.translatable("ftbevolutioncompanion.skills.cooldown",
                Component.translatable(nameKey), String.valueOf(remainingTicks / 20)));
    }

    public static void syncToggles(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SkillsPayloads.SyncSkillToggles(toggles(player)));
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToggles(player);
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToggles(player);
        }
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToggles(player);
        }
    }
}
