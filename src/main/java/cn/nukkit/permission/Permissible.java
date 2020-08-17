package cn.nukkit.permission;

import cn.nukkit.plugin.Plugin;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface Permissible extends ServerOperator {

    boolean isPermissionSet(String name);

    boolean isPermissionSet(Permission permission);

    boolean hasPermission(String name);

    boolean hasPermission(Permission permission);

    PermissionAttachment addAttachment(Plugin plugin);

    PermissionAttachment addAttachment(Plugin plugin, String name);

    PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value);

    void removeAttachment(PermissionAttachment attachment);

    void recalculatePermissions();

    Map<String, PermissionAttachmentInfo> getEffectivePermissions();

}
