package cn.nukkit.api.permission;

public interface PermissionAttachmentInfo {
    Permissible getPermissible();

    String getPermission();

    PermissionAttachment getAttachment();

    boolean getValue();
}
