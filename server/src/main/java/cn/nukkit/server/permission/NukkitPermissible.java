package cn.nukkit.server.permission;

import cn.nukkit.api.permission.*;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.util.PluginException;
import cn.nukkit.server.util.ServerException;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitPermissible implements Permissible {

    ServerOperator opable = null;

    private Permissible parent = null;

    private final Set<NukkitPermissionAttachment> attachments = new HashSet<>();

    private final Map<String, PermissionAttachmentInfo> permissions = new HashMap<>();

    public NukkitPermissible(ServerOperator opable) {
        this.opable = opable;
        if (opable instanceof Permissible) {
            this.parent = (Permissible) opable;
        }
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
    public boolean isPermissionSet(Permission nukkitPermission) {
        return this.isPermissionSet(nukkitPermission.getName());
    }

    @Override
    public boolean hasPermission(String name) {
        if (this.isPermissionSet(name)) {
            return this.permissions.get(name).getValue();
        }

        Optional<Permission> perm = NukkitServer.getInstance().getPermissionManager().getPermission(name);

        if (perm.isPresent()) {
            String permission = perm.get().getDefault();

            return NukkitPermission.DEFAULT_TRUE.equals(permission) || (this.isOp() && NukkitPermission.DEFAULT_OP.equals(permission)) || (!this.isOp() && NukkitPermission.DEFAULT_NOT_OP.equals(permission));
        } else {
            return NukkitPermission.DEFAULT_TRUE.equals(NukkitPermission.DEFAULT_PERMISSION) || (this.isOp() && NukkitPermission.DEFAULT_OP.equals(NukkitPermission.DEFAULT_PERMISSION)) || (!this.isOp() && NukkitPermission.DEFAULT_NOT_OP.equals(NukkitPermission.DEFAULT_PERMISSION));
        }
    }

    @Override
    public boolean hasPermission(Permission nukkitPermission) {
        return this.hasPermission(nukkitPermission.getName());
    }

    @Override
    public NukkitPermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null, null);
    }

    @Override
    public NukkitPermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    @Override
    public NukkitPermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin " + plugin.getDescription().getName() + " is disabled");
        }

        NukkitPermissionAttachment result = new NukkitPermissionAttachment(plugin, this.parent != null ? this.parent : this);
        this.attachments.add(result);
        if (name != null && value != null) {
            result.setPermission(name, value);
        }
        this.recalculatePermissions();

        return result;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        if (this.attachments.contains(attachment)) {
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
        this.clearPermissions();
        Map<String, Permission> defaults = NukkitServer.getInstance().getPermissionManager().getDefaultPermissions(this.isOp());
        NukkitServer.getInstance().getPermissionManager().subscribeToDefaultPerms(this.isOp(), this.parent != null ? this.parent : this);

        for (Permission perm : defaults.values()) {
            String name = perm.getName();
            this.permissions.put(name, new NukkitPermissionAttachmentInfo(this.parent != null ? this.parent : this, name, null, true));
            NukkitServer.getInstance().getPermissionManager().subscribeToPermission(name, this.parent != null ? this.parent : this);
            this.calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (NukkitPermissionAttachment attachment : this.attachments) {
            this.calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
    }

    public void clearPermissions() {
        for (String name : this.permissions.keySet()) {
            NukkitServer.getInstance().getPermissionManager().unsubscribeFromPermission(name, this.parent != null ? this.parent : this);
        }


        NukkitServer.getInstance().getPermissionManager().unsubscribeFromDefaultPerms(false, this.parent != null ? this.parent : this);
        NukkitServer.getInstance().getPermissionManager().unsubscribeFromDefaultPerms(true, this.parent != null ? this.parent : this);

        this.permissions.clear();
    }

    private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, NukkitPermissionAttachment attachment) {
        for (Map.Entry<String, Boolean> entry : children.entrySet()) {
            String name = entry.getKey();
            Optional<Permission> perm = NukkitServer.getInstance().getPermissionManager().getPermission(name);
            boolean v = entry.getValue();
            boolean value = (v ^ invert);
            this.permissions.put(name, new NukkitPermissionAttachmentInfo(this.parent != null ? this.parent : this, name, attachment, value));
            NukkitServer.getInstance().getPermissionManager().subscribeToPermission(name, this.parent != null ? this.parent : this);

            perm.ifPresent(permission -> calculateChildPermissions(permission.getChildren(), !value, attachment));
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.permissions;
    }
}
