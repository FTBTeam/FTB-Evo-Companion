package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDeadDamageMixin {
    @Unique
    private static final Logger ftbevo$LOGGER = LoggerFactory.getLogger("FTBEvolutionCompanion/DeadDamage");

    @Unique
    private static final Set<EntityType<?>> ftbevo$REPORTED = ConcurrentHashMap.newKeySet();

    @Shadow
    protected boolean dead;

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    private void ftbevo$recordDeadFlag(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfo ci,
            @Share("deadBeforePre") LocalBooleanRef deadBeforePre) {
        deadBeforePre.set(this.dead);
    }

    @WrapOperation(
            method = "actuallyHurt",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lcom/google/common/base/Preconditions;checkArgument(ZLjava/lang/Object;)V",
                            remap = false))
    private void ftbevo$skipDeadCheck(
            boolean expression,
            Object message,
            Operation<Void> original,
            ServerLevel level,
            DamageSource source,
            float damage,
            @Share("deadBeforePre") LocalBooleanRef deadBeforePre) {
        if (expression) {
            original.call(expression, message);
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        if (ftbevo$REPORTED.add(self.getType())) {
            Entity attacker = source.getEntity();
            ftbevo$LOGGER.warn(
                    "Damage to an entity already marked dead was allowed instead of crashing: {} at {} in {}, health {}, damage {} from {} by {}, dead before LivingDamageEvent.Pre: {}",
                    BuiltInRegistries.ENTITY_TYPE.getKey(self.getType()),
                    self.blockPosition().toShortString(),
                    level.dimension().identifier(),
                    self.getHealth(),
                    damage,
                    source.getMsgId(),
                    attacker == null ? "none" : BuiltInRegistries.ENTITY_TYPE.getKey(attacker.getType()),
                    deadBeforePre.get());
        }
    }
}
