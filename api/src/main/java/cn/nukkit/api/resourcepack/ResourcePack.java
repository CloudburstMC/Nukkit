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

package cn.nukkit.api.resourcepack;

import cn.nukkit.api.util.SemVer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePack {

    @Nonnull
    UUID getId();

    int getFormatVersion();

    @Nonnull
    Optional<SemVer> getMinimumSupportedMinecraftVersion();

    @Nonnull
    SemVer getVersion();

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();

    @Nonnull
    Collection<MinecraftPackManifest.Module> getModules();

    Collection<MinecraftPackManifest.Dependency> getDependencies();

    long getPackSize();

    int getCompressedSize();

    byte[] getSha256();

    byte[] getPackChunk(int chunkIndex);

    int getChunkCount();
}
