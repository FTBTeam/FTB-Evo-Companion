package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ae2;

import appeng.api.util.DimensionalBlockPos;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "appeng.helpers.WirelessTerminalMenuHost", remap = false)
public abstract class WirelessSignalSubLevelMixin {

    @ModifyExpressionValue(
            method = "getAccessPointSignal",
            at = @At(value = "INVOKE", target = "Lappeng/api/implementations/blockentities/IWirelessAccessPoint;getLocation()Lappeng/api/util/DimensionalBlockPos;")
    )
    private DimensionalBlockPos ftbevolutioncompanion$subLevelLocation(DimensionalBlockPos original) {
        try {
            Level level = original.getLevel();
            BlockPos pos = original.getPos();
            SubLevel sub = Sable.HELPER.getContaining(level, pos);
            if (sub == null) {
                return original;
            }
            Pose3dc pose = sub.logicalPose();
            if (pose == null) {
                return original;
            }
            Vec3 world = pose.transformPosition(Vec3.atCenterOf(pos));
            return new DimensionalBlockPos(level, BlockPos.containing(world));
        } catch (Throwable ignored) {
            return original;
        }
    }
}
