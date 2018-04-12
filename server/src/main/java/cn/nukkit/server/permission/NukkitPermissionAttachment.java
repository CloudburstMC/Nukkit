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

package cn.nukkit.server.permission;

import cn.nukkit.api.permission.Permissible;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.permission.PermissionAttachment;
import cn.nukkit.api.permission.PermissionRemovedExecutor;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.util.PluginException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitPermissionAttachment implements PermissionAttachment {

    private PermissionRemovedExecutor removed = null;

    private final Map<String, Boolean> permissions = new HashMap<>();

    private Permissible permissible;

    private Plugin plugin;

    public NukkitPermissionAttachment(Plugin plugin, Permissible permissible) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin " + plugin.getDescription().getName() + " is disabled");
        }
        this.permissible = permissible;
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setRemovalCallback(PermissionRemovedExecutor executor) {
        this.removed = executor;
    }

    public PermissionRemovedExecutor getRemovalCallback() {
        return removed;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void clearPermissions() {
        this.permissions.clear();
        this.permissible.recalculatePermissions();
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            this.permissions.put(key, value);
        }
        this.permissible.recalculatePermissions();
    }

    public void unsetPermissions(List<String> permissions) {
        for (String node : permissions) {
            this.permissions.remove(node);
        }
        this.permissible.recalculatePermissions();
    }

    public void setPermission(Permission nukkitPermission, boolean value) {
        this.setPermission(nukkitPermission.getName(), value);
    }

    public void setPermission(String name, boolean value) {
        if (this.permissions.containsKey(name)) {
            if (this.permissions.get(name).equals(value)) {
                return;
            }
            this.permissions.remove(name);
        }
        this.permissions.put(name, value);
        this.permissible.recalculatePermissions();
    }

    public void unsetPermission(Permission nukkitPermission, boolean value) {
        this.unsetPermission(nukkitPermission.getName(), value);
    }

    public void unsetPermission(String name, boolean value) {
        if (this.permissions.containsKey(name)) {
            this.permissions.remove(name);
            this.permissible.recalculatePermissions();
        }
    }

    public void remove() {
        this.permissible.removeAttachment(this);
    }

}
