package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowInvoker {
    @Invoker("setPierceLevel")
    void ftbevo$setPierceLevel(byte level);
}
