package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.HephaestusTools;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "com.titammods.hephaestus_tools.tools.helper.ToolTooltipBuilder", remap = false)
public abstract class HephaestusTierTooltipMixin {
    private static final String FTBEVO$NETHERITE = "tooltip.hephaestus_tools.tier.netherite";

    @Inject(
            method = "stats(Lnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true)
    private static void ftbevo$renameHighTiers(
            ItemStack stack, boolean full, CallbackInfoReturnable<List<Component>> cir) {
        int tier = HephaestusTools.headTier(stack);
        if (tier < 6) {
            return;
        }
        String key = "tooltip.ftbevolutioncompanion.tier." + tier;
        List<Component> lines = new ArrayList<>();
        for (Component line : cir.getReturnValue()) {
            lines.add(ftbevo$replace(line, key));
        }
        cir.setReturnValue(lines);
    }

    private static Component ftbevo$replace(Component component, String key) {
        if (component.getContents() instanceof TranslatableContents translatable) {
            if (FTBEVO$NETHERITE.equals(translatable.getKey())) {
                return Component.translatable(key).setStyle(component.getStyle());
            }
            Object[] args = translatable.getArgs().clone();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Component arg) {
                    args[i] = ftbevo$replace(arg, key);
                }
            }
            MutableComponent copy = Component.translatable(translatable.getKey(), args).setStyle(component.getStyle());
            component.getSiblings().forEach(sibling -> copy.append(ftbevo$replace(sibling, key)));
            return copy;
        }
        if (component.getSiblings().isEmpty()) {
            return component;
        }
        MutableComponent copy = component.plainCopy().setStyle(component.getStyle());
        component.getSiblings().forEach(sibling -> copy.append(ftbevo$replace(sibling, key)));
        return copy;
    }
}
