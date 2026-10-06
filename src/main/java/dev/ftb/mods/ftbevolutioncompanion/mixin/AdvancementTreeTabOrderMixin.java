package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AdvancementTree.class)
public abstract class AdvancementTreeTabOrderMixin {
    @Unique
    private static final Identifier FTBEVO_FIRST_TAB = Identifier.fromNamespaceAndPath("ftb", "evolution/root");

    @WrapOperation(
            method = "setListener",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", ordinal = 0))
    private Iterator<?> ftbevo$evolutionTabFirst(Set<?> roots, Operation<Iterator<?>> original) {
        List<Object> ordered = new ArrayList<>(roots.size());
        Iterator<?> it = original.call(roots);
        while (it.hasNext()) {
            Object node = it.next();
            if (node instanceof AdvancementNode advancement
                    && advancement.holder().id().equals(FTBEVO_FIRST_TAB)) {
                ordered.add(0, node);
            } else {
                ordered.add(node);
            }
        }
        return ordered.iterator();
    }
}
