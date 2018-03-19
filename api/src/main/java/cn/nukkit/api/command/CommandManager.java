package cn.nukkit.api.command;

import javax.annotation.Nonnull;

public interface CommandManager {
    void register(@Nonnull String fallbackPrefix, @Nonnull Command command) throws CommandException;

    CommandData getDefaultData();

    void setDefaultData(@Nonnull CommandData data);
}
