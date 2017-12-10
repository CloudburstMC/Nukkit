package cn.nukkit.server.permission;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PermissionAttachmentInfo {

    private Permissible permissible;

    private String permission;

    private PermissionAttachment attachment;

    private boolean value;

    public PermissionAttachmentInfo(Permissible permissible, String permission, PermissionAttachment attachment, boolean value) {
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

    public PermissionAttachment getAttachment() {
        return attachment;
    }

    public boolean getValue() {
        return value;
    }
}
