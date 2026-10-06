package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.iceandfire.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.iafenvoy.iceandfire.render.SirenShaderRenderHelper", remap = false)
public abstract class IceAndFireSirenShaderMixin {
    @Unique
    private static final Identifier ftbevo$SIREN_POST_CHAIN = FileToIdConverter.json("post_effect")
            .idToFile(Identifier.fromNamespaceAndPath("iceandfire", "shaders/post/siren.json"));

    @Inject(
            method = "enableShader(Lnet/minecraft/client/renderer/GameRenderer;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void ftbevo$skipMissingSirenShader(GameRenderer renderer, CallbackInfo ci) {
        if (Minecraft.getInstance()
                .getResourceManager()
                .getResource(ftbevo$SIREN_POST_CHAIN)
                .isEmpty()) {
            ci.cancel();
        }
    }
}
