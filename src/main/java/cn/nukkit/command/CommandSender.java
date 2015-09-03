package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.event.TextContainer;
import cn.nukkit.permission.Permissible;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface CommandSender extends Permissible {

    void sendMessage(String message);

    void sendMessage(TextContainer message);

    Server getServer();

    String getName();

}
