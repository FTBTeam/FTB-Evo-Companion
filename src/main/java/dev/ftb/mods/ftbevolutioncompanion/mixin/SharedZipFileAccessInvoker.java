package dev.ftb.mods.ftbevolutioncompanion.mixin;

import java.util.zip.ZipFile;
import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FilePackResources.SharedZipFileAccess.class)
public interface SharedZipFileAccessInvoker {
    @Invoker("getOrCreateZipFile")
    ZipFile ftbevo$getOrCreateZipFile();
}
