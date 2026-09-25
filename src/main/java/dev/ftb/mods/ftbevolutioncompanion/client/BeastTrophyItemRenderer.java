package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.constant.DataTickets;
import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.ftb.mods.ftbevolutioncompanion.content.BeastTrophyItem;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;

public class BeastTrophyItemRenderer extends GeoItemRenderer<BeastTrophyItem> {
    private static final float HALF_HEIGHT = 1F;

    public BeastTrophyItemRenderer() {
        super(new DefaultedBlockGeoModel<>(Identifier.fromNamespaceAndPath("ftb", "beast_trophy")));
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    private static float scaleFor(ItemDisplayContext context) {
        return switch (context) {
            case GUI, FIXED -> 0.5F;
            case GROUND -> 0.35F;
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> 0.4F;
            default -> 0.45F;
        };
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> pass) {
        super.adjustRenderPose(pass);
        PoseStack poseStack = pass.poseStack();
        float scale = scaleFor(pass.renderState().getOrDefaultGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, ItemDisplayContext.GUI));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0F, -HALF_HEIGHT, 0F);
    }
}
