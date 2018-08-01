package com.nukkitx.api.permission;

import com.nukkitx.api.plugin.PluginContainer;

import java.util.Map;

public interface Permissible extends ServerOperator {

    boolean isPermissionSet(String name);

    boolean isPermissionSet(Permission permission);

    boolean hasPermission(String name);

    boolean hasPermission(Permission permission);

    PermissionAttachment addAttachment(PluginContainer plugin);

    PermissionAttachment addAttachment(PluginContainer plugin, String name);

    PermissionAttachment addAttachment(PluginContainer plugin, String name, Boolean value);

    void removeAttachment(PermissionAttachment attachment);

    void recalculatePermissions();

    Map<String, PermissionAttachmentInfo> getEffectivePermissions();
}
