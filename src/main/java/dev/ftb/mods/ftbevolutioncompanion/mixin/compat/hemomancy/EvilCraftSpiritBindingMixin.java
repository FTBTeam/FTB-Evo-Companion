package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;
import net.minecraft.world.entity.Entity;
import org.cyclops.evilcraft.entity.effect.EntityAntiVengeanceBeam;
import org.cyclops.evilcraft.entity.monster.EntityVengeanceSpirit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityAntiVengeanceBeam.class, remap = false)
public abstract class EvilCraftSpiritBindingMixin {
    @Inject(
            method = "applyHitEffect(Lnet/minecraft/world/entity/Entity;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lorg/cyclops/evilcraft/entity/monster/EntityVengeanceSpirit;onHit(DDDDDD)V",
                            shift = At.Shift.AFTER))
    private void ftbevo$holdSpiritLonger(Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityVengeanceSpirit spirit)) {
            return;
        }
        int bonus =
                HemomancyHooks.spiritBinding(((EntityAntiVengeanceBeam) (Object) this).getOwner(), spirit.getRandom());
        if (bonus > 0) {
            spirit.addFrozenDuration(bonus);
        }
    }
}
