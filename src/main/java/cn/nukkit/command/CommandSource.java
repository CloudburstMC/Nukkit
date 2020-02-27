package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.permission.Permissible;

public interface CommandSource extends Permissible {

    void sendMessage(String message);

    void sendMessage(TextContainer container);

    Server getServer();

    String getName();

    boolean isPlayer();
}
