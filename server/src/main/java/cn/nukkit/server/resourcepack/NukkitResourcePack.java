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

package cn.nukkit.server.resourcepack;

import cn.nukkit.api.resourcepack.MinecraftPackManifest;
import cn.nukkit.api.resourcepack.ResourcePack;
import cn.nukkit.api.util.SemVer;
import cn.nukkit.server.resourcepack.loader.file.PackFile;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class NukkitResourcePack implements ResourcePack {
    private final MinecraftPackManifest manifest;
    private final PackFile packFile;

    public NukkitResourcePack(MinecraftPackManifest manifest, PackFile packFile) {
        this.manifest = manifest;
        this.packFile = packFile;
    }

    @Nonnull
    public UUID getId() {
        return manifest.getHeader().getUuid();
    }

    public int getFormatVersion() {
        return manifest.getFormatVersion();
    }

    @Nonnull
    public Optional<SemVer> getMinimumSupportedMinecraftVersion() {
        return Optional.ofNullable(manifest.getHeader().getMinimumSupportedMinecraftVersion());
    }

    @Nonnull
    public SemVer getVersion() {
        return manifest.getHeader().getVersion();
    }

    @Nonnull
    public String getName() {
        return manifest.getHeader().getName();
    }

    @Nonnull
    public String getDescription() {
        return manifest.getHeader().getDescription();
    }

    @Nonnull
    public Collection<MinecraftPackManifest.Module> getModules() {
        return manifest.getModules();
    }

    public Collection<MinecraftPackManifest.Dependency> getDependencies() {
        return manifest.getDependencies();
    }

    public long getPackSize() {
        return packFile.getPackSize();
    }

    public int getCompressedSize() {
        return packFile.getCompressedSize();
    }

    public byte[] getSha256() {
        return packFile.getSha256();
    }

    public byte[] getPackChunk(int chunkIndex) {
        return packFile.getPackChunk(chunkIndex);
    }

    public int getChunkCount() {
        return packFile.getChunkCount();
    }
}
