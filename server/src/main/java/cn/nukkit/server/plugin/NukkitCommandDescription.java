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

package cn.nukkit.server.plugin;

import cn.nukkit.api.plugin.CommandDescription;
import com.google.common.collect.ImmutableCollection;

import java.util.Optional;

public final class NukkitCommandDescription implements CommandDescription {
    private final String description;
    private final ImmutableCollection<String> aliases;
    private final String usage;
    private final String permission;
    private final String permissionMessage;

    public NukkitCommandDescription(String description, ImmutableCollection<String> aliases, String usage,
                                    String permission, String permissionMessage) {
        this.description = description;
        this.aliases = aliases;
        this.usage = usage;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public ImmutableCollection<String> getAliases() {
        return aliases;
    }

    public Optional<String> getUsage() {
        return Optional.ofNullable(usage);
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public Optional<String> getPermissionMessage() {
        return Optional.ofNullable(permissionMessage);
    }
}
