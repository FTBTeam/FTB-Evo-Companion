package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ali;

import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.yanny.ali.network.AbstractServer", remap = false)
public abstract class AdvancedLootInfoDeferredReadMixin {
    @Unique
    private ReloadableServerRegistries.Holder ftbevo$pendingRead;

    @Unique
    private boolean ftbevo$reading;

    @Shadow
    public abstract void readLootTables(ReloadableServerRegistries.Holder manager);

    @Inject(
            method = "readLootTables(Lnet/minecraft/server/ReloadableServerRegistries$Holder;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$deferRead(ReloadableServerRegistries.Holder manager, CallbackInfo ci) {
        if (this.ftbevo$reading) {
            return;
        }
        this.ftbevo$pendingRead = manager;
        ci.cancel();
    }

    @Inject(method = "syncLootTables", at = @At("HEAD"))
    private void ftbevo$readBeforeSync(Player player, CallbackInfo ci) {
        ReloadableServerRegistries.Holder manager = this.ftbevo$pendingRead;
        if (manager == null) {
            return;
        }
        this.ftbevo$pendingRead = null;
        this.ftbevo$reading = true;
        try {
            this.readLootTables(manager);
        } finally {
            this.ftbevo$reading = false;
        }
    }
}
