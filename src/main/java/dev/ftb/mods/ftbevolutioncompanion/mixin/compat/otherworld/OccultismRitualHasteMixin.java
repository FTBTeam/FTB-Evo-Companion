package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.klikli_dev.occultism.common.blockentity.GoldenSacrificialBowlBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GoldenSacrificialBowlBlockEntity.class, remap = false)
public abstract class OccultismRitualHasteMixin {
    @Unique
    private double ftbevo$hasteProgress;

    @Inject(method = "tick()V", at = {
            @At(value = "FIELD",
                    target = "Lcom/klikli_dev/occultism/common/blockentity/GoldenSacrificialBowlBlockEntity;currentTime:I",
                    opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER),
            @At(value = "FIELD",
                    target = "Lcom/klikli_dev/occultism/common/blockentity/GoldenSacrificialBowlBlockEntity;currentTime:I",
                    opcode = Opcodes.PUTFIELD, ordinal = 1, shift = At.Shift.AFTER),
            @At(value = "FIELD",
                    target = "Lcom/klikli_dev/occultism/common/blockentity/GoldenSacrificialBowlBlockEntity;currentTime:I",
                    opcode = Opcodes.PUTFIELD, ordinal = 2, shift = At.Shift.AFTER)
    })
    private void ftbevo$hasteRitualStep(CallbackInfo ci) {
        GoldenSacrificialBowlBlockEntity bowl = (GoldenSacrificialBowlBlockEntity) (Object) this;
        double haste = OtherworldMagic.ritualHaste(bowl);
        if (haste <= 0.0) {
            return;
        }
        if (bowl.currentTime <= 1) {
            ftbevo$hasteProgress = 0.0;
        }
        ftbevo$hasteProgress += haste;
        int bonus = (int) ftbevo$hasteProgress;
        if (bonus > 0) {
            ftbevo$hasteProgress -= bonus;
            bowl.currentTime += bonus;
        }
    }
}
