package cn.nukkit.server.plugin;

import cn.nukkit.api.plugin.PermissionDescription;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class NukkitPermissionDescription implements PermissionDescription {
    private final String description;
    private final String defaultValue;
    private final ImmutableMap<String, PermissionDescription> childrenPermissions;

    public String getDescription() {
        return description;
    }

    public String getDefault() {
        return defaultValue;
    }

    public ImmutableMap<String, PermissionDescription> getChildPermissions() {
        return childrenPermissions;
    }
}
