package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.powerarmor;

import com.portingdeadmods.portingdeadlibs.api.blockentities.SimpleContainerBlockEntity;
import com.portingdeadmods.power_armor.content.blockentities.ArmorModificationTableBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleContainerBlockEntity.class, remap = false)
public abstract class ArmorTableSlotPersistenceMixin {
    @Unique
    private static final String ftbevo$KEY = "ftbevolutioncompanion_armor_table_item";

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void ftbevo$saveArmorTableItem(ValueOutput output, CallbackInfo ci) {
        if ((Object) this instanceof ArmorModificationTableBlockEntity table) {
            output.store(
                    ftbevo$KEY, ItemStack.OPTIONAL_CODEC, table.getItemHandler().itemStack());
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void ftbevo$loadArmorTableItem(ValueInput input, CallbackInfo ci) {
        if ((Object) this instanceof ArmorModificationTableBlockEntity table) {
            input.read(ftbevo$KEY, ItemStack.OPTIONAL_CODEC)
                    .ifPresent(stack -> table.getItemHandler().set(0, stack, null));
        }
    }
}
