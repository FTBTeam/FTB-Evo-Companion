package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;
import org.cyclops.evilcraft.api.broom.BroomModifier;
import org.cyclops.evilcraft.entity.item.EntityBroom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityBroom.class, remap = false)
public abstract class EvilCraftBroomSpeedMixin {
    @ModifyReturnValue(method = "getModifier(Lorg/cyclops/evilcraft/api/broom/BroomModifier;)F", at = @At("RETURN"))
    private float ftbevo$riderSpeed(float original, BroomModifier modifier) {
        return HemomancyHooks.broomModifier(
                original, modifier, ((EntityBroom) (Object) this).getControllingPassenger());
    }
}
