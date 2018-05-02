package com.nukkitx.api.command.simple;

import com.nukkitx.api.command.Command;
import com.nukkitx.api.command.CommandExecutor;
import com.nukkitx.api.command.MessageRecipient;

public interface SimpleCommand extends Command, CommandExecutor {

    void setForbidConsole(boolean forbidConsole);

    void setMaxArgs(int maxArgs);

    void setMinArgs(int minArgs);

    void sendUsageMessage(MessageRecipient sender);

    void sendInGameMessage(MessageRecipient sender);
}
