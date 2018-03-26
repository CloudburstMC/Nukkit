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
