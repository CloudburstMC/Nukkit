package cn.nukkit.event.server;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ServerCommandEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected String command;

    protected final CommandSource sender;

    public ServerCommandEvent(CommandSource sender, String command) {
        this.sender = sender;
        this.command = command;
    }

    public CommandSource getSender() {
        return sender;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
