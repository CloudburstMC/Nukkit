package com.nukkitx.api.command;

import com.nukkitx.api.plugin.Plugin;

import javax.annotation.Nonnull;

public interface CommandManager {
    void register(@Nonnull Plugin plugin, @Nonnull Command command) throws CommandException;

    CommandData getDefaultData();

    void setDefaultData(@Nonnull CommandData data);
}
