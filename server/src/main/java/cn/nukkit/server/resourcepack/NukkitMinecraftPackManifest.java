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

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

public class NukkitMinecraftPackManifest extends MinecraftPackManifest {
    private Path path;

    public boolean verify(Path path) {
        this.path = path;

        if (dependencies == null) {
            dependencies = Collections.emptyList();
        }
        return getFormatVersion() != null && getHeader() != null && getModules() != null && getHeader().getDescription() != null &&
                getHeader().getName() != null && getHeader().getUuid() != null && getHeader().getVersion() != null;
    }

    public Optional<Path> getPath() {
        return Optional.ofNullable(path);
    }
}