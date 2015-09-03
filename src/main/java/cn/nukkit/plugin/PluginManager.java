package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.permission.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginManager {

    private Server server;

    private SimpleCommandMap commandMap;

    protected List<Plugin> plugins = new ArrayList<>();

    protected Map<String, Permission> permissions = new HashMap<String, Permission>();

    //public PluginManager(Server server, SimpleComm)
}
