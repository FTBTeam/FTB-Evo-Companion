package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.dimension.end.EnderDragonFight;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragonFight.class)
public abstract class EnderDragonFightEggMixin {
    @ModifyExpressionValue(
            method = "setDragonKilled",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;hasPreviouslyKilledDragon:Z",
                            opcode = Opcodes.GETFIELD))
    private boolean ftbevo$placeEggEveryKill(boolean previouslyKilled) {
        return false;
    }
}
