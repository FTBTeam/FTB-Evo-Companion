package dev.ftb.mods.ftbevolutioncompanion;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZipListingIndex {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipListingIndex.class);
    private static final String[] NONE = new String[0];

    private final String typeRoot;
    private final Map<String, String[]> buckets;

    private ZipListingIndex(String typeRoot, Map<String, String[]> buckets) {
        this.typeRoot = typeRoot;
        this.buckets = buckets;
    }

    public static ZipListingIndex build(ZipFile zipFile, String typeRoot) {
        Map<String, List<String>> collected = new HashMap<>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        int rootLength = typeRoot.length();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            String name = entry.getName();
            if (!name.startsWith(typeRoot)) {
                continue;
            }
            int namespaceEnd = name.indexOf('/', rootLength);
            if (namespaceEnd < 0) {
                continue;
            }
            int folderEnd = name.indexOf('/', namespaceEnd + 1);
            if (folderEnd < 0) {
                continue;
            }
            collected
                    .computeIfAbsent(name.substring(rootLength, folderEnd), key -> new ArrayList<>())
                    .add(name);
        }
        Map<String, String[]> buckets = HashMap.newHashMap(collected.size());
        collected.forEach((key, names) -> buckets.put(key, names.toArray(NONE)));
        return new ZipListingIndex(typeRoot, buckets);
    }

    public void list(ZipFile zipFile, String namespace, String directory, PackResources.ResourceOutput output) {
        int folderEnd = directory.indexOf('/');
        String folder = folderEnd < 0 ? directory : directory.substring(0, folderEnd);
        String[] names = this.buckets.get(namespace + "/" + folder);
        if (names == null) {
            return;
        }
        String root = this.typeRoot + namespace + "/";
        String prefix = root + directory + "/";
        int rootLength = root.length();
        for (String name : names) {
            if (!name.startsWith(prefix)) {
                continue;
            }
            String path = name.substring(rootLength);
            Identifier id = Identifier.tryBuild(namespace, path);
            if (id == null) {
                LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", namespace, path);
                continue;
            }
            ZipEntry entry = zipFile.getEntry(name);
            if (entry != null) {
                output.accept(id, IoSupplier.create(zipFile, entry));
            }
        }
    }
}
