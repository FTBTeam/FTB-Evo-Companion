package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.powerarmor;

import com.portingdeadmods.power_armor.content.blockentities.ArmorModificationTableBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorModificationTableBlockEntity.class, remap = false)
public abstract class ArmorTableMarkDirtyMixin {
    @Unique
    private ItemStack ftbevo$lastStack = ItemStack.EMPTY;

    @Unique
    private int ftbevo$ticks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void ftbevo$markDirtyOnSlotChange(CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getLevel() == null || self.getLevel().isClientSide()) {
            return;
        }
        ItemStack current = ((ArmorModificationTableBlockEntity) (Object) this)
                .getItemHandler()
                .itemStack();
        boolean changed = current != ftbevo$lastStack;
        boolean periodic = !current.isEmpty() && ++ftbevo$ticks >= 100;
        if (changed || periodic) {
            ftbevo$lastStack = current;
            ftbevo$ticks = 0;
            self.setChanged();
        }
    }
}
