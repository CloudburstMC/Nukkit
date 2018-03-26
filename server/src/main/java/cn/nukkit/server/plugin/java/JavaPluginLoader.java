package cn.nukkit.server.plugin.java;

import cn.nukkit.api.command.PluginCommand;
import cn.nukkit.api.event.plugin.PluginDisableEvent;
import cn.nukkit.api.event.plugin.PluginEnableEvent;
import cn.nukkit.api.plugin.*;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.console.TranslatableMessage;
import cn.nukkit.server.plugin.NukkitPluginDescription;
import cn.nukkit.server.plugin.PluginClassLoader;
import cn.nukkit.server.util.PluginException;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Log4j2
public class JavaPluginLoader implements PluginLoader {
    private static final String FILE_EXTENSION = ".jar";
    private final NukkitServer server;

    public JavaPluginLoader(NukkitServer server) {
        this.server = server;
    }

    @Nonnull
    @Override
    public NukkitPluginDescription loadPlugin(Path path) throws Exception {
        Optional<JavaPluginInformation> information = getPluginInformation(path.toFile());

        if (information.isPresent()) {
            return information.get().generateDescription(path);
        }

        throw new PluginException("Plugin Description was not found");
    }

    @Nonnull
    @Override
    public Plugin createPlugin(PluginDescription description) throws Exception {
        if (!(description instanceof NukkitJavaPluginDescription)) {
            throw new IllegalArgumentException("Description provided isn't of the Java plugin loader.");
        }

        Optional<Path> path = description.getPath();
        if (!path.isPresent()) {
            throw new IllegalArgumentException("No path in plugin description.");
        }

        return createPlugin(path.get(), (NukkitJavaPluginDescription) description);
    }

    @SuppressWarnings("unchecked")
    private Plugin createPlugin(Path path, NukkitJavaPluginDescription description) throws Exception {
        log.info(TranslatableMessage.of("nukkit.plugin.load", description.getName(), " v" + description.getVersion()));
        Path dataFolder = path.resolve(description.getName());
        if (!Files.isDirectory(dataFolder)) {
            throw new IllegalStateException("Projected dataFolder '" + dataFolder.toString() + "' for " + description.getName() + " exists and is not a directory");
        }
        PluginClassLoader loader = new PluginClassLoader(
                new URL[] { path.toUri().toURL() }
        );
        String className = description.getMainClass().replace('/', '.');
        int lastDot = className.lastIndexOf('.');
        String packageName = lastDot == -1 ? "" : className.substring(0, className.lastIndexOf('.'));

        Class clz = loader.loadClass(className);

        try {
            Class<? extends JavaPlugin> pluginClass = clz.asSubclass(JavaPlugin.class);

            JavaPlugin javaPlugin = pluginClass.newInstance();
            javaPlugin.initPlugin(this, server, description, dataFolder,
                    LoggerFactory.getLogger(description.getLoggerPrefix().orElse(description.getName())),
                    generateCommands(description.getCommandDescriptions(), javaPlugin));

            return javaPlugin;
        } catch (Exception e) {
            throw new InvalidPluginException("Unable to instantiate plugin class.");
        }
    }

    private Optional<JavaPluginInformation> getPluginInformation(File file) {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("nukkit.yml");
            if (entry == null) {
                entry = jar.getJarEntry("plugin.yml");
                if (entry == null) {
                    return Optional.empty();
                }
            }
            try (InputStream stream = jar.getInputStream(entry)) {
                return Optional.ofNullable(NukkitServer.YAML_MAPPER.readValue(stream, JavaPluginInformation.class));
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    private ImmutableCollection<PluginCommand> generateCommands(Map<String, CommandDescription> commandDescriptions, Plugin plugin) {
        List<PluginCommand> pluginCommands = new ArrayList<>();

        commandDescriptions.forEach((command, description) -> {
            if (command.contains(":")) {
                return;
            }
            /*NukkitPluginCommand newCommand = new NukkitPluginCommand<>(command, plugin);

            if (description.getDescription().isPresent()) {
                newCommand.setDescription(description.getDescription().get());
            }

            if (description.getPermission().isPresent()) {
                newCommand.setPermission(description.getPermission().get());
            }

            if (description.getPermissionMessage().isPresent()) {
                newCommand.setPermissionMessage(description.getPermissionMessage().get());
            }

            newCommand.setAliases(description.getAliases().toArray(new String[0]));
            pluginCommands.add(newCommand);*/
        });
        return ImmutableList.copyOf(pluginCommands);
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin instanceof JavaPlugin) {
            if (!plugin.isEnabled()) {
                ((JavaPlugin) plugin).setEnabled(true);

                this.server.getEventManager().fire(new PluginEnableEvent(plugin));
            }
        } else {
            throw new IllegalArgumentException("Plugin provided is not an instance of JavaPlugin");
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin instanceof JavaPlugin) {
            if (plugin.isEnabled()) {
                this.server.getServiceManager().cancel(plugin);

                this.server.getEventManager().fire(new PluginDisableEvent(plugin));
                ((JavaPlugin) plugin).setEnabled(false);
            }
        } else {
            throw new IllegalArgumentException("Plugin provided is not an instance of JavaPlugin");
        }
    }

    @Nonnull
    @Override
    public String getPluginFileExtension() {
        return FILE_EXTENSION;
    }
}
