package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "com.enderio.enderio.content.filters.FilterSlot", remap = false)
public abstract class EnderIOFilterSlotPickupMixin {
    public boolean mayPickup(Player player) {
        return false;
    }
}
