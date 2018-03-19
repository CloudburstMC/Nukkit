package cn.nukkit.api.command;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Singular;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Builder
public class CommandData {
    private final String permission;
    @Singular
    private final List<String> aliases;
    private final String description;
    private final String usage;
    private final String permissionMessage;

    public CommandData(String permission, Collection<String> aliases, String description, String usage, String permissionMessage) {
        this.permission = permission;
        this.aliases = aliases == null ? ImmutableList.of() : ImmutableList.copyOf(aliases);
        this.description = description;
        this.usage = usage;
        this.permissionMessage = permissionMessage;
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public List<String> getAliases() {
        return ImmutableList.copyOf(aliases);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<String> getUsage() {
        return Optional.ofNullable(usage);
    }

    public Optional<String> getPermissionMessage() {
        return Optional.ofNullable(permissionMessage);
    }
}
