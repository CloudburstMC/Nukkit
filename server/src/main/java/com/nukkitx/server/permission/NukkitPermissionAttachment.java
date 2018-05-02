package com.nukkitx.server.permission;

import com.nukkitx.api.permission.Permissible;
import com.nukkitx.api.permission.Permission;
import com.nukkitx.api.permission.PermissionAttachment;
import com.nukkitx.api.permission.PermissionRemovedExecutor;
import com.nukkitx.api.plugin.Plugin;
import com.nukkitx.server.util.PluginException;

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
