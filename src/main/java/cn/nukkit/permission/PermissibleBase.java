package cn.nukkit.permission;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.ServerException;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PermissibleBase implements Permissible {

    ServerOperator opable = null;

    private Permissible parent = null;

    private Map<PermissionAttachment, PermissionAttachment> attachments = new HashMap<>();

    private Map<String, PermissionAttachmentInfo> permissions = new HashMap<>();

    public PermissibleBase(ServerOperator opable) {
        this.opable = opable;
        if (opable instanceof Permissible) {
            this.parent = (Permissible) opable;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.parent = null;
        this.opable = null;
    }

    @Override
    public boolean isOp() {
        return this.opable != null && this.opable.isOp();
    }

    @Override
    public void setOp(boolean value) {
        if (this.opable == null) {
            throw new ServerException("Cannot change op value as no ServerOperator is set");
        } else {
            this.opable.setOp(value);
        }
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.permissions.containsKey(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.isPermissionSet(permission.getName());
    }

    @Override
    public boolean hasPermission(String name) {
        //todo
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        //todo
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        if (plugin == null) {
            throw new PluginException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new PluginException("Plugin " + plugin.getDescription().getName() + " is disabled");
        }

        PermissionAttachment result = new PermissionAttachment(plugin, this.parent != null ? this.parent : this);
        this.attachments.put(result, result);
        if (name != null && value != null) {
            result.setPermission(name, value);
        }
        this.recalculatePermissions();

        return result;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        if (attachment == null) {
            throw new IllegalStateException("Attachment cannot be null");
        }

        if (this.attachments.containsKey(attachment)) {
            this.attachments.remove(attachment);
            PermissionRemovedExecutor ex = attachment.getRemovalCallback();
            if (ex != null) {
                ex.attachmentRemoved(attachment);
            }
            this.recalculatePermissions();
        }
    }

    @Override
    public void recalculatePermissions() {
        //todo
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.permissions;
    }
}
