package com.nukkitx.api.command;

import java.util.Collection;

public interface CommandBuilder {

    CommandBuilder setExecutor(CommandExecutor executor);

    CommandBuilder setCommand(String command);

    CommandBuilder setAliases(Collection<String> aliases);

    CommandBuilder setAlias(String alias);

    CommandBuilder clearAliases();

    CommandBuilder removeAlias(String alias);

    CommandBuilder removeAliases(Collection<String> aliases);

    CommandBuilder setUsageMessage(String usageMessage);

    CommandBuilder setDescription(String description);

    CommandBuilder setPermission(String permission);

    Command build();
}
