package cn.nukkit.permission;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PermissionAttachment {

    private PermissionRemovedExecutor removed = null;

    private final Map<String, Boolean> permissions = new HashMap<>();

    private Permissible permissible;

    private Plugin plugin;

    public PermissionAttachment(Plugin plugin, Permissible permissible) {
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

    public void setPermission(Permission permission, boolean value) {
        this.setPermission(permission.getName(), value);
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

    public void unsetPermission(Permission permission, boolean value) {
        this.unsetPermission(permission.getName(), value);
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
