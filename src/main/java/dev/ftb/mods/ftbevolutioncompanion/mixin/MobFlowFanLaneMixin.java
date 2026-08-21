package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.misterd.mobflowutilities.blockentity.custom.FanBlockEntity", remap = false)
public abstract class MobFlowFanLaneMixin {
    @Inject(method = "hasClearLane(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void ftbevo$checkOwnLane(Level level, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        BlockEntity self = (BlockEntity) (Object) this;
        BlockState fanState = self.getBlockState();
        if (!fanState.hasProperty(BlockStateProperties.FACING)) {
            return;
        }

        Direction facing = fanState.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = facing.getAxis();
        int step = facing.getAxisDirection().getStep();

        BlockPos fanPos = self.getBlockPos();
        BlockPos entityPos = entity.blockPosition();
        int fanAxial = axis.choose(fanPos.getX(), fanPos.getY(), fanPos.getZ());
        int entityAxial = axis.choose(entityPos.getX(), entityPos.getY(), entityPos.getZ());
        int distance = (entityAxial - fanAxial) * step;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i = 1; i < distance; i++) {
            int axial = fanAxial + i * step;
            cursor.set(
                    axis == Direction.Axis.X ? axial : entityPos.getX(),
                    axis == Direction.Axis.Y ? axial : entityPos.getY(),
                    axis == Direction.Axis.Z ? axial : entityPos.getZ());
            BlockState state = level.getBlockState(cursor);
            if (!state.isAir() && state.isSolidRender()) {
                cir.setReturnValue(false);
                return;
            }
        }

        cir.setReturnValue(true);
    }
}
