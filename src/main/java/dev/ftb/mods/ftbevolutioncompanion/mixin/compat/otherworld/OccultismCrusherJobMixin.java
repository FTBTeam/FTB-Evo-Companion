package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.klikli_dev.occultism.common.entity.job.CrusherJob;
import com.klikli_dev.occultism.common.entity.job.SpiritJob;
import com.klikli_dev.occultism.crafting.recipe.CrushingRecipe;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(value = CrusherJob.class, remap = false)
public abstract class OccultismCrusherJobMixin {
    @Shadow
    protected Optional<RecipeHolder<CrushingRecipe>> currentRecipe;

    @ModifyExpressionValue(method = "update()V",
            at = @At(value = "INVOKE",
                    target = "Lcom/klikli_dev/occultism/crafting/recipe/CrushingRecipe;getCrushingTime()I"))
    private int ftbevo$spiritDiligence(int time) {
        return OtherworldMagic.diligentTime(((SpiritJob) (Object) this).entity, time);
    }

    @WrapOperation(method = "update()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V", ordinal = 0))
    private void ftbevo$spiritBounty(ItemStack result, int count, Operation<Void> original) {
        boolean ignoresMultiplier = currentRecipe.isEmpty() || currentRecipe.get().value().getIgnoreCrushingMultiplier();
        original.call(result, ignoresMultiplier
                ? count
                : OtherworldMagic.bountifulCount(((SpiritJob) (Object) this).entity, result, count));
    }
}
