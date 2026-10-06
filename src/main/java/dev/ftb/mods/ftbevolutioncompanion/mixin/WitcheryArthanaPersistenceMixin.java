package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.serialization.Codec;
import dev.sterner.witchery.content.block.arthana.ArthanaBlockEntity;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ArthanaBlockEntity.class, remap = false)
public abstract class WitcheryArthanaPersistenceMixin {
    @Shadow
    private ItemStack arthana;

    @ModifyExpressionValue(
            method = "loadAdditional",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/storage/ValueInput;read(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Ljava/util/Optional;"))
    private Optional<ItemStack> ftbevo$keepLoadedArthana(Optional<ItemStack> stored) {
        arthana = stored.orElse(ItemStack.EMPTY);
        return stored;
    }

    @WrapWithCondition(
            method = "saveAdditional",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V"))
    private boolean ftbevo$skipEmptyArthana(ValueOutput output, String key, Codec<?> codec, Object value) {
        return !((ItemStack) value).isEmpty();
    }
}
