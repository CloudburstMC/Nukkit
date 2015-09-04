package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.HandlerList;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Pattern;

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

    protected Map<String, WeakHashMap<Integer, Permissible>> permSubs = new HashMap<>();

    protected Map<Integer, Permissible> defSubs = new WeakHashMap<>();

    protected Map<Integer, Permissible> defSubsOp = new WeakHashMap<>();

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

    public Plugin loadPlugin(String path) {
        return this.loadPlugin(path, null);
    }

    public Plugin loadPlugin(File file) {
        return this.loadPlugin(file, null);
    }

    public Plugin loadPlugin(String path, Map<String, PluginLoader> loaders) {
        return this.loadPlugin(new File(path), loaders);
    }

    public Plugin loadPlugin(File file, Map<String, PluginLoader> loaders) {
        for (PluginLoader loader : (loaders == null ? this.fileAssociations : loaders).values()) {
            for (Pattern pattern : loader.getPluginFilters()) {
                if (pattern.matcher(file.getName()).matches()) {
                    PluginDescription description = loader.getPluginDescription(file);
                    if (description != null) {
                        try {
                            Plugin plugin = loader.loadPlugin(file);
                            if (plugin != null) {
                                this.plugins.put(plugin.getDescription().getName(), plugin);

                                List<PluginCommand> pluginCommands = this.parseYamlCommands(plugin);

                                if (pluginCommands.size() > 0) {
                                    this.commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
                                }

                                return plugin;
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    public Map<String, Plugin> loadPlugins(String dictionary) {
        return this.loadPlugins(new File(dictionary));
    }

    public Map<String, Plugin> loadPlugins(File dictionary) {
        return this.loadPlugins(dictionary, null);
    }

    public Map<String, Plugin> loadPlugins(String dictionary, List<String> newLoaders) {
        return this.loadPlugins(new File(dictionary), newLoaders);
    }

    public Map<String, Plugin> loadPlugins(File dictionary, List<String> newLoaders) {
        if (dictionary.isDirectory()) {
            Map<String, File> plugins = new HashMap<>();
            Map<String, Plugin> loadedPlugins = new HashMap<>();
            Map<String, List<String>> dependencies = new HashMap<>();
            Map<String, List<String>> softDependencies = new HashMap<>();
            Map<String, PluginLoader> loaders = new HashMap<>();
            if (newLoaders != null) {
                for (String key : newLoaders) {
                    if (this.fileAssociations.containsKey(key)) {
                        loaders.put(key, this.fileAssociations.get(key));
                    }
                }
            } else {
                loaders = this.fileAssociations;
            }

            for (PluginLoader loader : loaders.values()) {
                for (File file : dictionary.listFiles((dir, name) -> {
                    for (Pattern pattern : loader.getPluginFilters()) {
                        if (pattern.matcher(name).matches()) {
                            return true;
                        }
                    }
                    return false;
                })) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    try {
                        PluginDescription description = loader.getPluginDescription(file);
                        if (description != null) {
                            String name = description.getName();
                            if (name.toLowerCase().contains("nukkit") || name.toLowerCase().contains("minecraft") || name.toLowerCase().contains("mojang")) {
                                this.server.getLogger().error(this.server.getLanguage().translateString("nukkit.plugin.loadError", new String[]{name, "%nukkit.plugin.restrictedName"}));
                                continue;
                            } else if (name.contains(" ")) {
                                this.server.getLogger().warning(this.server.getLanguage().translateString("nukkit.plugin.spacesDiscouraged", name));
                            }

                            if (plugins.containsKey(name) || this.getPlugin(name) != null) {
                                this.server.getLogger().error(this.server.getLanguage().translateString("nukkit.plugin.duplicateError", name));
                                continue;
                            }

                            boolean compatible = false;

                            for (String version : description.getCompatibleApis()) {

                                //Check the format: majorVersion.minorVersion.patch
                                if (!Pattern.matches("[0-9]\\.[0-9]\\.[0-9]", version)) {
                                    this.server.getLogger().error(this.server.getLanguage().translateString("nukkit.plugin.loadError", new String[]{name, "Wrong API format"}));
                                    continue;
                                }

                                String[] versionArray = version.split("\\.");
                                String[] apiVersion = this.server.getApiVersion().split("\\.");

                                //Completely different API version
                                if (!Objects.equals(Integer.valueOf(versionArray[0]), Integer.valueOf(apiVersion[0]))) {
                                    continue;
                                }

                                //If the plugin requires new API features, being backwards compatible
                                if (Integer.valueOf(versionArray[1]) > Integer.valueOf(apiVersion[1])) {
                                    continue;
                                }

                                compatible = true;
                                break;
                            }

                            if (!compatible) {
                                this.server.getLogger().error(this.server.getLanguage().translateString("nukkit.plugin.loadError", new String[]{name, "%nukkit.plugin.incompatibleAPI"}));
                            }

                            plugins.put(name, file);

                            softDependencies.put(name, description.getSoftDepend());

                            dependencies.put(name, description.getDepend());

                            for (String before : description.getLoadBefore()) {
                                if (softDependencies.containsKey(before)) {
                                    softDependencies.get(before).add(name);
                                } else {
                                    List<String> list = new ArrayList<>();
                                    list.add(name);
                                    softDependencies.put(before, list);
                                }
                            }
                        }
                    } catch (Exception e) {
                        this.server.getLogger().error(this.server.getLanguage().translateString("nukkit.plugin" +
                                ".fileError", new String[]{file.getName(), dictionary.toString(), Utils
                                .getExceptionMessage(e)}));
                        MainLogger logger = this.server.getLogger();
                        if (logger != null) {
                            logger.logException(e);
                        }
                    }
                }
            }

            while (plugins.size() > 0) {
                boolean missingDependency = true;
                for (Map.Entry entry : plugins.entrySet()) {
                    String name = (String) entry.getKey();
                    File file = (File) entry.getValue();
                    if (dependencies.containsKey(name)) {
                        for (String dependency : dependencies.get(name)) {
                            if (loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null) {
                                dependencies.get(name).remove(dependency);
                            } else if (!plugins.containsKey(dependency)) {
                                this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit" +
                                        ".plugin.loadError", new String[]{name, "%nukkit.plugin.unknownDependency"}));
                                break;
                            }
                        }

                        if (dependencies.get(name).size() == 0) {
                            dependencies.remove(name);
                        }
                    }

                    if (softDependencies.containsKey(name)) {
                        for (String dependency : softDependencies.get(name)) {
                            if (loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null) {
                                softDependencies.get(name).remove(dependency);
                            }
                        }

                        if (softDependencies.get(name).size() == 0) {
                            softDependencies.remove(name);
                        }
                    }

                    if (!dependencies.containsKey(name) && !softDependencies.containsKey(name)) {
                        plugins.remove(name);
                        missingDependency = false;
                        Plugin plugin = this.loadPlugin(file, loaders);
                        if (plugin != null) {
                            loadedPlugins.put(name, plugin);
                        } else {
                            this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit" +
                                    ".plugin.genericLoadError", name));
                        }
                    }
                }

                if (missingDependency) {
                    for (Map.Entry entry : plugins.entrySet()) {
                        String name = (String) entry.getKey();
                        this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit.plugin" +
                                ".loadError", new String[]{name, "%nukkit.plugin.circularDependency"}));
                        plugins.clear();
                    }
                }
            }

            return loadedPlugins;
        } else {
            return new HashMap<>();
        }
    }

    public Permission getPermission(String name) {
        if (this.permissions.containsKey(name)) {
            return this.permissions.get(name);
        }
        return null;
    }

    public boolean addPermission(Permission permission) {
        if (!this.permissions.containsKey(permission.getName())) {
            this.permissions.put(permission.getName(), permission);
            this.calculatePermissionDefault(permission);

            return true;
        }

        return false;
    }

    public void removePermission(String name) {
        this.permissions.remove(name);
    }

    public void removePermission(Permission permission) {
        this.removePermission(permission.getName());
    }

    public Map<String, Permission> getDefaultPermissions(boolean op) {
        if (op) {
            return this.defaultPermsOp;
        } else {
            return this.defaultPerms;
        }
    }

    public void recalculatePermissionDefaults(Permission permission) {
        if (this.permissions.containsKey(permission.getName())) {
            this.defaultPermsOp.remove(permission.getName());
            this.defaultPerms.remove(permission.getName());
            this.calculatePermissionDefault(permission);
        }
    }

    private void calculatePermissionDefault(Permission permission) {
        if (permission.getDefault().equals(Permission.DEFAULT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPermsOp.put(permission.getName(), permission);
            this.dirtyPermissibles(true);
        }

        if (permission.getDefault().equals(Permission.DEFAULT_NOT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPermsOp.put(permission.getName(), permission);
            this.dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op) {
        for (Permissible p : this.getDefaultPermSubscriptions(op)) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(String permission, Permissible permissible) {
        if (!this.permSubs.containsKey(permission)) {
            this.permSubs.put(permission, new WeakHashMap<>());
        }
        this.permSubs.get(permission).put(permissible.hashCode(), permissible);
    }

    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        if (this.permSubs.containsKey(permission)) {
            this.permSubs.get(permission).remove(permissible.hashCode());
            if (this.permSubs.get(permission).size() == 0) {
                this.permSubs.remove(permission);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(String permission) {
        if (this.permSubs.containsKey(permission)) {
            Set<Permissible> subs = new HashSet<>();
            for (Permissible p : this.permSubs.get(permission).values()) {
                subs.add(p);
            }
            return subs;
        }

        return new HashSet<>();
    }

    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.put(permissible.hashCode(), permissible);
        } else {
            this.defSubs.put(permissible.hashCode(), permissible);
        }
    }

    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.remove(permissible.hashCode());
        } else {
            this.defSubs.remove(permissible.hashCode());
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        Set<Permissible> subs = new HashSet<>();
        if (op) {
            for (Permissible p : this.defSubsOp.values()) {
                subs.add(p);
            }
        } else {
            for (Permissible p : this.defSubs.values()) {
                subs.add(p);
            }
        }
        return subs;
    }

    public Map<String, Permission> getPermissions() {
        return permissions;
    }

    public boolean isPluginEnabled(Plugin plugin) {
        if (plugin != null && this.plugins.containsKey(plugin.getDescription().getName())) {
            return plugin.isEnabled();
        } else {
            return false;
        }
    }

    public void enablePlugin(Plugin plugin) {
        if (!plugin.isEnabled()) {
            try {
                for (Permission permission : plugin.getDescription().getPermissions()) {
                    this.addPermission(permission);
                }
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Exception e) {
                MainLogger logger = Server.getInstance().getLogger();
                if (logger != null) {
                    logger.logException(e);
                }
                this.disablePlugin(plugin);
            }
        }
    }

    protected List<PluginCommand> parseYamlCommands(Plugin plugin) {
        List<PluginCommand> pluginCmds = new ArrayList<>();

        for (Map.Entry entry : plugin.getDescription().getCommands().entrySet()) {
            String key = (String) entry.getKey();
            Object data = entry.getValue();
            if (key.contains(":")) {
                this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit.plugin.commandError", new String[]{key, plugin.getDescription().getFullName()}));
                continue;
            }
            if (data instanceof Map) {
                PluginCommand newCmd = new PluginCommand(key, plugin);

                if (((Map) data).containsKey("description")) {
                    newCmd.setDescription((String) ((Map) data).get("description"));
                }

                if (((Map) data).containsKey("usage")) {
                    newCmd.setUsage((String) ((Map) data).get("usage"));
                }

                if (((Map) data).containsKey("aliases")) {
                    Object aliases = ((Map) data).get("aliases");
                    if (aliases instanceof List) {
                        List<String> aliasList = new ArrayList<>();
                        for (String alias : (List<String>) aliases) {
                            if (alias.contains(":")) {
                                this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit.plugin.aliasError", new String[]{alias, plugin.getDescription().getFullName()}));
                                continue;
                            }
                            aliasList.add(alias);
                        }

                        newCmd.setAliases(aliasList);
                    }
                }

                if (((Map) data).containsKey("permission")) {
                    newCmd.setPermission((String) ((Map) data).get("permission"));
                }

                if (((Map) data).containsKey("permission-message")) {
                    newCmd.setPermissionMessage((String) ((Map) data).get("permission-message"));
                }

                pluginCmds.add(newCmd);
            }
        }

        return pluginCmds;
    }

    public void disablePlugins() {
        for (Plugin plugin : this.getPlugins().values()) {
            this.disablePlugin(plugin);
        }
    }

    public void disablePlugin(Plugin plugin) {
        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Exception e) {
                MainLogger logger = Server.getInstance().getLogger();
                if (logger != null) {
                    logger.logException(e);
                }
            }

            this.server.getScheduler().cancelTask(plugin);
            HandlerList.unregisterAll(plugin);
            for (Permission permission : plugin.getDescription().getPermissions()) {
                this.removePermission(permission);
            }
        }
    }

    public void clearPlugins() {
        this.disablePlugins();
        this.plugins.clear();
        this.fileAssociations.clear();
        this.permissions.clear();
        this.defaultPerms.clear();
        this.defaultPermsOp.clear();
    }

    //todo Event Part
}
