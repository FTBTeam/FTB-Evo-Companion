package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy.client;

import com.breakinblocks.neovitae.client.screen.RitualDivinerScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.client.HemomancyDisplay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = RitualDivinerScreen.class, remap = false)
public abstract class NeoVitaeRitualDivinerMixin {
    @ModifyArg(
            method = "buildTooltip(Lcom/breakinblocks/neovitae/client/screen/RitualDivinerScreen$Entry;)Ljava/util/List;",
            at = @At(value = "INVOKE",
                    target = "Lcom/breakinblocks/neovitae/client/screen/RitualDivinerScreen;fmt(I)Ljava/lang/String;",
                    ordinal = 0))
    private int ftbevo$skillActivation(int cost) {
        return HemomancyDisplay.ritualActivation(cost);
    }

    @WrapOperation(
            method = "buildTooltip(Lcom/breakinblocks/neovitae/client/screen/RitualDivinerScreen$Entry;)Ljava/util/List;",
            at = @At(value = "INVOKE",
                    target = "Lcom/breakinblocks/neovitae/client/screen/RitualDivinerScreen;fmt(I)Ljava/lang/String;",
                    ordinal = 1))
    private String ftbevo$skillUpkeep(int cost, Operation<String> original) {
        String reduced = HemomancyDisplay.ritualUpkeep(cost);
        return reduced != null ? reduced : original.call(cost);
    }
}
