package cn.nukkit.api.plugin;

import com.google.common.collect.ImmutableCollection;

import java.util.Optional;

public interface CommandDescription {

    Optional<String> getDescription();

    ImmutableCollection<String> getAliases();

    Optional<String> getUsage();

    Optional<String> getPermission();

    Optional<String> getPermissionMessage();
}
