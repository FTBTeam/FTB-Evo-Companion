package dev.ftb.mods.ftbevolutioncompanion.mixin;

import java.lang.reflect.Field;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonElement;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.DeserializationException;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.Marshaller;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.impl.POJODeserializer", remap = false)
public abstract class PowahConfigInPlaceUnpackMixin {
    private static final String ftbevo$POWAH_CONFIG_PACKAGE = "owmii.powah.config.";

    @Shadow
    public static void unpackObject(Object target, JsonObject source, boolean failFast) throws DeserializationException {
        throw new AssertionError();
    }

    @Inject(method = "unpackFieldData", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$unpackPowahConfigInPlace(Object parent, Field field, JsonElement elem, Marshaller marshaller,
                                                        CallbackInfoReturnable<Boolean> cir) throws Throwable {
        if (!(elem instanceof JsonObject json) || !parent.getClass().getName().startsWith(ftbevo$POWAH_CONFIG_PACKAGE)) {
            return;
        }
        field.setAccessible(true);
        Object existing = field.get(parent);
        if (existing == null || existing.getClass().isPrimitive()) {
            return;
        }
        unpackObject(existing, json, false);
        cir.setReturnValue(true);
    }
}
