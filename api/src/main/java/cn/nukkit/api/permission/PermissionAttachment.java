package cn.nukkit.api.permission;

import cn.nukkit.api.plugin.Plugin;

import java.util.Set;

public interface PermissionAttachment {
    Plugin getPlugin();

    Set<Permission> getPermissions();

    void clearPermissions();

    void addPermission(Permission permission);

    void removePermission(Permission permission);

    void remove();
}
