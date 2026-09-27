package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.items.IItemHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(BlockBehaviour.class)
public abstract class HephaestusInventoryDropMixin {
    private static final Map<String, String> ftbevo$INVENTORY_FIELDS = Map.of(
            "com.titammods.common.blocks.MelterBlock", "inventory",
            "com.titammods.common.blocks.SmelteryControllerBlock", "itemHandler");

    @ModifyReturnValue(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;",
            at = @At("RETURN"))
    private List<ItemStack> ftbevo$dropHephaestusContents(List<ItemStack> drops, BlockState state, LootParams.Builder params) {
        String fieldName = ftbevo$INVENTORY_FIELDS.get(this.getClass().getName());
        if (fieldName == null) {
            return drops;
        }
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity == null) {
            return drops;
        }
        try {
            if (!(blockEntity.getClass().getField(fieldName).get(blockEntity) instanceof IItemHandler handler)) {
                return drops;
            }
            List<ItemStack> result = new ArrayList<>(drops);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    result.add(stack.copy());
                }
            }
            return result;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return drops;
        }
    }
}
