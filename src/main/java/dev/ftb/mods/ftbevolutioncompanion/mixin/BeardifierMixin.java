package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.level.levelgen.Beardifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Beardifier.class)
public abstract class BeardifierMixin {
    @ModifyReturnValue(method = "forStructuresInChunk", at = @At("RETURN"))
    private static Beardifier ftbevo$unshareEmptyBeardifier(Beardifier original) {
        return original == Beardifier.EMPTY ? new Beardifier(List.of(), List.of(), null) : original;
    }
}
