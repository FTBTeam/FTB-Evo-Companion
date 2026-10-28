package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.mffs;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.su5ed.mffs.item.RemoteControllerItem", remap = false)
public abstract class RemoteControllerUntrackedChunkMixin {
    @Shadow
    public abstract BlockPos getLink(ItemStack stack);

    @Inject(
            method =
                    "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$refuseUntrackedTarget(
            Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.isShiftKeyDown()) {
            return;
        }
        BlockPos pos = getLink(player.getItemInHand(hand));
        if (pos == null || !level.isLoaded(pos)) {
            return;
        }
        if (!serverPlayer.getChunkTrackingView().contains(ChunkPos.containing(pos))) {
            player.sendOverlayMessage(Component.translatable("message.ftbevolutioncompanion.mffs.remote_out_of_range")
                    .withStyle(ChatFormatting.RED));
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
