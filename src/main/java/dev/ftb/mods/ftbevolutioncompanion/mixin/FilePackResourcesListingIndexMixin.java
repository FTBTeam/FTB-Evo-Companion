package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.ZipListingIndex;
import java.util.zip.ZipFile;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilePackResources.class)
public abstract class FilePackResourcesListingIndexMixin {
    @Shadow
    @Final
    private FilePackResources.SharedZipFileAccess zipFileAccess;

    @Shadow
    @Final
    private String prefix;

    @Unique
    private volatile ZipListingIndex ftbevo$dataIndex;

    @Unique
    private volatile ZipListingIndex ftbevo$assetIndex;

    @Inject(method = "listResources", at = @At("HEAD"), cancellable = true)
    private void ftbevo$listFromIndex(
            PackType type, String namespace, String directory, PackResources.ResourceOutput output, CallbackInfo ci) {
        if (directory.isEmpty() || directory.charAt(0) == '/') {
            return;
        }
        boolean data = type == PackType.SERVER_DATA;
        if (!data && type != PackType.CLIENT_RESOURCES) {
            return;
        }
        ZipFile zipFile = ((SharedZipFileAccessInvoker) this.zipFileAccess).ftbevo$getOrCreateZipFile();
        if (zipFile == null) {
            return;
        }
        ZipListingIndex index = data ? this.ftbevo$dataIndex : this.ftbevo$assetIndex;
        if (index == null) {
            synchronized (this) {
                index = data ? this.ftbevo$dataIndex : this.ftbevo$assetIndex;
                if (index == null) {
                    String typeRoot = type.getDirectory() + "/";
                    index = ZipListingIndex.build(
                            zipFile, this.prefix.isEmpty() ? typeRoot : this.prefix + "/" + typeRoot);
                    if (data) {
                        this.ftbevo$dataIndex = index;
                    } else {
                        this.ftbevo$assetIndex = index;
                    }
                }
            }
        }
        index.list(zipFile, namespace, directory, output);
        ci.cancel();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void ftbevo$dropIndexes(CallbackInfo ci) {
        this.ftbevo$dataIndex = null;
        this.ftbevo$assetIndex = null;
    }
}
