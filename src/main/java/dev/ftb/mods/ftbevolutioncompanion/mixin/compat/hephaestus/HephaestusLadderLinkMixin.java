package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.titammods.common.blocks.SearedLadderBlock", remap = false)
public abstract class HephaestusLadderLinkMixin {
    @Shadow
    @Final
    public static Property<Direction> FACING;

    @Shadow
    @Final
    public static BooleanProperty BOTTOM;

    @ModifyReturnValue(
            method = "getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("RETURN"))
    private BlockState ftbevo$fixTopDownLinking(BlockState original, BlockPlaceContext context) {
        if (original == null) {
            return original;
        }
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return original;
        }
        BlockPos abovePos = context.getClickedPos().above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (aboveState.getBlock() == (Object) this
                && aboveState.hasProperty(BOTTOM)
                && aboveState.getValue(BOTTOM)
                && aboveState.getValue(FACING) == original.getValue(FACING)) {
            level.setBlock(abovePos, aboveState.setValue(BOTTOM, false), 3);
        }
        return original;
    }
}
