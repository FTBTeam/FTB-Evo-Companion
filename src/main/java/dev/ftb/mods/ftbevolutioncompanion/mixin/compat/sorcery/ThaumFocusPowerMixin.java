package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.ThaumaturgeHooks;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(targets = "com.leclowndu93150.thaumaturge.api.casters.FocusEngine", remap = false)
public abstract class ThaumFocusPowerMixin {
    @WrapMethod(
            method = "cast(Lnet/minecraft/world/entity/LivingEntity;"
                    + "Lcom/leclowndu93150/thaumaturge/api/casters/FocusPackage;"
                    + "Lcom/leclowndu93150/thaumaturge/api/casters/CastStreams;)V")
    private static void ftbevo$empowerFocus(
            LivingEntity caster, @Coerce Object pack, @Coerce Object origin, Operation<Void> original) {
        original.call(caster, ThaumaturgeHooks.onFocusCast(caster, pack), origin);
    }
}
