package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.klikli_dev.occultism.common.entity.job.CrystallizerJob;
import com.klikli_dev.occultism.common.entity.job.SpiritJob;
import com.klikli_dev.occultism.crafting.recipe.CrystallizeRecipe;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CrystallizerJob.class, remap = false)
public abstract class OccultismCrystallizerJobMixin {
    @Shadow
    protected Optional<RecipeHolder<CrystallizeRecipe>> currentRecipe;

    @ModifyExpressionValue(
            method = "update()V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/klikli_dev/occultism/crafting/recipe/CrystallizeRecipe;getCrystallizeTime()I"))
    private int ftbevo$spiritDiligence(int time) {
        return OtherworldMagic.diligentTime(((SpiritJob) (Object) this).entity, time);
    }

    @WrapOperation(
            method = "update()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V", ordinal = 0))
    private void ftbevo$spiritBounty(ItemStack result, int count, Operation<Void> original) {
        boolean ignoresMultiplier =
                currentRecipe.isEmpty() || currentRecipe.get().value().getIgnoreCrystallizeMultiplier();
        original.call(
                result,
                ignoresMultiplier
                        ? count
                        : OtherworldMagic.bountifulCount(((SpiritJob) (Object) this).entity, result, count));
    }
}
