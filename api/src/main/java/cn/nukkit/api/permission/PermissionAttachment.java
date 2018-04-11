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

package cn.nukkit.api.permission;

import cn.nukkit.api.plugin.Plugin;

import java.util.Map;

public interface PermissionAttachment {
    Plugin getPlugin();

    Map<String, Boolean> getPermissions();

    PermissionRemovedExecutor getRemovalCallback();

    void setRemovalCallback(PermissionRemovedExecutor executor);

    void clearPermissions();

    void setPermissions(Map<String, Boolean> permissions);

    void setPermission(Permission nukkitPermission, boolean value);

    void setPermission(String name, boolean value);

    void unsetPermission(Permission nukkitPermission, boolean value);

    void unsetPermission(String name, boolean value);

    void remove();
}
