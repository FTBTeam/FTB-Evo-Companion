package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.ThaumaturgeHooks;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.leclowndu93150.thaumaturge.content.infusion.BlockEntityInfusionMatrix", remap = false)
public abstract class ThaumInfusionStabilityMixin {
    @ModifyArg(
            method = "tryStartCraft(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At(value = "INVOKE",
                    target = "Lcom/leclowndu93150/thaumaturge/content/infusion/InfusionCraftJob;<init>(Ljava/util/List;"
                            + "Lcom/leclowndu93150/thaumaturge/api/aspect/AspectList;Lnet/minecraft/world/item/ItemStack;"
                            + "Lnet/minecraft/world/item/ItemStack;ILjava/util/Optional;)V"),
            index = 4)
    private int ftbevo$steadyMatrix(int instability, @Local(argsOnly = true) Player player) {
        return ThaumaturgeHooks.stabilize(player, instability);
    }
}
