package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.apiimpl.SpellHelperImpl", remap = false)
public abstract class ArsMagicaPiercingStatMixin {
    @Unique
    private static final String ftbevo$PIERCING_ID = "arsmagicalegacy:piercing";

    @Unique
    private static Method ftbevo$idAccessor;

    @WrapOperation(
            method = "getModifiedStat(DLat/minecraftschurli/mods/arsmagicalegacy/api/spell/SpellStat;"
                    + "Ljava/util/List;Lat/minecraftschurli/mods/arsmagicalegacy/api/spell/SpellCastContext;)D",
            at = @At(value = "INVOKE",
                    target = "Lat/minecraftschurli/mods/arsmagicalegacy/api/spell/SpellStat;"
                            + "genericModifiers(Ljava/util/function/ToDoubleFunction;)Ljava/util/Map;"))
    private Map<Object, Object> ftbevo$dropPiercingGenericModifier(ToDoubleFunction<Object> valueFunction,
                                                                  Operation<Map<Object, Object>> original) {
        Map<Object, Object> generic = original.call(valueFunction);
        Map<Object, Object> filtered = new LinkedHashMap<>(generic.size());
        for (Map.Entry<Object, Object> entry : generic.entrySet()) {
            if (!ftbevo$isPiercing(entry.getKey())) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }

    @Unique
    private static boolean ftbevo$isPiercing(Object stat) {
        if (stat == null) {
            return false;
        }
        try {
            Method accessor = ftbevo$idAccessor;
            if (accessor == null) {
                accessor = stat.getClass().getMethod("id");
                ftbevo$idAccessor = accessor;
            }
            return ftbevo$PIERCING_ID.equals(String.valueOf(accessor.invoke(stat)));
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
