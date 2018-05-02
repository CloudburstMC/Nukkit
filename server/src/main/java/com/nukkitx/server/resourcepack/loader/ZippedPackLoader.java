package com.nukkitx.server.resourcepack.loader;

import com.nukkitx.api.resourcepack.MinecraftPackManifest;
import com.nukkitx.api.resourcepack.PackLoader;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.resourcepack.NukkitBehaviorPack;
import com.nukkitx.server.resourcepack.NukkitMinecraftPackManifest;
import com.nukkitx.server.resourcepack.NukkitResourcePack;
import com.nukkitx.server.resourcepack.loader.file.ZippedPackFile;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZippedPackLoader implements PackLoader {
    private static final String[] FILE_EXTENSION = new String[] {".jar", ".mcpack"};

    @Nonnull
    public Optional<String[]> getPluginFileExtension() {
        return Optional.of(FILE_EXTENSION);
    }

    public MinecraftPackManifest loadManifest(Path path) {
        try (ZipFile zip = new ZipFile(path.toFile())) {
            ZipEntry entry = zip.getEntry("manifest.json");
            if (entry == null) {
                throw new IllegalArgumentException("No manifest.json found");
            }
            try (InputStream stream = zip.getInputStream(entry)) {
                NukkitMinecraftPackManifest manifest = (NukkitMinecraftPackManifest) NukkitServer.JSON_MAPPER.readValue(stream, MinecraftPackManifest.class);
                if (!manifest.verify(path)) {
                    throw new IllegalArgumentException("Invalid manifest.json");
                }
                return manifest;
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Zipped file", e);
        }
    }

    public NukkitResourcePack createResourcePack(MinecraftPackManifest manifest) {
        if (!(manifest instanceof NukkitMinecraftPackManifest)) {
            throw new IllegalStateException("Manifest is not of type NukkitMinecraftPackManifest");
        }
        Optional<Path> optionalPath = ((NukkitMinecraftPackManifest) manifest).getPath();
        if (!optionalPath.isPresent()) {
            throw new IllegalStateException("No path was found");
        }
        return new NukkitResourcePack(manifest, new ZippedPackFile(optionalPath.get()));
    }

    public NukkitBehaviorPack createBehaviorPack(MinecraftPackManifest manifest) {
        return null;
    }
}
