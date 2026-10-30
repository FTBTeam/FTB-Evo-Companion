package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.justdirethings;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE", remap = false)
public abstract class ParadoxMachineGearStripMixin {
    @Unique
    private static final List<String> ftbevo$STRIPPED_KEYS = List.of("equipment", "drop_chances", "carriedBlockState");

    @Inject(method = "sanitizeEntityDataDeny", at = @At("RETURN"))
    private void ftbevo$stripGear(CompoundTag entityData, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag sanitized = cir.getReturnValue();
        ftbevo$STRIPPED_KEYS.forEach(sanitized::remove);
    }
}
