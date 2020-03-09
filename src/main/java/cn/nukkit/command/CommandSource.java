package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.locale.TextContainer;
import cn.nukkit.permission.Permissible;

public interface CommandSource extends Permissible {

    void sendMessage(String message);

    void sendMessage(TextContainer container);

    Server getServer();

    String getName();

    boolean isPlayer();
}
