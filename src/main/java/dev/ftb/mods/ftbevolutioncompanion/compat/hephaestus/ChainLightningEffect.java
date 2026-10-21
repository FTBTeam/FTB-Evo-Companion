package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.effect.EffectContext;
import com.titammods.hephaestus_tools.effect.HitDetails;
import com.titammods.hephaestus_tools.effect.ToolEffect;
import com.titammods.hephaestus_tools.tools.helper.ToolCombat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record ChainLightningEffect(int targets, double range) implements ToolEffect {
    public static final MapCodec<ChainLightningEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.optionalFieldOf("targets", 10).forGetter(ChainLightningEffect::targets),
                    Codec.DOUBLE.optionalFieldOf("range", 10.0).forGetter(ChainLightningEffect::range))
            .apply(instance, ChainLightningEffect::new));

    @Override
    public MapCodec<ChainLightningEffect> codec() {
        return CODEC;
    }

    @Override
    public void afterHit(EffectContext context, HitDetails hit, int level) {
        if (!(context.level() instanceof ServerLevel serverLevel)
                || !(context.holder() instanceof Player player)
                || !(context.target() instanceof Mob first)) {
            return;
        }
        Set<LivingEntity> struck = new HashSet<>();
        struck.add(first);
        Deque<LivingEntity> queue = new ArrayDeque<>();
        queue.add(first);
        float amount = hit.damage();
        while (!queue.isEmpty() && struck.size() <= targets) {
            LivingEntity from = queue.poll();
            amount *= 0.5F;
            if (amount < 0.5F) {
                break;
            }
            AABB area = from.getBoundingBox().inflate(range);
            for (Mob next :
                    serverLevel.getEntitiesOfClass(Mob.class, area, mob -> mob.isAlive() && !struck.contains(mob))) {
                if (struck.size() > targets) {
                    break;
                }
                if (!player.hasLineOfSight(next)) {
                    continue;
                }
                struck.add(next);
                queue.add(next);
                arc(
                        serverLevel,
                        from.position().add(0, from.getBbHeight() / 2, 0),
                        next.position().add(0, next.getBbHeight() / 2, 0));
                float damage = amount;
                ToolCombat.secondary(() -> {
                    next.invulnerableTime = 0;
                    next.hurtServer(serverLevel, serverLevel.damageSources().playerAttack(player), damage);
                });
            }
        }
        if (struck.size() > 1) {
            serverLevel.playSound(
                    null,
                    first.getX(),
                    first.getY(),
                    first.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT,
                    SoundSource.PLAYERS,
                    0.6F,
                    1.4F);
        }
    }

    private static void arc(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 step = to.subtract(from);
        int points = Math.max(4, (int) (step.length() * 3));
        for (int i = 0; i <= points; i++) {
            Vec3 at = from.add(step.scale((double) i / points));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 1, 0.05, 0.05, 0.05, 0.0);
        }
    }
}
