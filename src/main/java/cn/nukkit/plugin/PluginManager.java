package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.event.*;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import cn.nukkit.registry.CommandRegistry;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author MagicDroidX
 */
@Log4j2
public class PluginManager {

    private final Server server;

    protected final Map<String, Plugin> plugins = new LinkedHashMap<>();

    protected final Map<String, Permission> permissions = new HashMap<>();

    protected final Map<String, Permission> defaultPerms = new HashMap<>();

    protected final Map<String, Permission> defaultPermsOp = new HashMap<>();

    protected final Map<String, Set<Permissible>> permSubs = new HashMap<>();

    protected final Set<Permissible> defSubs = Collections.newSetFromMap(new WeakHashMap<>());

    protected final Set<Permissible> defSubsOp = Collections.newSetFromMap(new WeakHashMap<>());

    protected final Map<String, PluginLoader> fileAssociations = new HashMap<>();

    public PluginManager(Server server) {
        this.server = server;
    }

    public Plugin getPlugin(String name) {
        if (this.plugins.containsKey(name)) {
            return this.plugins.get(name);
        }
        return null;
    }

    public boolean registerInterface(Class<? extends PluginLoader> loaderClass) {
        if (loaderClass != null) {
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

                                this.parseYamlCommands(plugin);
                                return plugin;
                            }
                        } catch (Exception e) {
                            log.error("Could not load plugin", e);
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
        return this.loadPlugins(dictionary, newLoaders, false);
    }

    public Map<String, Plugin> loadPlugins(File dictionary, List<String> newLoaders, boolean includeDir) {
        if (dictionary.isDirectory()) {
            Map<String, File> plugins = new LinkedHashMap<>();
            Map<String, Plugin> loadedPlugins = new LinkedHashMap<>();
            Map<String, List<String>> dependencies = new LinkedHashMap<>();
            Map<String, List<String>> softDependencies = new LinkedHashMap<>();
            Map<String, PluginLoader> loaders = new LinkedHashMap<>();
            if (newLoaders != null) {
                for (String key : newLoaders) {
                    if (this.fileAssociations.containsKey(key)) {
                        loaders.put(key, this.fileAssociations.get(key));
                    }
                }
            } else {
                loaders = this.fileAssociations;
            }

            for (final PluginLoader loader : loaders.values()) {
                for (File file : dictionary.listFiles((dir, name) -> {
                    for (Pattern pattern : loader.getPluginFilters()) {
                        if (pattern.matcher(name).matches()) {
                            return true;
                        }
                    }
                    return false;
                })) {
                    if (file.isDirectory() && !includeDir) {
                        continue;
                    }
                    try {
                        PluginDescription description = loader.getPluginDescription(file);
                        if (description != null) {
                            String name = description.getName();

                            if (plugins.containsKey(name) || this.getPlugin(name) != null) {
                                log.error(this.server.getLanguage().translate("nukkit.plugin.duplicateError", name));
                                continue;
                            }

                            boolean compatible = false;

                            for (String version : description.getCompatibleAPIs()) {

                                try {
                                    //Check the format: majorVersion.minorVersion.patch
                                    if (!Pattern.matches("[0-9]\\.[0-9]\\.[0-9]", version)) {
                                        throw new IllegalArgumentException();
                                    }
                                } catch (NullPointerException | IllegalArgumentException e) {
                                    log.error(this.server.getLanguage().translate("nukkit.plugin.loadError", name, "Wrong API format"));
                                    continue;
                                }

                                String[] versionArray = version.split("\\.");
                                String[] apiVersion = this.server.getApiVersion().split("\\.");

                                //Completely different API version
                                if (!Objects.equals(Integer.valueOf(versionArray[0]), Integer.valueOf(apiVersion[0]))) {
                                    continue;
                                }

                                //If the plugin requires new API features, being backwards compatible
                                if (Integer.parseInt(versionArray[1]) > Integer.parseInt(apiVersion[1])) {
                                    continue;
                                }

                                compatible = true;
                                break;
                            }

                            if (!compatible) {
                                log.error(this.server.getLanguage().translate("nukkit.plugin.loadError", name, "%nukkit.plugin.incompatibleAPI"));
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
                        log.error(this.server.getLanguage().translate("nukkit.plugin.fileError",
                                file.getName(), dictionary.toString(), Utils.getExceptionMessage(e)));
                    }
                }
            }

            while (!plugins.isEmpty()) {
                boolean missingDependency = true;
                for (String name : new ArrayList<>(plugins.keySet())) {
                    File file = plugins.get(name);
                    if (dependencies.containsKey(name)) {
                        for (String dependency : new ArrayList<>(dependencies.get(name))) {
                            if (loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null) {
                                dependencies.get(name).remove(dependency);
                            } else if (!plugins.containsKey(dependency)) {
                                log.error(this.server.getLanguage().translate("nukkit.plugin.loadError",
                                        name, "%nukkit.plugin.unknownDependency"));
                                break;
                            }
                        }

                        if (dependencies.get(name).isEmpty()) {
                            dependencies.remove(name);
                        }
                    }

                    if (softDependencies.containsKey(name)) {
                        softDependencies.get(name).removeIf(dependency ->
                                loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null);

                        if (softDependencies.get(name).isEmpty()) {
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
                            log.error(this.server.getLanguage().translate("nukkit.plugin.genericLoadError",
                                    name));
                        }
                    }
                }

                if (missingDependency) {
                    for (String name : new ArrayList<>(plugins.keySet())) {
                        File file = plugins.get(name);
                        if (!dependencies.containsKey(name)) {
                            softDependencies.remove(name);
                            plugins.remove(name);
                            missingDependency = false;
                            Plugin plugin = this.loadPlugin(file, loaders);
                            if (plugin != null) {
                                loadedPlugins.put(name, plugin);
                            } else {
                                log.error(this.server.getLanguage().translate("nukkit.plugin.genericLoadError",
                                        name));
                            }
                        }
                    }

                    if (missingDependency) {
                        for (String name : plugins.keySet()) {
                            log.error(this.server.getLanguage().translate("nukkit.plugin.loadError",
                                    name, "%nukkit.plugin.circularDependency"));
                        }
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
        try (Timing ignored = Timings.permissionDefaultTimer.startTiming()) {
            if (permission.getDefault().equals(Permission.DEFAULT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
                this.defaultPermsOp.put(permission.getName(), permission);
                this.dirtyPermissibles(true);
            }

            if (permission.getDefault().equals(Permission.DEFAULT_NOT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
                this.defaultPerms.put(permission.getName(), permission);
                this.dirtyPermissibles(false);
            }
        }
    }

    private void dirtyPermissibles(boolean op) {
        for (Permissible p : this.getDefaultPermSubscriptions(op)) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(String permission, Permissible permissible) {
        if (!this.permSubs.containsKey(permission)) {
            this.permSubs.put(permission, Collections.newSetFromMap(new WeakHashMap<>()));
        }
        this.permSubs.get(permission).add(permissible);
    }

    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        if (this.permSubs.containsKey(permission)) {
            this.permSubs.get(permission).remove(permissible);
            if (this.permSubs.get(permission).size() == 0) {
                this.permSubs.remove(permission);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(String permission) {
        if (this.permSubs.containsKey(permission)) {
            return new HashSet<>(this.permSubs.get(permission));
        }
        return new HashSet<>();
    }

    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.add(permissible);
        } else {
            this.defSubs.add(permissible);
        }
    }

    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.remove(permissible);
        } else {
            this.defSubs.remove(permissible);
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        if (op) {
            return new HashSet<>(this.defSubsOp);
        } else {
            return new HashSet<>(this.defSubs);
        }
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
            } catch (Throwable e) {
                log.fatal("Unable to load plugins", e);
                this.disablePlugin(plugin);
            }
        }
    }

    protected void parseYamlCommands(Plugin plugin) {
        CommandRegistry registry = CommandRegistry.get();

        for (Map.Entry<String, Object> entry : plugin.getDescription().getCommands().entrySet()) {
            String key = entry.getKey();
            Object data = entry.getValue();
            if (key.contains(":")) {
                log.error(this.server.getLanguage().translate("nukkit.plugin.commandError",
                        key, plugin.getDescription().getFullName()));
                continue;
            }
            if (data instanceof Map) {
                String desc = "";
                String usage = "";
                String[] aliases = new String[0];
                String perms = "";
                String permsMsg = "";


                if (((Map) data).containsKey("description")) {
                    desc = (String) ((Map) data).get("description");
                }

                if (((Map) data).containsKey("usage")) {
                    usage = (String) ((Map) data).get("usage");
                }

                if (((Map) data).containsKey("aliases")) {
                    Object configAliases = ((Map) data).get("aliases");
                    if (configAliases instanceof List) {
                        List<String> aliasList = new ArrayList<>();
                        for (String alias : (List<String>) configAliases) {
                            if (alias.contains(":")) {
                                log.error(this.server.getLanguage().translate("nukkit.plugin.aliasError",
                                        alias, plugin.getDescription().getFullName()));
                                continue;
                            }
                            aliasList.add(alias);
                        }

                        aliases = aliasList.toArray(new String[0]);
                    }
                }

                if (((Map) data).containsKey("permission")) {
                    perms = (String) ((Map) data).get("permission");
                }

                if (((Map) data).containsKey("permission-message")) {
                    permsMsg = (String) ((Map) data).get("permission-message");
                }

                registry.register(plugin, new PluginCommand(plugin, CommandData.builder(key)
                        .setDescription(desc)
                        .setUsageMessage(usage)
                        .setAliases(aliases)
                        .setPermissions(perms.split(";"))
                        .setPermissionMessage(permsMsg).build()
                ));
            }
        }
    }

    public void disablePlugins() {
        ListIterator<Plugin> plugins = new ArrayList<>(this.getPlugins().values()).listIterator(this.getPlugins().size());

        while (plugins.hasPrevious()) {
            this.disablePlugin(plugins.previous());
        }
    }

    public void disablePlugin(Plugin plugin) {
        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Exception e) {
                log.error("Unable to disable plugin", e);
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

    public void callEvent(Event event) {
        try {
            for (RegisteredListener registration : getEventListeners(event.getClass()).getRegisteredListeners()) {
                if (!registration.getPlugin().isEnabled()) {
                    continue;
                }

                try {
                    registration.callEvent(event);
                } catch (Exception e) {
                    log.error(this.server.getLanguage().translate("nukkit.plugin.eventError",
                            event.getEventName(), registration.getPlugin().getDescription().getFullName(),
                            e.getMessage(), registration.getListener().getClass().getName()));
                    log.throwing(Level.ERROR, e);
                }
            }
        } catch (IllegalAccessException e) {
            log.throwing(Level.ERROR, e);
        }
    }

    public void registerEvents(Listener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + listener.getClass().getName() + " while not enabled");
        }

        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();
        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            Method[] privateMethods = listener.getClass().getDeclaredMethods();
            methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);
            Collections.addAll(methods, publicMethods);
            Collections.addAll(methods, privateMethods);
        } catch (NoClassDefFoundError e) {
            plugin.getLogger().error("Plugin " + plugin.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> checkClass;

            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                plugin.getLogger().error(plugin.getDescription().getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }

            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);

            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    if (Boolean.parseBoolean(String.valueOf(this.server.getConfig(
                            "settings.deprecated-verbose", true)))) {
                        log.warn(this.server.getLanguage().translate("nukkit.plugin.deprecatedEvent",
                                plugin.getName(), clazz.getName(), listener.getClass().getName() + "." + method.getName() + "()"));
                    }
                    break;
                }
            }
            this.registerEvent(eventClass, listener, eh.priority(), new MethodEventExecutor(method), plugin, eh.ignoreCancelled());
        }
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin) throws PluginException {
        this.registerEvent(event, listener, priority, executor, plugin, false);
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled) throws PluginException {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + event + " while not enabled");
        }

        try {
            Timing timing = Timings.getPluginEventTiming(event, listener, executor, plugin);
            this.getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled, timing));
        } catch (IllegalAccessException e) {
            log.error("Unable to register event", e);
        }
    }

    private HandlerList getEventListeners(Class<? extends Event> type) throws IllegalAccessException {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlers");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("getHandlers method in " + type.getName() + " was not static!");
        } catch (Exception e) {
            throw new IllegalAccessException(Utils.getExceptionMessage(e));
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) throws IllegalAccessException {
        try {
            clazz.getDeclaredMethod("getHandlers");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlers method required!");
            }
        }
    }
}
