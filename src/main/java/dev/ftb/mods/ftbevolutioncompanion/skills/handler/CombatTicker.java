package dev.ftb.mods.ftbevolutioncompanion.skills.handler;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.skills.ApothicHooks;
import dev.ftb.mods.ftbevolutioncompanion.skills.CombatState;
import dev.ftb.mods.ftbevolutioncompanion.skills.HomingState;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillCooldowns;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class CombatTicker {
    private static final Identifier FASTER_STRIKES_ID = FTBEvolutionCompanion.id("faster_strikes");
    private static final Identifier SHIELD_MASTERY_ARMOR_ID = FTBEvolutionCompanion.id("shield_mastery_armor");
    private static final Identifier SHIELD_MASTERY_TOUGHNESS_ID = FTBEvolutionCompanion.id("shield_mastery_toughness");
    private static final Identifier AXE_DESPERATION_ID = FTBEvolutionCompanion.id("axe_desperation");

    private CombatTicker() {
    }

    public static void onDamagePost(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer attacker) || event.getEntity() == attacker) {
            return;
        }
        LivingEntity target = event.getEntity();
        CombatState state = attacker.getData(SkillsRegistry.COMBAT_STATE);
        long now = attacker.level().getGameTime();

        if (source.isDirect() && source.getDirectEntity() == attacker) {
            if (SkillsHelper.isUnarmed(attacker)) {
                state.unarmedHitCounter++;
                state.lastUnarmedHitTime = now;
                state.unarmedRampStacks = Math.min(state.unarmedRampStacks + 1,
                        CompanionConfig.UNARMED_RAMP_MAX_STACKS.get());
                if (SkillsHelper.attr(attacker, SkillsRegistry.FASTER_STRIKES) > 0.0
                        && SkillsAbilities.toggles(attacker).fasterStrikes()) {
                    state.fasterStrikesStacks = Math.min(state.fasterStrikesStacks + 1,
                            CompanionConfig.FASTER_STRIKES_MAX_STACKS.get());
                }
            } else {
                resetUnarmed(state);
            }
            if (SkillsHelper.isAxe(attacker.getMainHandItem())) {
                handleLightning(attacker, target, state);
            } else {
                state.axeHitCounter = 0;
            }
        } else if (source.getDirectEntity() instanceof AbstractArrow) {
            resetUnarmed(state);
            int maxStacks = (int) SkillsHelper.attr(attacker, SkillsRegistry.RAMPING_SHOTS);
            if (maxStacks > 0) {
                if (now - state.lastArcherHitTime > CompanionConfig.ARCHER_RAMP_TIMEOUT.get()) {
                    state.archerRampStacks = 0;
                }
                state.archerRampStacks = Math.min(state.archerRampStacks + 1, maxStacks);
                state.lastArcherHitTime = now;
            }
        }
    }

    private static void resetUnarmed(CombatState state) {
        state.unarmedHitCounter = 0;
        state.unarmedRampStacks = 0;
        state.fasterStrikesStacks = 0;
    }

    private static void handleLightning(ServerPlayer attacker, LivingEntity target, CombatState state) {
        if (SkillsHelper.attr(attacker, SkillsRegistry.LIGHTNING_STRIKES) <= 0.0
                || !SkillsAbilities.toggles(attacker).lightning()) {
            state.axeHitCounter = 0;
            return;
        }
        state.axeHitCounter++;
        if (state.axeHitCounter < 3) {
            return;
        }
        state.axeHitCounter = 0;
        if (!SkillCooldowns.ready(attacker, SkillCooldowns.LIGHTNING)) {
            return;
        }
        if (attacker.level() instanceof ServerLevel serverLevel) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.spawn(serverLevel, target.blockPosition(),
                    EntitySpawnReason.TRIGGERED);
            if (bolt != null) {
                bolt.setCause(attacker);
            }
            SkillCooldowns.start(attacker, SkillCooldowns.LIGHTNING, CompanionConfig.LIGHTNING_COOLDOWN.get());
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer) || event.getEntity() == killer) {
            return;
        }
        if (SkillsHelper.isUnarmed(killer)) {
            double fraction = SkillsHelper.attr(killer, SkillsRegistry.UNARMED_KILL_HEAL);
            if (fraction > 0.0) {
                killer.heal((float) (fraction * killer.getMaxHealth()));
            }
        } else if (SkillsHelper.isAxe(killer.getMainHandItem())) {
            double flat = SkillsHelper.attr(killer, SkillsRegistry.BLOODLUST);
            if (flat > 0.0) {
                killer.heal((float) flat);
            }
        }
    }

    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof AbstractArrow arrow
                && arrow.getOwner() instanceof ServerPlayer player
                && event.getRayTraceResult().getType() == HitResult.Type.BLOCK) {
            if (tryHomingRedirect(arrow, player)) {
                event.setCanceled(true);
                return;
            }
            player.getData(SkillsRegistry.COMBAT_STATE).archerRampStacks = 0;
        }
    }

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()
                || !(event.getEntity() instanceof AbstractArrow arrow)
                || arrow instanceof ThrownTrident
                || !(arrow.getOwner() instanceof ServerPlayer shooter)
                || SkillsHelper.attr(shooter, SkillsRegistry.HOMING_ARROWS) <= 0.0) {
            return;
        }
        LivingEntity target = findHomingTarget(shooter, arrow);
        if (target != null) {
            arrow.getData(SkillsRegistry.HOMING_STATE).targetId = target.getUUID();
        }
    }

    private static LivingEntity findHomingTarget(ServerPlayer shooter, AbstractArrow arrow) {
        Vec3 direction = arrow.getDeltaMovement();
        if (direction.lengthSqr() < 1.0E-4) {
            direction = shooter.getLookAngle();
        }
        direction = direction.normalize();
        Vec3 start = arrow.position();
        double range = 48.0;
        AABB search = arrow.getBoundingBox().expandTowards(direction.scale(range)).inflate(8.0);
        LivingEntity best = null;
        double bestDot = 0.9;
        for (LivingEntity candidate : shooter.level().getEntitiesOfClass(LivingEntity.class, search,
                entity -> entity != shooter && entity.isAlive() && !entity.isSpectator()
                        && !(entity instanceof Player other && !shooter.canHarmPlayer(other)))) {
            Vec3 toCandidate = candidate.getBoundingBox().getCenter().subtract(start);
            double distance = toCandidate.length();
            if (distance < 1.0E-4 || distance > range) {
                continue;
            }
            double dot = toCandidate.normalize().dot(direction);
            if (dot > bestDot) {
                bestDot = dot;
                best = candidate;
            }
        }
        return best;
    }

    private static boolean tryHomingRedirect(AbstractArrow arrow, ServerPlayer player) {
        if (SkillsHelper.attr(player, SkillsRegistry.HOMING_ARROWS) <= 0.0
                || !arrow.hasData(SkillsRegistry.HOMING_STATE)
                || !(arrow.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        HomingState state = arrow.getData(SkillsRegistry.HOMING_STATE);
        if (state.targetId == null || state.retargets >= 3) {
            return false;
        }
        if (!(serverLevel.getEntity(state.targetId) instanceof LivingEntity target)
                || !target.isAlive() || arrow.distanceTo(target) > 64.0) {
            return false;
        }
        state.retargets++;
        double speed = Math.max(arrow.getDeltaMovement().length(), 1.0);
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(arrow.position());
        double horizontal = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        Vec3 aimed = new Vec3(toTarget.x, toTarget.y + horizontal * 0.1, toTarget.z);
        if (aimed.lengthSqr() < 1.0E-4) {
            return false;
        }
        arrow.setDeltaMovement(aimed.normalize().scale(speed));
        arrow.hurtMarked = true;
        return true;
    }

    public static void onShieldBlock(LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getBlocked()) {
            return;
        }
        double stunChance = SkillsHelper.attr(player, SkillsRegistry.SHIELD_STUN);
        if (stunChance > 0.0
                && event.getDamageSource().getDirectEntity() instanceof LivingEntity attacker
                && attacker != player
                && player.getRandom().nextDouble() < stunChance) {
            attacker.addEffect(new MobEffectInstance(SkillsRegistry.STUNNED,
                    CompanionConfig.STUN_DURATION_TICKS.get(), 0), player);
        }
    }

    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double attrValue = SkillsHelper.attr(player, SkillsRegistry.GROUND_SLAM);
        if (attrValue <= 0.0 || event.getDistance() < CompanionConfig.GROUND_SLAM_MIN_FALL.get()) {
            return;
        }
        double safe = player.getAttributeValue(Attributes.SAFE_FALL_DISTANCE);
        float fallDamage = (float) (Math.ceil(event.getDistance() - safe) * event.getDamageMultiplier());
        if (fallDamage <= 0.0F) {
            return;
        }
        float slamDamage = (float) (fallDamage * attrValue);
        double radius = CompanionConfig.GROUND_SLAM_RADIUS.get();
        for (LivingEntity victim : serverLevel.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(radius, 2.0, radius),
                entity -> entity != player && entity.isAlive())) {
            victim.hurtServer(serverLevel, serverLevel.damageSources().playerAttack(player), slamDamage);
            victim.knockback(0.5 + attrValue,
                    player.getX() - victim.getX(), player.getZ() - victim.getZ());
        }
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.MACE_SMASH_GROUND, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 10 != 0) {
            return;
        }
        CombatState state = player.getData(SkillsRegistry.COMBAT_STATE);
        long now = player.level().getGameTime();

        if (state.fasterStrikesStacks > 0
                && now - state.lastUnarmedHitTime > CompanionConfig.FASTER_STRIKES_TIMEOUT.get()) {
            state.fasterStrikesStacks = 0;
        }
        if (state.unarmedRampStacks > 0
                && now - state.lastUnarmedHitTime > CompanionConfig.FASTER_STRIKES_TIMEOUT.get()) {
            state.unarmedRampStacks = 0;
        }
        if (state.archerRampStacks > 0
                && now - state.lastArcherHitTime > CompanionConfig.ARCHER_RAMP_TIMEOUT.get()) {
            state.archerRampStacks = 0;
        }

        double fasterStrikes = SkillsHelper.attr(player, SkillsRegistry.FASTER_STRIKES);
        boolean fasterActive = fasterStrikes > 0.0 && state.fasterStrikesStacks > 0
                && SkillsAbilities.toggles(player).fasterStrikes();
        applyTransient(player.getAttribute(Attributes.ATTACK_SPEED), FASTER_STRIKES_ID,
                fasterStrikes * state.fasterStrikesStacks,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE, fasterActive);

        double mastery = SkillsHelper.attr(player, SkillsRegistry.SHIELD_MASTERY);
        boolean masteryActive = mastery > 0.0 && player.isBlocking();
        applyTransient(player.getAttribute(Attributes.ARMOR), SHIELD_MASTERY_ARMOR_ID,
                mastery * 2.0, AttributeModifier.Operation.ADD_VALUE, masteryActive);
        applyTransient(player.getAttribute(Attributes.ARMOR_TOUGHNESS), SHIELD_MASTERY_TOUGHNESS_ID,
                mastery * 0.5, AttributeModifier.Operation.ADD_VALUE, masteryActive);

        double desperation = SkillsHelper.attr(player, SkillsRegistry.AXE_DESPERATION);
        boolean desperationActive = desperation > 0.0
                && SkillsHelper.isAxe(player.getMainHandItem())
                && SkillsHelper.healthFraction(player) < CompanionConfig.AXE_LIFESTEAL_THRESHOLD.get();
        ApothicHooks.lifeSteal().ifPresent(lifeSteal ->
                applyTransient(player.getAttribute(lifeSteal), AXE_DESPERATION_ID,
                        desperation, AttributeModifier.Operation.ADD_VALUE, desperationActive));

        double recovery = SkillsHelper.attr(player, SkillsRegistry.SHIELD_RECOVERY);
        if (recovery > 0.0 && SkillsHelper.isShield(player.getOffhandItem())
                && now - state.lastShieldHealTime >= CompanionConfig.SHIELD_HEAL_INTERVAL_TICKS.get()
                && player.getHealth() < player.getMaxHealth()) {
            state.lastShieldHealTime = now;
            player.heal((float) (recovery * player.getMaxHealth()));
        }

        if (player.isBlocking()
                && SkillsHelper.attr(player, SkillsRegistry.LIGHTS_SHIELD) > 0.0
                && SkillsHelper.healthFraction(player) < CompanionConfig.LIGHTS_SHIELD_THRESHOLD.get()
                && SkillCooldowns.ready(player, SkillCooldowns.LIGHTS_SHIELD)) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,
                    CompanionConfig.LIGHTS_SHIELD_RESISTANCE_TICKS.get(),
                    CompanionConfig.LIGHTS_SHIELD_RESISTANCE_AMPLIFIER.get(), false, false, true));
            SkillCooldowns.start(player, SkillCooldowns.LIGHTS_SHIELD,
                    CompanionConfig.LIGHTS_SHIELD_COOLDOWN.get());
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.5F);
        }
    }

    private static void applyTransient(AttributeInstance instance, Identifier id, double amount,
                                       AttributeModifier.Operation operation, boolean active) {
        if (instance == null) {
            return;
        }
        if (active) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(id, amount, operation));
        } else if (instance.hasModifier(id)) {
            instance.removeModifier(id);
        }
    }

    public static void onLivingVisibility(LivingEvent.LivingVisibilityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        double stealth = SkillsHelper.attr(player, SkillsRegistry.STEALTH);
        if (player instanceof ServerPlayer serverPlayer
                && serverPlayer.level().getGameTime() < serverPlayer.getData(SkillsRegistry.COMBAT_STATE).ninjaUntil) {
            stealth = 1.0;
        }
        if (stealth > 0.0) {
            event.modifyVisibility(Math.max(0.0, 1.0 - stealth));
        }
    }

    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (player.hasEffect(SkillsRegistry.STUNNED)) {
            event.setCanceled(true);
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)
                || !(event.getTarget() instanceof LivingEntity target)
                || !serverPlayer.isShiftKeyDown()
                || !SkillsHelper.isSword(serverPlayer.getMainHandItem())
                || SkillsHelper.attr(serverPlayer, SkillsRegistry.SHADOW_STEP) <= 0.0
                || !SkillsAbilities.toggles(serverPlayer).shadowStep()
                || !SkillCooldowns.ready(serverPlayer, SkillCooldowns.SHADOW_STEP)) {
            return;
        }
        Vec3 look = target.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            horizontal = Vec3.directionFromRotation(0.0F, target.getYRot());
        }
        Vec3 destination = target.position().subtract(horizontal.normalize().scale(1.5));
        serverPlayer.teleportTo(destination.x, destination.y, destination.z);
        serverPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, target, EntityAnchorArgument.Anchor.EYES);
        SkillCooldowns.start(serverPlayer, SkillCooldowns.SHADOW_STEP, CompanionConfig.SHADOW_STEP_COOLDOWN.get());
        serverPlayer.level().playSound(null, destination.x, destination.y, destination.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)
                || !SkillsHelper.isSword(killer.getMainHandItem())) {
            return;
        }
        double chance = SkillsHelper.attr(killer, SkillsRegistry.SHAKEDOWN);
        if (chance <= 0.0 || event.getDrops().isEmpty() || killer.getRandom().nextDouble() >= chance) {
            return;
        }
        int index = killer.getRandom().nextInt(event.getDrops().size());
        ItemEntity picked = event.getDrops().stream().skip(index).findFirst().orElse(null);
        if (picked != null) {
            event.getDrops().add(new ItemEntity(picked.level(), picked.getX(), picked.getY(), picked.getZ(),
                    picked.getItem().copy()));
        }
    }
}
