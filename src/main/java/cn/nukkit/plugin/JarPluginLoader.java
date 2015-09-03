package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.utils.PluginException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

/**
 * Created by Nukkit Team.
 */
public class JarPluginLoader implements PluginLoader {

    private Server server;

    public JarPluginLoader(Server server) {
        this.server = server;
    }

    private class JarClassLoader extends URLClassLoader {

        public JarClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public void addURL(URL url) {
            super.addURL(url);
        }
    }

    @Override
    public Plugin loadPlugin(String file) throws IllegalAccessException, MalformedURLException {
        if (!file.endsWith(".jar")) {
            throw new IllegalAccessException("Plugin file must end with .jar not " + file);
        }
        PluginDescription pluginDescription = null;
        if ((pluginDescription = getPluginDescription(file)) != null) {
            server.getLogger().info(server.getLanguage().translateString("nukkit.plugin.load", pluginDescription.getFullName()));
            File f = new File(file);
            if (f.getParentFile().exists() && !f.getParentFile().isDirectory()) {
                throw new IllegalAccessException("Plugin " + pluginDescription.getName() + "data folder exists and it's not a directory!");
            }

            URL[] url = new URL[]{};
            JarClassLoader classLoader = new JarClassLoader(url, null);

            classLoader.addURL(f.toURI().toURL());
            Class<?> claz;
            try {
                claz = classLoader.loadClass(pluginDescription.getMain());
                initPlugin(claz, pluginDescription, f.getParent(), f);
            } catch (ClassNotFoundException e) {
                throw new PluginException("Cound not load plugin " + pluginDescription.getName() + ": Main class can not found!");
            }

            if (!PluginBase.class.isAssignableFrom(claz)) {
                throw new PluginException("Cound not load plugin " + pluginDescription.getName() + ": Main class is not assignable from PluginBase!");
            }
        }
        return null;
    }

    private void initPlugin(Class<?> claz, PluginDescription pluginDescription, String parent, File f) {

    }

    @Override
    public PluginDescription getPluginDescription(String file) {
        return null;
    }

    @Override
    public Pattern[] getPluginFilters() {
        return new Pattern[]{Pattern.compile("\\.jar$")};
    }

    @Override
    public void enablePlugin(Plugin plugin) {

    }

    @Override
    public void disablePlugin(Plugin plugin) {

    }

}
