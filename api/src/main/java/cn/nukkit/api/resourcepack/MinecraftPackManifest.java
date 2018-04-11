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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class MinecraftPackManifest {
    @JsonProperty("format_version")
    private Integer formatVersion;
    private Header header;
    private Collection<Module> modules;
    protected Collection<Dependency> dependencies;

    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(modules);
    }

    @Getter
    @ToString
    public static class Header {
        private String description;
        private String name;
        private UUID uuid;
        private SemVer version;
        @JsonProperty("min_engine_version")
        private SemVer minimumSupportedMinecraftVersion;
    }

    @Getter
    @ToString
    public static class Module {
        private String description;
        private String name;
        private UUID uuid;
        private SemVer version;
    }

    @Getter
    @ToString
    public static class Dependency {
        private UUID uuid;
        private SemVer version;
    }
}
