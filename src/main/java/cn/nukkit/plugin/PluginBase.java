package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * 一般的Nukkit插件需要继承的类。<br>
 * A class to be extended by a normal Nukkit plugin.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.plugin.PluginDescription
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Log4j2
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

    /**
     * 加载这个插件。<br>
     * Enables this plugin.
     * <p>
     * <p>如果你需要卸载这个插件，建议使用{@link #setEnabled(boolean)}<br>
     * If you need to disable this plugin, it's recommended to use {@link #setEnabled(boolean)}</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final void setEnabled() {
        this.setEnabled(true);
    }

    /**
     * 加载或卸载这个插件。<br>
     * Enables or disables this plugin.
     * <p>
     * <p>插件管理器插件常常使用这个方法。<br>
     * It's normally used by a plugin manager plugin to manage plugins.</p>
     *
     * @param value {@code true}为加载，{@code false}为卸载。<br>{@code true} for enable, {@code false} for disable.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    @PowerNukkitDifference(info = "Made impossible to disable special the PowerNukkitPlugin", since = "1.3.0.0-PN")
    public final void setEnabled(boolean value) {
        if (isEnabled != value) {
            if (!value && PowerNukkitPlugin.getInstance() == this) {
                throw new UnsupportedOperationException("The PowerNukkitPlugin cannot be disabled");
            }
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

    /**
     * 初始化这个插件。<br>
     * Initialize the plugin.
     * <p>
     * <p>这个方法会在加载(load)之前被插件加载器调用，初始化关于插件的一些事项，不能被重写。<br>
     * Called by plugin loader before load, and initialize the plugin. Can't be overridden.</p>
     *
     * @param loader      加载这个插件的插件加载器的{@code PluginLoader}对象。<br>
     *                    The plugin loader ,which loads this plugin, as a {@code PluginLoader} object.
     * @param server      运行这个插件的服务器的{@code Server}对象。<br>
     *                    The server running this plugin, as a {@code Server} object.
     * @param description 描述这个插件的{@code PluginDescription}对象。<br>
     *                    A {@code PluginDescription} object that describes this plugin.
     * @param dataFolder  这个插件的数据的文件夹。<br>
     *                    The data folder of this plugin.
     * @param file        这个插件的文件{@code File}对象。对于jar格式的插件，就是jar文件本身。<br>
     *                    The {@code File} object of this plugin itself. For jar-packed plugins, it is the jar file itself.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
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

    /**
     * 返回这个插件是否已经初始化。<br>
     * Returns if this plugin is initialized.
     *
     * @return 这个插件是否已初始化。<br>if this plugin is initialized.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * TODO: FINISH JAVADOC
     */
    @Nullable
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
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public PluginCommand<?> getPluginCommand(@Nonnull String name) {
        PluginIdentifiableCommand command = getCommand(name);
        if (command instanceof PluginCommand<?>) {
            return (PluginCommand<?>) command;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
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
    public boolean saveResource(String filename, String outputName, boolean replace) {
        Preconditions.checkArgument(filename != null && outputName != null, "Filename can not be null!");
        Preconditions.checkArgument(filename.trim().length() != 0 && outputName.trim().length() != 0, "Filename can not be empty!");

        File out = new File(dataFolder, outputName);
        if (!out.exists() || replace) {
            try (InputStream resource = getResource(filename)) {
                if (resource != null) {
                    File outFolder = out.getParentFile();
                    if (!outFolder.exists()) {
                        outFolder.mkdirs();
                    }
                    Utils.writeFile(out, resource);

                    return true;
                }
            } catch (IOException e) {
                log.error("Error while saving resource {}, to {} (replace: {}, plugin:{})", filename, outputName, replace, getDescription().getName(), e);
            }
        }
        return false;
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
                log.error("Error while reloading configs for the plugin {}", getDescription().getName(), e);
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

    /**
     * 返回这个插件完整的名字。<br>
     * Returns the full name of this plugin.
     * <p>
     * <p>一个插件完整的名字由{@code 名字+" v"+版本号}组成。比如：<br>
     * A full name of a plugin is composed by {@code name+" v"+version}.for example:</p>
     * <p>{@code HelloWorld v1.0.0}</p>
     *
     * @return 这个插件完整的名字。<br>The full name of this plugin.
     * @see cn.nukkit.plugin.PluginDescription#getFullName
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final String getFullName() {
        return this.description.getFullName();
    }

    /**
     * 返回这个插件的文件{@code File}对象。对于jar格式的插件，就是jar文件本身。<br>
     * Returns the {@code File} object of this plugin itself. For jar-packed plugins, it is the jar file itself.
     *
     * @return 这个插件的文件 {@code File}对象。<br>The {@code File} object of this plugin itself.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    protected File getFile() {
        return file;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.loader;
    }
}
