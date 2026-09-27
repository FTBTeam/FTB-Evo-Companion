package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.klikli_dev.occultism.common.blockentity.GoldenSacrificialBowlBlockEntity;
import com.klikli_dev.occultism.common.ritual.SummonRitual;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SummonRitual.class, remap = false)
public abstract class OccultismSummonSnapshotMixin {
    @Inject(method = "initSummoned(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;"
            + "Lnet/minecraft/core/BlockPos;"
            + "Lcom/klikli_dev/occultism/common/blockentity/GoldenSacrificialBowlBlockEntity;"
            + "Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("TAIL"))
    private void ftbevo$snapshotSpiritOwner(LivingEntity living, Level level, BlockPos goldenBowlPosition,
                                            GoldenSacrificialBowlBlockEntity blockEntity,
                                            @Nullable Player castingPlayer, CallbackInfo ci) {
        OtherworldMagic.snapshotSummoned(living, castingPlayer);
    }
}
