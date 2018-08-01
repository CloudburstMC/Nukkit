package com.nukkitx.api.permission;

import com.nukkitx.api.plugin.PluginContainer;

import java.util.Map;

public interface PermissionAttachment {
    PluginContainer getPlugin();

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
