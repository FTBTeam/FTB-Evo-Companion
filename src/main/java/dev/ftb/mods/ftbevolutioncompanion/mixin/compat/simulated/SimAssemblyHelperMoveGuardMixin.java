package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.simulated;

import dev.ftb.mods.ftbevolutioncompanion.compat.sable.SubLevelMoveGuard;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.simulated_team.simulated.util.SimAssemblyHelper", remap = false)
public abstract class SimAssemblyHelperMoveGuardMixin {

    @Inject(method = "disassembleSubLevel", at = @At("HEAD"))
    private static void ftbevolutioncompanion$beginSubLevelDisassemble(Level level, SubLevel subLevel, BlockPos worldPos, BlockPos assemblerPos, Rotation rotation, boolean flag, CallbackInfo ci) {
        SubLevelMoveGuard.enter();
    }

    @Inject(method = "disassembleSubLevel", at = @At("RETURN"))
    private static void ftbevolutioncompanion$endSubLevelDisassemble(Level level, SubLevel subLevel, BlockPos worldPos, BlockPos assemblerPos, Rotation rotation, boolean flag, CallbackInfo ci) {
        SubLevelMoveGuard.exit();
    }
}
