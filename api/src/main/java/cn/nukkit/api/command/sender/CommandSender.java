package cn.nukkit.api.command.sender;

import cn.nukkit.api.Server;
import cn.nukkit.api.permission.Permissible;

public interface CommandSender extends Permissible {

    String getName();

    Server getServer();
}
