package cn.nukkit.server.permission;

import cn.nukkit.api.permission.Permissible;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.permission.ServerOperator;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.utils.PluginException;
import cn.nukkit.server.utils.ServerException;
import co.aikar.timings.Timings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NukkitPermissible implements Permissible {

    ServerOperator opable = null;

    private Permissible parent = null;

    private final Set<PermissionAttachment> attachments = new HashSet<>();

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

        NukkitPermission perm = NukkitServer.getInstance().getPluginManager().getPermission(name);

        if (perm != null) {
            String permission = perm.getDefault();

            return NukkitPermission.DEFAULT_TRUE.equals(permission) || (this.isOp() && NukkitPermission.DEFAULT_OP.equals(permission)) || (!this.isOp() && NukkitPermission.DEFAULT_NOT_OP.equals(permission));
        } else {
            return NukkitPermission.DEFAULT_TRUE.equals(NukkitPermission.DEFAULT_PERMISSION) || (this.isOp() && NukkitPermission.DEFAULT_OP.equals(NukkitPermission.DEFAULT_PERMISSION)) || (!this.isOp() && NukkitPermission.DEFAULT_NOT_OP.equals(NukkitPermission.DEFAULT_PERMISSION));
        }
    }

    @Override
    public boolean hasPermission(NukkitPermission nukkitPermission) {
        return this.hasPermission(nukkitPermission.getName());
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
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin " + plugin.getDescription().getName() + " is disabled");
        }

        PermissionAttachment result = new PermissionAttachment(plugin, this.parent != null ? this.parent : this);
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
        Timings.permissibleCalculationTimer.startTiming();

        this.clearPermissions();
        Map<String, NukkitPermission> defaults = NukkitServer.getInstance().getPluginManager().getDefaultPermissions(this.isOp());
        NukkitServer.getInstance().getPluginManager().subscribeToDefaultPerms(this.isOp(), this.parent != null ? this.parent : this);

        for (NukkitPermission perm : defaults.values()) {
            String name = perm.getName();
            this.permissions.put(name, new PermissionAttachmentInfo(this.parent != null ? this.parent : this, name, null, true));
            NukkitServer.getInstance().getPluginManager().subscribeToPermission(name, this.parent != null ? this.parent : this);
            this.calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (PermissionAttachment attachment : this.attachments) {
            this.calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
        Timings.permissibleCalculationTimer.stopTiming();
    }

    public void clearPermissions() {
        for (String name : this.permissions.keySet()) {
            NukkitServer.getInstance().getPluginManager().unsubscribeFromPermission(name, this.parent != null ? this.parent : this);
        }


        NukkitServer.getInstance().getPluginManager().unsubscribeFromDefaultPerms(false, this.parent != null ? this.parent : this);
        NukkitServer.getInstance().getPluginManager().unsubscribeFromDefaultPerms(true, this.parent != null ? this.parent : this);

        this.permissions.clear();
    }

    private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
        for (Map.Entry<String, Boolean> entry : children.entrySet()) {
            String name = entry.getKey();
            NukkitPermission perm = NukkitServer.getInstance().getPluginManager().getPermission(name);
            boolean v = entry.getValue();
            boolean value = (v ^ invert);
            this.permissions.put(name, new PermissionAttachmentInfo(this.parent != null ? this.parent : this, name, attachment, value));
            NukkitServer.getInstance().getPluginManager().subscribeToPermission(name, this.parent != null ? this.parent : this);

            if (perm != null) {
                this.calculateChildPermissions(perm.getChildren(), !value, attachment);
            }
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.permissions;
    }
}
