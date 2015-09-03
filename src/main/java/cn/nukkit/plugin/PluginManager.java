package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginManager {

    private Server server;

    private SimpleCommandMap commandMap;

    protected Map<String, Plugin> plugins = new HashMap<>();

    protected Map<String, Permission> permissions = new HashMap<>();

    protected Map<String, Permission> defaultPerms = new HashMap<>();

    protected Map<String, Permission> defaultPermsOp = new HashMap<>();

    protected Map<String, Permissible> permSubs = new HashMap<>();

    protected Map<String, Permissible> defSubs = new HashMap<>();

    protected Map<String, Permissible> defSubsOp = new HashMap<>();

    protected Map<String, PluginLoader> fileAssociations = new HashMap<>();

    public PluginManager(Server server, SimpleCommandMap commandMap) {
        this.server = server;
        this.commandMap = commandMap;
    }

    public Plugin getPlugin(String name) {
        if (this.plugins.containsKey(name)) {
            return this.plugins.get(name);
        }
        return null;
    }

    public boolean registerInterface(Class<? extends PluginLoader> loaderClass) {
        if (PluginLoader.class.isAssignableFrom(loaderClass)) {
            try {
                Constructor constructor = loaderClass.getDeclaredConstructor(Server.class);
                constructor.setAccessible(true);
                this.fileAssociations.put(loaderClass.getName(), (PluginLoader) constructor.newInstance(this.server));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public Map<String, Plugin> getPlugins() {
        return plugins;
    }
}
