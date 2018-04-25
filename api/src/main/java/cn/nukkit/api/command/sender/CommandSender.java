package cn.nukkit.api.command.sender;

import cn.nukkit.api.Server;
import cn.nukkit.api.command.MessageRecipient;
import cn.nukkit.api.permission.Permissible;

import java.util.Locale;

public interface CommandSender extends Permissible, MessageRecipient {

    String getName();

    Server getServer();

    Locale getLocale();
}
