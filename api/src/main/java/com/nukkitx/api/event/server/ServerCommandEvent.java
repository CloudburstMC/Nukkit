package com.nukkitx.api.event.server;

import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.event.Cancellable;

public class ServerCommandEvent implements ServerEvent, Cancellable {
    private final CommandSender sender;
    private String command;
    private boolean cancelled;

    public ServerCommandEvent(CommandSender sender, String command) {
        this.sender = sender;
        this.command = command;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
