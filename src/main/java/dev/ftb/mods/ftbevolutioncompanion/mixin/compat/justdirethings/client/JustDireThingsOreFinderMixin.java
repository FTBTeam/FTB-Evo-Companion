package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.justdirethings.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.direwolf20.justdirethings.client.renderactions.ThingFinder", remap = false)
public abstract class JustDireThingsOreFinderMixin {
    @Unique
    private static final TagKey<Block> FTBEVO$NEEDS_ADAMANTITE_TOOL =
            TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("ftbarmory", "needs_adamantite_tool"));

    @Inject(
            method =
                    "isValidBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("RETURN"),
            cancellable = true,
            require = 1)
    private static void ftbevo$showPostNetheriteOres(
            BlockPos pos, Player player, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        BlockState state = player.level().getBlockState(pos);
        if (state.is(Tags.Blocks.ORES) && state.is(FTBEVO$NEEDS_ADAMANTITE_TOOL)) {
            cir.setReturnValue(true);
        }
    }
}
