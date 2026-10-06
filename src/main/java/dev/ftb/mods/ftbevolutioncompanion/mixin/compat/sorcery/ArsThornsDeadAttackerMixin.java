package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import dev.ftb.mods.ftbevolutioncompanion.mixin.LivingEntityAccessor;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.ability.ThornsAbilityEffect", remap = false)
public abstract class ArsThornsDeadAttackerMixin {
    @Inject(
            method = "apply(Lnet/neoforged/neoforge/event/entity/living/LivingDamageEvent$Post;"
                    + "Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Holder;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$skipDeadAttacker(
            LivingDamageEvent.Post event, Player player, Holder<?> ability, CallbackInfo ci) {
        if (event.getSource().getEntity() instanceof LivingEntity living
                && (living.isRemoved() || ((LivingEntityAccessor) living).ftbevo$isDead())) {
            ci.cancel();
        }
    }
}
