/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.resourcepack.loader;

import cn.nukkit.api.resourcepack.MinecraftPackManifest;
import cn.nukkit.api.resourcepack.PackLoader;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.resourcepack.NukkitBehaviorPack;
import cn.nukkit.server.resourcepack.NukkitMinecraftPackManifest;
import cn.nukkit.server.resourcepack.NukkitResourcePack;
import cn.nukkit.server.resourcepack.loader.file.ZippedPackFile;

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
