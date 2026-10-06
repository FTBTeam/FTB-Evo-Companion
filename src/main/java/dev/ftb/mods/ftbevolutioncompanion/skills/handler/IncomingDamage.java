package dev.ftb.mods.ftbevolutioncompanion.skills.handler;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.skills.CombatState;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillCooldowns;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class IncomingDamage {
    private IncomingDamage() {}

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && attacker.hasEffect(SkillsRegistry.STUNNED)) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource().getEntity() instanceof LivingEntity
                && event.getEntity().hasEffect(SkillsRegistry.MARKED)) {
            event.setAmount(event.getAmount() * (float) (1.0 + CompanionConfig.MARKED_DAMAGE_BONUS.get()));
        }
        if (event.getEntity() instanceof Player victim
                && SkillsHelper.isSpear(victim.getMainHandItem())
                && SkillsHelper.isShield(victim.getOffhandItem())
                && event.getSource().getDirectEntity() instanceof LivingEntity meleeAttacker
                && meleeAttacker == event.getSource().getEntity()
                && isInFront(victim, meleeAttacker)) {
            double phalanx = SkillsHelper.attr(victim, SkillsRegistry.PHALANX);
            if (phalanx > 0.0) {
                event.setAmount(event.getAmount() * (float) Math.max(0.0, 1.0 - phalanx));
            }
        }
        if (event.getEntity() instanceof Player victim && SkillsHelper.isUnarmed(victim)) {
            double resist = SkillsHelper.attr(victim, SkillsRegistry.UNARMED_RESISTANCE);
            if (resist > 0.0) {
                event.setAmount(event.getAmount() * (float) Math.max(0.0, 1.0 - resist));
            }
        }
    }

    private static boolean isInFront(LivingEntity victim, LivingEntity attacker) {
        Vec3 toAttacker = attacker.position().subtract(victim.position());
        Vec3 horizontal = new Vec3(toAttacker.x, 0.0, toAttacker.z);
        Vec3 look = victim.getLookAngle();
        Vec3 facing = new Vec3(look.x, 0.0, look.z);
        if (horizontal.lengthSqr() < 1.0E-4 || facing.lengthSqr() < 1.0E-4) {
            return true;
        }
        return facing.normalize().dot(horizontal.normalize()) > 0.5;
    }

    public static void onDamagePre(LivingDamageEvent.Pre event) {
        applyPiercingStrike(event);
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getNewDamage() < player.getHealth()) {
            return;
        }
        if (SkillsHelper.attr(player, SkillsRegistry.CHEAT_DEATH) <= 0.0
                || !SkillsAbilities.toggles(player).cheatDeath()
                || !SkillCooldowns.ready(player, SkillCooldowns.CHEAT_DEATH)) {
            return;
        }
        event.setNewDamage(Math.max(0.0F, player.getHealth() - 1.0F));
        event.getContainer().setPostAttackInvulnerabilityTicks(40);
        player.addEffect(new MobEffectInstance(
                MobEffects.RESISTANCE, CompanionConfig.CHEAT_DEATH_INVULN_TICKS.get(), 4, false, false, true));
        SkillCooldowns.start(player, SkillCooldowns.CHEAT_DEATH, CompanionConfig.CHEAT_DEATH_COOLDOWN.get());
        player.level()
                .playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.TOTEM_USE,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F);
        player.sendOverlayMessage(Component.translatable("ftbevolutioncompanion.skills.cheat_death.triggered"));
    }

    private static void applyPiercingStrike(LivingDamageEvent.Pre event) {
        DamageSource source = event.getContainer().getSource();
        if (!(source.getEntity() instanceof ServerPlayer attacker)
                || attacker == event.getEntity()
                || !source.isDirect()
                || source.getDirectEntity() != attacker
                || !SkillsHelper.isSword(attacker.getMainHandItem())
                || SkillsHelper.attr(attacker, SkillsRegistry.PIERCING_STRIKE) <= 0.0
                || !SkillsAbilities.toggles(attacker).piercingStrike()) {
            return;
        }
        CombatState state = attacker.getData(SkillsRegistry.COMBAT_STATE);
        if ((state.swordHitCounter + 1) % CompanionConfig.PIERCING_STRIKE_INTERVAL.get() == 0) {
            event.getContainer().addModifier(DamageContainer.Reduction.ARMOR, (container, reduction) -> 0.0F);
        }
    }
}
