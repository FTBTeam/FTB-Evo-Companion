package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;

import dev.ftb.mods.ftbevolutioncompanion.client.EvolutionPyramidItemRenderer;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class EvolutionPyramidItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache geoCache = new SingletonAnimatableInstanceCache(this);

    public EvolutionPyramidItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private EvolutionPyramidItemRenderer renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new EvolutionPyramidItemRenderer();
                }
                return renderer;
            }
        });
    }
}
