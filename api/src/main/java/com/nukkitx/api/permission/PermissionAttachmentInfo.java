package com.nukkitx.api.permission;

public interface PermissionAttachmentInfo {
    Permissible getPermissible();

    String getPermission();

    PermissionAttachment getAttachment();

    boolean getValue();
}
