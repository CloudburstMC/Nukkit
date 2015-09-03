package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class PluginBase implements Plugin {

    private PluginLoader loader;

    private Server server;

    private boolean isEnabled = false;

    private boolean initialized = false;

    private PluginDescription description;

    private File dataFolder;
    private Config config;
    private File configFile;
    private File file;
    private PluginLogger logger;


    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public final boolean isEnabled() {
        return isEnabled;
    }

    public final void setEnabled() {
        this.setEnabled(true);
    }

    public final void setEnabled(boolean value) {
        if (isEnabled != value) {
            isEnabled = value;
            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public final boolean isDisabled() {
        return !isEnabled;
    }

    public final File getDataFolder() {
        return dataFolder;
    }

    public final PluginDescription getDescription() {
        return description;
    }

    public final void init(PluginLoader loader, Server server, PluginDescription description, File dataFolder, File file) {
        if (!initialized) {
            initialized = true;
            this.loader = loader;
            this.server = server;
            this.description = description;
            this.dataFolder = dataFolder;
            this.file = file;
            this.configFile = new File(this.dataFolder, "config.yml");
            this.logger = new PluginLogger(this);
        }
    }

    public PluginLogger getLogger() {
        return logger;
    }

    public final boolean isInitialized() {
        return initialized;
    }

    public PluginIdentifiableCommand getCommand(String name) {
        PluginIdentifiableCommand command = this.getServer().getPluginCommand(name);
        if (command == null || !command.getPlugin().equals(this)) {
            command = this.getServer().getPluginCommand(this.description.getName().toLowerCase() + ":" + name);
        }

        if (command != null && command.getPlugin().equals(this)) {
            return command;
        } else {
            return null;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public InputStream getResource(String filename) {
        return this.loader.getClass().getClassLoader().getResourceAsStream("resources/" + filename);
    }

    @Override
    public boolean saveResource(String filename) {
        return this.saveResource(filename, false);
    }

    @Override
    public boolean saveResource(String filename, boolean replace) {
        if (filename.trim().equals("")) {
            return false;
        }

        InputStream resource = this.getResource(filename);
        if (resource == null) {
            return false;
        }

        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }

        File out = new File(this.dataFolder, filename);
        if (out.exists() && !replace) {
            return false;
        }
        try {
            Utils.writeFile(out, resource);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public Config getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }
        return this.config;
    }

    @Override
    public void saveConfig() {
        if (!this.getConfig().save()) {
            this.getLogger().critical("Could not save config to " + this.configFile.toString());
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.saveResource("config.yml", false);
        }
    }

    @Override
    public void reloadConfig() {
        this.config = new Config(this.configFile);
        InputStream configStream = this.getResource("config.yml");
        if (configStream != null) {
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(dumperOptions);
            try {
                this.config.setDefault(yaml.loadAs(Utils.readFile(this.configFile), LinkedHashMap.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public String getName() {
        return this.description.getName();
    }

    public final String getFullName() {
        return this.description.getFullName();
    }

    protected File getFile() {
        return file;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.loader;
    }
}
