package cn.nukkit.api.plugin;

import cn.nukkit.api.Server;
import cn.nukkit.api.command.Command;
import cn.nukkit.api.command.PluginCommand;
import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.util.Config;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

public abstract class JavaPlugin implements Plugin {
    private PluginLoader loader;
    private Server server;
    private boolean enabled = false;
    private boolean initialized = false;
    private PluginDescription description;
    private Path dataFolder;
    private Config config;
    private Path configPath;
    private Logger logger;
    private Collection<PluginCommand> pluginCommands;

    public final void initPlugin(PluginLoader loader, Server server, PluginDescription description, Path dataFolder, Logger logger, Collection<PluginCommand> pluginCommands) {
        if (!initialized) {
            initialized = true;
            this.loader = loader;
            this.server = server;
            this.description = description;
            this.dataFolder = dataFolder;
            this.logger = logger;
            this.configPath = dataFolder.resolve("config.yml");
            this.config = server.createConfigBuilder().file(configPath.toFile()).build();
            this.pluginCommands = pluginCommands;
        }
    }

    public final PluginLoader getPluginLoader() {
        return loader;
    }

    public final Server getServer() {
        return server;
    }

    public final PluginDescription getPluginDescription() {
        return description;
    }

    public final String getVersion() {
        return description.getVersion();
    }

    public final String getName() {
        return description.getName();
    }

    public final Path getDataFolder() {
        return dataFolder;
    }

    public final Path getConfigPath() {
        return configPath;
    }

    public final Config getConfig() {
        return config;
    }

    public final Logger getLogger() {
        return logger;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public final boolean isDisabled() {
        return !enabled;
    }

    public void onLoad() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public final boolean isInitialized() {
        return initialized;
    }

    @Override
    public InputStream getResource(String filename) {
        return this.getClass().getClassLoader().getResourceAsStream(filename);
    }

    @Override
    public boolean saveResource(String filename) {
        return saveResource(filename, false);
    }

    @Override
    public boolean saveResource(String filename, boolean replace) {
        return saveResource(filename, filename, replace);
    }

    @Override
    public boolean onCommand(CommandSender executorSource, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public boolean saveResource(String filename, String outputName, boolean replace) {
        Preconditions.checkArgument(filename != null && outputName != null, "Filename can not be null!");
        Preconditions.checkArgument(filename.trim().length() != 0 && outputName.trim().length() != 0, "Filename can not be empty!");

        File out = new File(dataFolder.toFile(), outputName);
        File outDir = out.getParentFile();
        InputStream resource = getResource(filename);
        if (resource == null) {
            throw new IllegalArgumentException("The embedded file \"" + filename + "\" cannot be found in classloader.");
        }

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (!out.exists() || replace) {
            try (OutputStream outStream = new FileOutputStream(out)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = resource.read(buf)) > 0) {
                    outStream.write(buf, 0, len);
                }
                outStream.close();
                resource.close();
                return true;
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        } else {
            logger.warn("Could not save " + out.getName() + " to " + out + " because " + out.getName() + " already exists.");
        }
        return false;
    }

    @Override
    public String toString() {
        return description.getName() + "{" +
                "version='" + description.getVersion() + '\'' +
                ", authors='" + Arrays.toString(description.getAuthors().toArray()) + '\'' +
                ", apiVersions='" + Arrays.toString(description.getApiVersions().toArray()) + '\'' +
                ", dependencies='" + Arrays.toString(description.getDependencies().toArray()) + '\'' +
                ", softDependencies='" + Arrays.toString(description.getSoftDependencies().toArray()) + "'}" ;
    }
}
