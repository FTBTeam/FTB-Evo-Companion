package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.klikli_dev.occultism.common.entity.job.SmelterJob;
import com.klikli_dev.occultism.common.entity.job.SpiritJob;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SmelterJob.class, remap = false)
public abstract class OccultismSmelterJobMixin {
    @ModifyExpressionValue(
            method = "update()V",
            at = {
                @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/SmeltingRecipe;cookingTime()I"),
                @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/BlastingRecipe;cookingTime()I"),
                @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/SmokingRecipe;cookingTime()I"),
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/item/crafting/CampfireCookingRecipe;cookingTime()I")
            })
    private int ftbevo$spiritDiligence(int time) {
        return OtherworldMagic.diligentTime(((SpiritJob) (Object) this).entity, time);
    }
}
