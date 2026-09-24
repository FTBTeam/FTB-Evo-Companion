package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import dev.ftb.mods.ftbevolutioncompanion.client.FabricatorItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public final class FabricatorItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public FabricatorItem(Block block, Properties properties) { super(block, properties); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private FabricatorItemRenderer renderer;
            @Override public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) renderer = new FabricatorItemRenderer();
                return renderer;
            }
        });
    }
}
