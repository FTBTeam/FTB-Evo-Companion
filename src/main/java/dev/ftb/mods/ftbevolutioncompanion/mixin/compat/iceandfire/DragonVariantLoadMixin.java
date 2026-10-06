package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.iceandfire;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import java.util.List;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonBaseEntity.class)
public abstract class DragonVariantLoadMixin {
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void ftbevo$fallBackToKnownColor(ValueInput input, CallbackInfo ci) {
        DragonBaseEntity dragon = (DragonBaseEntity) (Object) this;
        List<DragonColor> colors = dragon.dragonType.colors();
        String variant = dragon.getVariant();
        if (colors.isEmpty()
                || colors.stream().anyMatch(color -> color.getName().equals(variant))) {
            return;
        }
        dragon.setVariant(colors.getFirst().getName());
    }
}
