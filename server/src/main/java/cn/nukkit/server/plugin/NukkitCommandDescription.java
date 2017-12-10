package cn.nukkit.server.plugin;

import cn.nukkit.api.plugin.CommandDescription;
import com.google.common.collect.ImmutableCollection;

import java.util.Optional;

public final class NukkitCommandDescription implements CommandDescription {
    private final String description;
    private final ImmutableCollection<String> aliases;
    private final String usage;
    private final String permission;
    private final String permissionMessage;

    public NukkitCommandDescription(String description, ImmutableCollection<String> aliases, String usage,
                                    String permission, String permissionMessage) {
        this.description = description;
        this.aliases = aliases;
        this.usage = usage;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public ImmutableCollection<String> getAliases() {
        return aliases;
    }

    public Optional<String> getUsage() {
        return Optional.ofNullable(usage);
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public Optional<String> getPermissionMessage() {
        return Optional.ofNullable(permissionMessage);
    }
}
