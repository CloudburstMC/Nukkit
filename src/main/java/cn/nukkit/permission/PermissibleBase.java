package cn.nukkit.permission;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.ServerException;
import co.aikar.timings.Timings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PermissibleBase implements Permissible {

    ServerOperator opable = null;

    private Permissible parent = null;

    private final Set<PermissionAttachment> attachments = new HashSet<>();

    private final Map<String, PermissionAttachmentInfo> permissions = new HashMap<>();

    public PermissibleBase(ServerOperator opable) {
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
    public boolean isPermissionSet(Permission permission) {
        return this.isPermissionSet(permission.getName());
    }

    @Override
    public boolean hasPermission(String name) {
		if(this.isPermissionSet("*") && ((PermissionAttachmentInfo)this.permissions.get("*")).getValue()){
		   if(this.isPermissionSet("-" + name) && ((PermissionAttachmentInfo)this.permissions.get("-" + name)).getValue()){
			   return false;
		   }else{
			   return true;
		   }
		}
        if (this.isPermissionSet(name)) {
            return this.permissions.get(name).getValue();
        }

        Permission perm = Server.getInstance().getPluginManager().getPermission(name);

        if (perm != null) {
            String permission = perm.getDefault();

            return Permission.DEFAULT_TRUE.equals(permission) || (this.isOp() && Permission.DEFAULT_OP.equals(permission)) || (!this.isOp() && Permission.DEFAULT_NOT_OP.equals(permission));
        } else {
            return Permission.DEFAULT_TRUE.equals(Permission.DEFAULT_PERMISSION) || (this.isOp() && Permission.DEFAULT_OP.equals(Permission.DEFAULT_PERMISSION)) || (!this.isOp() && Permission.DEFAULT_NOT_OP.equals(Permission.DEFAULT_PERMISSION));
        }
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.hasPermission(permission.getName());
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
        Map<String, Permission> defaults = Server.getInstance().getPluginManager().getDefaultPermissions(this.isOp());
        Server.getInstance().getPluginManager().subscribeToDefaultPerms(this.isOp(), this.parent != null ? this.parent : this);

        for (Permission perm : defaults.values()) {
            String name = perm.getName();
            this.permissions.put(name, new PermissionAttachmentInfo(this.parent != null ? this.parent : this, name, null, true));
            Server.getInstance().getPluginManager().subscribeToPermission(name, this.parent != null ? this.parent : this);
            this.calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (PermissionAttachment attachment : this.attachments) {
            this.calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
        Timings.permissibleCalculationTimer.stopTiming();
    }

    public void clearPermissions() {
        for (String name : this.permissions.keySet()) {
            Server.getInstance().getPluginManager().unsubscribeFromPermission(name, this.parent != null ? this.parent : this);
        }


        Server.getInstance().getPluginManager().unsubscribeFromDefaultPerms(false, this.parent != null ? this.parent : this);
        Server.getInstance().getPluginManager().unsubscribeFromDefaultPerms(true, this.parent != null ? this.parent : this);

        this.permissions.clear();
    }

    private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
        for (Map.Entry<String, Boolean> entry : children.entrySet()) {
            String name = entry.getKey();
            Permission perm = Server.getInstance().getPluginManager().getPermission(name);
            boolean v = entry.getValue();
            boolean value = (v ^ invert);
            this.permissions.put(name, new PermissionAttachmentInfo(this.parent != null ? this.parent : this, name, attachment, value));
            Server.getInstance().getPluginManager().subscribeToPermission(name, this.parent != null ? this.parent : this);

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
