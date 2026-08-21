package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.init.AMBlocks", remap = false)
public interface ArsMagicaLiquidEtheriumMixin {
    @ModifyReturnValue(
            method = "lambda$static$4(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)"
                    + "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;",
            at = @At("RETURN"), require = 0)
    private static BlockBehaviour.Properties ftbevo$makeEtheriumBehaveLikeWater(
            BlockBehaviour.Properties original) {
        return original.replaceable()
                .noCollision()
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid()
                .sound(SoundType.EMPTY);
    }
}
