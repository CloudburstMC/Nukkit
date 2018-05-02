package com.nukkitx.server.permission;

import com.nukkitx.api.permission.Permissible;
import com.nukkitx.api.permission.PermissionAttachmentInfo;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitPermissionAttachmentInfo implements PermissionAttachmentInfo {

    private Permissible permissible;

    private String permission;

    private NukkitPermissionAttachment attachment;

    private boolean value;

    public NukkitPermissionAttachmentInfo(Permissible permissible, String permission, NukkitPermissionAttachment attachment, boolean value) {
        if (permission == null) {
            throw new IllegalStateException("NukkitPermission may not be null");
        }

        this.permissible = permissible;
        this.permission = permission;
        this.attachment = attachment;
        this.value = value;
    }

    public Permissible getPermissible() {
        return permissible;
    }

    public String getPermission() {
        return permission;
    }

    public NukkitPermissionAttachment getAttachment() {
        return attachment;
    }

    public boolean getValue() {
        return value;
    }
}
