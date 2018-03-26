package cn.nukkit.server.permission;

import cn.nukkit.api.permission.Permissible;
import cn.nukkit.api.permission.Permission;
import cn.nukkit.api.permission.PermissionManager;

import javax.annotation.Nonnull;
import java.util.*;

public class NukkitPermissionManager implements PermissionManager {
    private final Map<String, Permission> permissions = new HashMap<>();
    private final Map<String, Permission> defaultPerms = new HashMap<>();
    private final Map<String, Permission> defaultPermsOp = new HashMap<>();
    private final Map<String, WeakHashMap<Permissible, Permissible>> permSubs = new HashMap<>();
    private final Map<Permissible, Permissible> defSubs = new WeakHashMap<>();
    private final Map<Permissible, Permissible> defSubsOp = new WeakHashMap<>();

    @Nonnull
    @Override
    public Optional<Permission> getPermission(String name) {
        if (this.permissions.containsKey(name)) {
            return Optional.ofNullable(permissions.get(name));
        }
        return Optional.empty();
    }

    @Override
    public boolean addPermission(Permission permission) {
        if (!this.permissions.containsKey(permission.getName())) {
            this.permissions.put(permission.getName(), permission);
            this.calculatePermissionDefault(permission);

            return true;
        }
        return false;
    }

    @Override
    public void removePermission(String name) {
        this.permissions.remove(name);
    }

    @Override
    public void removePermission(Permission permission) {
        this.removePermission(permission.getName());
    }

    @Nonnull
    @Override
    public Map<String, Permission> getDefaultPermissions(boolean op) {
        if (op) {
            return this.defaultPermsOp;
        } else {
            return this.defaultPerms;
        }
    }

    @Override
    public void recalculatePermissionDefaults(Permission permission) {
        if (this.permissions.containsKey(permission.getName())) {
            this.defaultPermsOp.remove(permission.getName());
            this.defaultPerms.remove(permission.getName());
            this.calculatePermissionDefault(permission);
        }
    }

    private void calculatePermissionDefault(Permission permission) {
        if (permission.getDefault().equals(Permission.DEFAULT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPermsOp.put(permission.getName(), permission);
            this.dirtyPermissibles(true);
        }

        if (permission.getDefault().equals(Permission.DEFAULT_NOT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPerms.put(permission.getName(), permission);
            this.dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op) {
        for (Permissible p : this.getDefaultPermSubscriptions(op)) {
            p.recalculatePermissions();
        }
    }

    @Override
    public void subscribeToPermission(String permission, Permissible permissible) {
        if (!this.permSubs.containsKey(permission)) {
            this.permSubs.put(permission, new WeakHashMap<>());
        }
        this.permSubs.get(permission).put(permissible, permissible);
    }

    @Override
    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        if (this.permSubs.containsKey(permission)) {
            this.permSubs.get(permission).remove(permissible);
            if (this.permSubs.get(permission).size() == 0) {
                this.permSubs.remove(permission);
            }
        }
    }

    @Nonnull
    @Override
    public Set<Permissible> getPermissionSubscriptions(String permission) {
        if (this.permSubs.containsKey(permission)) {
            Set<Permissible> subs = new HashSet<>();
            for (Permissible p : this.permSubs.get(permission).values()) {
                subs.add(p);
            }
            return subs;
        }

        return new HashSet<>();
    }

    @Override
    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.put(permissible, permissible);
        } else {
            this.defSubs.put(permissible, permissible);
        }
    }

    @Override
    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.remove(permissible);
        } else {
            this.defSubs.remove(permissible);
        }
    }

    @Nonnull
    @Override
    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        Set<Permissible> subs = new HashSet<>();
        if (op) {
            subs.addAll(this.defSubsOp.values());
        } else {
            subs.addAll(this.defSubs.values());
        }
        return subs;
    }

    @Nonnull
    @Override
    public Map<String, Permission> getPermissions() {
        return permissions;
    }
}
