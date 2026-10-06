package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;
import dev.sterner.witchery.core.api.SymbologySpell;
import dev.sterner.witchery.network.payload.c2s.SymbologyCastC2SPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SymbologyCastC2SPayload.class, remap = false)
public abstract class WitcherySymbologyMixin {
    @WrapOperation(
            method = "handleOnServer(Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ldev/sterner/witchery/core/api/SymbologySpell;onUse(Lnet/minecraft/server/level/ServerLevel;"
                                            + "Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void ftbevo$castWithMastery(
            SymbologySpell spell,
            ServerLevel level,
            ServerPlayer player,
            Vec3 look,
            int spellLevel,
            Operation<Void> original) {
        original.call(spell, level, player, look, CovenMagic.symbolLevel(player, spell, spellLevel));
        CovenMagic.awardSpell(player);
    }
}
