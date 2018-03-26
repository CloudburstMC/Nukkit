package cn.nukkit.api.command;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Singular;

import javax.annotation.Nullable;
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

    public CommandData(@Nullable String permission, @Nullable Collection<String> aliases, @Nullable String description,
                       @Nullable String usage, @Nullable String permissionMessage) {
        this.permission = Strings.isNullOrEmpty(permission) ? null : permission;
        this.aliases = aliases == null ? ImmutableList.of() : ImmutableList.copyOf(aliases);
        this.description = Strings.isNullOrEmpty(description) ? null : description;
        this.usage = Strings.isNullOrEmpty(usage) ? null : usage;
        this.permissionMessage = Strings.isNullOrEmpty(permissionMessage) ? null : permissionMessage;
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
