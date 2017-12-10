package cn.nukkit.api.plugin;

import cn.nukkit.api.Server;
import cn.nukkit.api.command.Command;
import cn.nukkit.api.command.CommandExecutorSource;
import cn.nukkit.api.util.Config;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

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

    public final void initPlugin(PluginLoader loader, Server server, PluginDescription description, Path dataFolder,  Logger logger) {
        if (!initialized) {
            initialized = true;
            this.loader = loader;
            this.server = server;
            this.description = description;
            this.dataFolder = dataFolder;
            this.logger = logger;
            this.configPath = dataFolder.resolve("config.yml");
            this.config = server.getConfigBuilder().file(configPath.toFile()).build();
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

    public final Path getDataFolder() {
        return dataFolder;
    }

    public final Path getPath() {
        return description.getPath().get();
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
    public boolean onCommand(CommandExecutorSource executorSource, Command command, String label, String[] args) {
        return false;
    }



    @Override
    public void saveResource(String filename, String outputName, boolean replace) {
        Preconditions.checkArgument(filename != null && outputName != null, "Filename can not be null!");
        Preconditions.checkArgument(filename.trim().length() != 0 && outputName.trim().length() != 0, "Filename can not be empty!");

        File out = new File(dataFolder, outputName);
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
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        } else {
            logger.warn("Could not save " + out.getName() + " to " + out + " because " + out.getName() + " already exists.");
        }
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
