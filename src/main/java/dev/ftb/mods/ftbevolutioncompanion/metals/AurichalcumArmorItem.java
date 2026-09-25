package dev.ftb.mods.ftbevolutioncompanion.metals;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoArmorRenderer;
import dev.ftb.mods.ftbevolutioncompanion.client.AurichalcumArmorRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public final class AurichalcumArmorItem extends Item implements GeoItem {
    private final AnimatableInstanceCache geoCache = new SingletonAnimatableInstanceCache(this);

    public AurichalcumArmorItem(Properties properties) {
        super(properties);
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
            private AurichalcumArmorRenderer renderer;

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack stack, EquipmentSlot slot) {
                if (renderer == null) {
                    renderer = new AurichalcumArmorRenderer();
                }
                return renderer;
            }
        });
    }
}
