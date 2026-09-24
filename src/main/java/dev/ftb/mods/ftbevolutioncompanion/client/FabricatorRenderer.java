package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorBlock;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public final class FabricatorRenderer extends GeoBlockRenderer<FabricatorBlockEntity, FabricatorRenderer.State> {
    private final ItemModelResolver resolver;
    public FabricatorRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DefaultedBlockGeoModel<>(FTBEvolutionCompanion.id("ftb_fabricator")));
        resolver = context.itemModelResolver();
        withRenderLayer(AutoGlowingGeoLayer::new);
    }
    public static final class State extends BlockEntityRenderState {
        final ItemStackRenderState item = new ItemStackRenderState();
        float time;
        float facing;
        boolean hologram;
    }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(FabricatorBlockEntity machine, State state, float partialTick, Vec3 camera,
                                            ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        super.extractRenderState(machine, state, partialTick, camera, crumbling);
        state.item.clear();
        state.hologram = machine.status() == FabricatorBlockEntity.Status.WORKING && machine.getLevel() != null;
        state.facing = 180F - machine.getBlockState().getValue(FabricatorBlock.FACING).toYRot();
        if (state.hologram) {
            state.time = (machine.getLevel().getGameTime() % 24000 + partialTick);
            if (!machine.displayItem().isEmpty()) {
                resolver.updateForTopItem(state.item, machine.displayItem(), ItemDisplayContext.FIXED, machine.getLevel(), null, 0);
            }
        }
    }
    @Override protected void tryRotateByBlockstate(RenderPassInfo<State> info, PoseStack pose) {
        Direction facing = info.getOrDefaultGeckolibData(DIRECTION_FACING, Direction.NORTH);
        pose.mulPose(Axis.YP.rotationDegrees(180F - facing.toYRot()));
    }
    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, pose, collector, camera);
        if (!state.hologram) return;
        pose.pushPose();
        pose.translate(0.5, 0, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(state.facing));
        pose.translate(-0.094, 0.51, -0.02);
        float scan = (float) Math.sin(state.time * 0.09) * 0.14F;
        collector.submitCustomGeometry(pose, RenderTypes.textBackground(), (matrix, consumer) -> {
            horizontalQuad(matrix, consumer, -0.14F, 0.14F, -0.25F, 0x604BCFCD);
            horizontalQuad(matrix, consumer, -0.18F, 0.18F, scan, 0x504BCFCD);
            horizontalQuad(matrix, consumer, -0.185F, 0.185F, scan + 0.006F, 0x80DAFFFF);
        });
        pose.translate(0, Math.sin(state.time * 0.06) * 0.025, 0);
        pose.mulPose(Axis.YP.rotationDegrees(state.time * 2.5F % 360));
        pose.scale(0.38F, 0.38F, 0.38F);
        if (!state.item.isEmpty()) state.item.submit(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();
    }
    private static void horizontalQuad(PoseStack.Pose pose, VertexConsumer consumer, float min, float max, float y, int color) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, min, y, max).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
        consumer.addVertex(matrix, max, y, max).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
        consumer.addVertex(matrix, max, y, min).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
        consumer.addVertex(matrix, min, y, min).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
    }
}
