package com.nukkitx.api.command;

import com.nukkitx.api.plugin.PluginDescription;

import javax.annotation.Nonnull;

public interface CommandManager {
    void register(@Nonnull PluginDescription plugin, @Nonnull Command command) throws CommandException;

    CommandData getDefaultData();

    void setDefaultData(@Nonnull CommandData data);
}
