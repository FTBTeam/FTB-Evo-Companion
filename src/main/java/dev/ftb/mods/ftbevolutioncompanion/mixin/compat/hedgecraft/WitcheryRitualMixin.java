package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;
import dev.sterner.witchery.content.block.ritual.GoldenRitualChalkBlockEntity;
import dev.sterner.witchery.content.recipe.ritual.RitualRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GoldenRitualChalkBlockEntity.class, remap = false)
public abstract class WitcheryRitualMixin {
    @WrapMethod(
            method = "validateConditions(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;"
                    + "Ldev/sterner/witchery/content/recipe/ritual/RitualRecipe;)Z")
    private boolean ftbevo$trackStarter(Player player, Level level, RitualRecipe recipe, Operation<Boolean> original) {
        CovenMagic.beginRiteStart(player);
        try {
            return original.call(player, level, recipe);
        } finally {
            CovenMagic.endRiteStart();
        }
    }

    @Inject(method = "finishRitual()V", at = @At("HEAD"))
    private void ftbevo$awardRite(CallbackInfo ci) {
        CovenMagic.awardRite((GoldenRitualChalkBlockEntity) (Object) this);
    }
}
