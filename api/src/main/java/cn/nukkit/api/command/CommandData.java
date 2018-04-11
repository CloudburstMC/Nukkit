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

package cn.nukkit.api.command;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Singular;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Builder
public class CommandData {
    private final String permission;
    @Singular
    private final List<String> aliases;
    private final String description;
    private final String usage;
    private final String permissionMessage;

    public CommandData(@Nullable String permission, @Nullable Collection<String> aliases, @Nullable String description,
                       @Nullable String usage, @Nullable String permissionMessage) {
        this.permission = Strings.isNullOrEmpty(permission) ? null : permission;
        this.aliases = aliases == null ? ImmutableList.of() : ImmutableList.copyOf(aliases);
        this.description = Strings.isNullOrEmpty(description) ? null : description;
        this.usage = Strings.isNullOrEmpty(usage) ? null : usage;
        this.permissionMessage = Strings.isNullOrEmpty(permissionMessage) ? null : permissionMessage;
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public List<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<String> getUsage() {
        return Optional.ofNullable(usage);
    }

    public Optional<String> getPermissionMessage() {
        return Optional.ofNullable(permissionMessage);
    }
}
