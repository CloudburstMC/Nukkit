package cn.nukkit.api.command.simple;


import cn.nukkit.api.MessageRecipient;
import cn.nukkit.api.command.Command;

public interface SimpleCommand extends Command {

    void setForbidConsole(boolean forbidConsole);

    void setMaxArgs(int maxArgs);

    void setMinArgs(int minArgs);

    void sendUsageMessage(MessageRecipient sender);

    void sendInGameMessage(MessageRecipient sender);


}
