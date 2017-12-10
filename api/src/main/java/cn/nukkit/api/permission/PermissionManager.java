package cn.nukkit.api.permission;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PermissionManager {

    @Nonnull
    Optional<Permission> getPermission(String name);

    boolean addPermission(Permission nukkitPermission);

    void removePermission(String name);

    void removePermission(Permission permission);

    @Nonnull
    Map<String, Permission> getDefaultPermissions(boolean op);

    void recalculatePermissionDefaults(Permission nukkitPermission);

    void subscribeToPermission(String permission, Permissible permissible);

    void unsubscribeFromPermission(String permission, Permissible permissible);

    @Nonnull
    Set<Permissible> getPermissionSubscriptions(String permission);

    void subscribeToDefaultPerms(boolean op, Permissible permissible);

    void unsubscribeFromDefaultPerms(boolean op, Permissible permissible);

    @Nonnull
    Set<Permissible> getDefaultPermSubscriptions(boolean op);

    @Nonnull
    Map<String, Permission> getPermissions();
}
