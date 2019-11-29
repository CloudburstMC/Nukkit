package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.plugin.simple.Command;
import cn.nukkit.plugin.simple.EnableRegister;
import cn.nukkit.plugin.simple.Main;
import cn.nukkit.plugin.simple.Permission;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static cn.nukkit.permission.Permission.parsePermission;

/**
 * Created by Nukkit Team.
 */
public class JavaPluginLoader implements PluginLoader {

    private final Server server;

    private final Map<String, Class> classes = new HashMap<>();
    private final Map<String, PluginClassLoader> classLoaders = new HashMap<>();

    private final Map<File,Class> loadedSimplePlugin = new HashMap<>();

    private final Map<Class,List<Class>> simplePluginEnableClasses = new HashMap<>();

    public JavaPluginLoader(Server server) {
        this.server = server;
    }

    @Override
    public Plugin loadPlugin(File file) throws Exception {
        PluginDescription description = this.getPluginDescription(file);
        if (description != null) {
            this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.plugin.load", description.getFullName()));
            File dataFolder = new File(file.getParentFile(), description.getName());
            if (dataFolder.exists() && !dataFolder.isDirectory()) {
                throw new IllegalStateException("Projected dataFolder '" + dataFolder.toString() + "' for " + description.getName() + " exists and is not a directory");
            }

            String className = description.getMain();
            PluginClassLoader classLoader = new PluginClassLoader(this, this.getClass().getClassLoader(), file);
            this.classLoaders.put(description.getName(), classLoader);
            PluginBase plugin;
            try {
                Class javaClass = classLoader.loadClass(className);

                PluginAssert.isPluginBaseChild(javaClass,description.getMain());

                try {
                    Class<PluginBase> pluginClass = (Class<PluginBase>) javaClass.asSubclass(PluginBase.class);

                    plugin = pluginClass.newInstance();
                    this.initPlugin(plugin, description, dataFolder, file);

                    loadEnableRegister(new ArrayList<>(),plugin.getClass(),false,file,classLoader);
                    return plugin;
                } catch (ClassCastException e) {
                    throw new PluginException("Error whilst initializing main class `" + description.getMain() + "'", e);
                } catch (InstantiationException | IllegalAccessException e) {
                    Server.getInstance().getLogger().logException(e);
                }

            } catch (ClassNotFoundException e) {
                PluginAssert.findMainClass(description.getName());
            }
        }

        return null;
    }

    //simple load Plugin
    @Override
    public Plugin simpleLoadPlugin(File file) {
        try {
            Class<?> pluginClass = getSimplePlugin(file);
            PluginDescription pluginDescription = getSimpleDescription(pluginClass);
            this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.plugin.load", pluginDescription.getFullName()));
            File dataFolder = new File(file.getParentFile(), pluginDescription.getName());
            PluginBase plugin = (PluginBase) pluginClass.newInstance();
            this.initPlugin(plugin,pluginDescription,dataFolder,file);
            return plugin;
        }catch (InstantiationException | IllegalAccessException e){
            throw new PluginException(e.getMessage());
        }
        //do it
    }

    @Override
    public Plugin simpleLoadPlugin(String fileName) {
        return simpleLoadPlugin(new File(fileName));
    }

    private Class getSimplePlugin(File file){
        if(loadedSimplePlugin.containsKey(file)){
            return loadedSimplePlugin.get(file);
        }
        try(JarFile jarFile = new JarFile(file)){
            PluginClassLoader classLoader = new PluginClassLoader(this, this.getClass().getClassLoader(),file);
            Enumeration<JarEntry> entries = jarFile.entries();

            List<Class> haveLoaded = new ArrayList<>();
            Class<?> mainClass = null;
            while (entries.hasMoreElements()){
                String name = entries.nextElement().getName();
                if(name.endsWith(".class")) {
                    String mainName = getClassName(name);
                    Class<?> clz = classLoader.loadClass(mainName);
                    Main main = clz.getAnnotation(Main.class);
                    haveLoaded.add(clz);
                    if(main != null){
                        classLoaders.put(main.name(),classLoader);
                        loadedSimplePlugin.put(file,clz);
                        PluginAssert.isPluginBaseChild(clz,mainName);
                        mainClass = clz;
                    }
                }
            }


            if(mainClass!=null) {
                loadEnableRegister(haveLoaded,mainClass,true,file,classLoader);
                return mainClass;
            }
            PluginAssert.findMainClass("");
        }catch (IOException|ClassNotFoundException e){
            throw new PluginException(e.getMessage());
        }
        return null;
    }

    private void loadEnableRegister(List<Class> haveLoaded,Class<?> mainClass,boolean hasLoaded,File file,PluginClassLoader loader){
        boolean isEnableRegister;
        EnableRegister register;
        List<Class<? extends Listener>> noRegister = null;
        register = mainClass.getAnnotation(EnableRegister.class);
        isEnableRegister = register != null;
        List<Class> classes = new ArrayList<>();
        simplePluginEnableClasses.put(mainClass, classes);
        if (register != null) {
            noRegister = Arrays.asList(register.noRegister());
        }
        if (isEnableRegister) {
            if(!hasLoaded){
                try(JarFile jarFile = new JarFile(file)){
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.endsWith(".class")) {
                            haveLoaded.add(loader.loadClass(getClassName(name)));
                        }
                    }
                }catch (IOException|ClassNotFoundException e){
                    throw new PluginException(e.getMessage());
                }
            }
            for (Class<?> loaded : haveLoaded) {

                if (Arrays.asList(loaded.getInterfaces()).contains(Listener.class)) {
                    
                    if (!noRegister.contains(loaded)) {
                        simplePluginEnableClasses.get(mainClass).add(loaded);
                    }
                }

            }
        }


    }

    private String getClassName(String name){
        return name.substring(0, name.lastIndexOf(".")).replace("/", ".");
    }
    /**
     * the simple description for simple plugin system
     * @param plugin the main class
     * @return the description for simple plugin system
     */
    private PluginDescription getSimpleDescription(Class<?> plugin){
        Main main = plugin.getAnnotation(Main.class);
        if(main == null){
            return null;
        }
        String name = main.name();
        String version = main.version();
        String author = main.author();
        String description = main.description();
        PluginLoadOrder pluginLoadOrder = main.load();
        String website = main.website();
        String prefix = main.prefix();
        List<String> api = Arrays.asList(main.api());
        List<String> depend = Arrays.asList(main.depend());
        List<String> loadBefore = Arrays.asList(main.loadBefore());
        List<String> softDepend = Arrays.asList(main.softDepend());
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("version",version);
        hashMap.put("author",author);
        hashMap.put("api",api);
        hashMap.put("depend",depend);
        hashMap.put("loadBefore",loadBefore);
        hashMap.put("softDepend",softDepend);
        hashMap.put("description",description);
        hashMap.put("load",pluginLoadOrder.toString());
        hashMap.put("website",website);
        hashMap.put("prefix",prefix.equals("")?name:prefix);
        hashMap.put("main",plugin.getName());
        hashMap.put("isSimple",true);
        PluginDescription descript = new PluginDescription(hashMap);
        Permission[] permissions = main.permissions();
        hashMap.put("permissions",parsePermission(permissions));
        Command[] commands = main.commands();
        for(Command command:commands){
            descript.getCommands().put(command.name(),command);
        }
        return descript;
    }


    @Override
    public Plugin loadPlugin(String filename) throws Exception {
        return this.loadPlugin(new File(filename));
    }

    @Override
    public PluginDescription getPluginDescription(File file) {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("nukkit.yml");
            if (entry == null) {
                entry = jar.getJarEntry("plugin.yml");
                if (entry == null) {
                    Class clz = getSimplePlugin(file);
                    if(clz != null){
                        return getSimpleDescription(clz);
                    }
                    return null;
                }
            }
            try (InputStream stream = jar.getInputStream(entry)) {
                return new PluginDescription(Utils.readFile(stream));
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public PluginDescription getPluginDescription(String filename) {
        return this.getPluginDescription(new File(filename));
    }

    @Override
    public Pattern[] getPluginFilters() {
        return new Pattern[]{Pattern.compile("^.+\\.jar$")};
    }

    private void initPlugin(PluginBase plugin, PluginDescription description, File dataFolder, File file) {
        plugin.init(this, this.server, description, dataFolder, file);
        plugin.onLoad();
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && !plugin.isEnabled()) {
            this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.plugin.enable", plugin.getDescription().getFullName()));

            ((PluginBase) plugin).setEnabled(true);

            try {
                if (simplePluginEnableClasses.get(plugin.getClass())!=null) {
                    List<Class> listeners = simplePluginEnableClasses.get(plugin.getClass());
                    for (Class clz : listeners) {
                        server.getPluginManager().registerEvents((Listener) clz.newInstance(), plugin);
                    }
                }
            }catch (InstantiationException|IllegalAccessException e){
                throw new PluginException(e.getMessage());
            }

            this.server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && plugin.isEnabled()) {
            this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.plugin.disable", plugin.getDescription().getFullName()));

            this.server.getServiceManager().cancel(plugin);

            this.server.getPluginManager().callEvent(new PluginDisableEvent(plugin));

            ((PluginBase) plugin).setEnabled(false);
        }
    }

    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (PluginClassLoader loader : this.classLoaders.values()) {

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException e) {
                    //ignore
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    private void removeClass(String name) {
        Class<?> clazz = classes.remove(name);
    }
}
