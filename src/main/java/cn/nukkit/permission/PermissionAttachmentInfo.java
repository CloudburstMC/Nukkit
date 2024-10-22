package cn.nukkit.permission;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class PermissionAttachmentInfo {

    private final Permissible permissible;

    private final String permission;

    private final PermissionAttachment attachment;

    private boolean value;

    public PermissionAttachmentInfo(Permissible permissible, String permission, PermissionAttachment attachment, boolean value) {
        if (permission == null) {
            throw new IllegalStateException("Permission may not be null");
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

    /**
     * Set value.
     *
     * @param value value
     */
    public void setValue(boolean value) {
        this.value = value;
    }
}
