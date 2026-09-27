package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.klikli_dev.occultism.common.entity.familiar.DragonFamiliarEntity;
import com.klikli_dev.occultism.common.entity.familiar.FamiliarEntity;
import com.klikli_dev.occultism.common.entity.familiar.GuardianFamiliarEntity;
import com.klikli_dev.occultism.common.entity.familiar.IFamiliar;
import com.klikli_dev.occultism.common.entity.familiar.OtherworldBirdEntity;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = {
        FamiliarEntity.class,
        DragonFamiliarEntity.class,
        GuardianFamiliarEntity.class,
        OtherworldBirdEntity.class
}, remap = false)
public abstract class OccultismFamiliarBondMixin {
    @ModifyArg(method = "getFamiliarEffects()Ljava/lang/Iterable;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/core/Holder;IIZZ)V"),
            index = 2)
    private int ftbevo$familiarBond(int amplifier) {
        return OtherworldMagic.bondedAmplifier(((IFamiliar) (Object) this).getFamiliarOwner(), amplifier);
    }
}
