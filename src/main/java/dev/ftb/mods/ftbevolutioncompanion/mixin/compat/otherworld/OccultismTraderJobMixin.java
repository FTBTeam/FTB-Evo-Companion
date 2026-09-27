package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.klikli_dev.occultism.common.entity.job.SpiritJob;
import com.klikli_dev.occultism.common.entity.job.TraderJob;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TraderJob.class, remap = false)
public abstract class OccultismTraderJobMixin {
    @ModifyExpressionValue(method = "update()V",
            at = @At(value = "FIELD",
                    target = "Lcom/klikli_dev/occultism/common/entity/job/TraderJob;timeToConvert:I",
                    opcode = Opcodes.GETFIELD))
    private int ftbevo$spiritDiligence(int time) {
        return OtherworldMagic.diligentTime(((SpiritJob) (Object) this).entity, time);
    }
}
