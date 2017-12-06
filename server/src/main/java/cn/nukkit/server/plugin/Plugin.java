package cn.nukkit.server.plugin;

import cn.nukkit.api.command.CommandExecutor;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.utils.Config;

import java.io.File;
import java.io.InputStream;

/**
 * 所有Nukkit插件必须实现的接口。<br>
 * An interface what must be implemented by all Nukkit plugins.
 * <p>
 * <p>对于插件作者，我们建议让插件主类继承{@link cn.nukkit.plugin.PluginBase}类，而不是实现这个接口。<br>
 * For plugin developers: it's recommended to use {@link cn.nukkit.plugin.PluginBase} for an actual plugin
 * instead of implement this interface.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.plugin.PluginBase
 * @see cn.nukkit.plugin.PluginDescription
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface Plugin extends CommandExecutor {
    /**
     * 在一个Nukkit插件被加载时调用的方法。这个方法会在{@link Plugin#onEnable()}之前调用。<br>
     * Called when a Nukkit plugin is loaded, before {@link Plugin#onEnable()} .
     * <p>
     * <p>应该填写加载插件时需要作出的动作。例如：初始化数组、初始化数据库连接。<br>
     * Use this to init a Nukkit plugin, such as init arrays or init database connections.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onLoad();

    /**
     * 在一个Nukkit插件被启用时调用的方法。<br>
     * Called when a Nukkit plugin is enabled.
     * <p>
     * <p>应该填写插件启用时需要作出的动作。例如：读取配置文件、读取资源、连接数据库。<br>
     * Use this to open config files, open resources, connect databases.</p>
     * <p>
     * <p>注意到可能存在的插件管理器插件，这个方法在插件多次重启时可能被调用多次。<br>
     * Notes that there may be plugin manager plugins,
     * this method can be called many times when a plugin is restarted many times.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onEnable();

    /**
     * 返回这个Nukkit插件是否已启用。<br>
     * Whether this Nukkit plugin is enabled.
     *
     * @return 这个插件是否已经启用。<br>Whether this plugin is enabled.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isEnabled();

    /**
     * 在一个Nukkit插件被停用时调用的方法。<br>
     * Called when a Nukkit plugin is disabled.
     * <p>
     * <p>应该填写插件停用时需要作出的动作。例如：关闭数据库，断开资源。<br>
     * Use this to free open things and finish actions,
     * such as disconnecting databases and close resources.</p>
     * <p>
     * <p>注意到可能存在的插件管理器插件，这个方法在插件多次重启时可能被调用多次。<br>
     * Notes that there may be plugin manager plugins,
     * this method can be called many times when a plugin is restarted many times.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onDisable();

    /**
     * 返回这个Nukkit插件是否已停用。<br>
     * Whether this Nukkit plugin is disabled.
     *
     * @return 这个插件是否已经停用。<br>Whether this plugin is disabled.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isDisabled();

    /**
     * 返回这个Nukkit插件的数据文件夹。<br>
     * The data folder of this Nukkit plugin.
     * <p>
     * <p>一般情况下，数据文件夹名字与插件名字相同，而且都放在nukkit安装目录下的plugins文件夹里。<br>
     * Under normal circumstances, the data folder has the same name with the plugin,
     * and is placed in the 'plugins' folder inside the nukkit installation directory.</p>
     *
     * @return 这个插件的数据文件夹。<br>The data folder of this plugin.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    File getDataFolder();

    /**
     * 返回描述这个Nukkit插件的{@link PluginDescription}对象。<br>
     * The description this Nukkit plugin as a {@link PluginDescription} object.
     * <p>
     * <p>对于jar格式的Nukkit插件，插件的描述在plugin.yml文件内定义。<br>
     * For jar-packed Nukkit plugins, the description is defined in the 'plugin.yml' file.</p>
     *
     * @return 这个插件的描述。<br>A description of this plugin.
     * @see cn.nukkit.plugin.PluginDescription
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginDescription getDescription();

    /**
     * 读取这个插件特定的资源文件，并返回为{@code InputStream}对象。<br>
     * Reads a resource of this plugin, and returns as an {@code InputStream} object.
     * <p>
     * <p>对于jar格式的Nukkit插件，Nukkit会在jar包内的资源文件夹(一般为resources文件夹)寻找资源文件。<br>
     * For jar-packed Nukkit plugins, Nukkit will look for your resource file in the resources folder,
     * which is normally named 'resources' and placed in plugin jar file.</p>
     * <p>
     * <p>当你需要把一个文件的所有内容读取为字符串，可以使用{@link cn.nukkit.utils.Utils#readFile}函数，
     * 来从{@code InputStream}读取所有内容为字符串。例如：<br>
     * When you need to read the whole file content as a String, you can use {@link cn.nukkit.utils.Utils#readFile}
     * to read from a {@code InputStream} and get whole content as a String. For example:</p>
     * <p><code>String string = Utils.readFile(this.getResource("string.txt"));</code></p>
     *
     * @param filename 要读取的资源文件名字。<br>The name of the resource file to read.
     * @return 读取的资源文件的 {@code InputStream}对象。若错误会返回{@code null}<br>
     * The resource as an {@code InputStream} object, or {@code null} when an error occurred.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    InputStream getResource(String filename);

    /**
     * 保存这个Nukkit插件的资源。<br>
     * Saves the resource of this plugin.
     * <p>
     * <p>对于jar格式的Nukkit插件，Nukkit会在jar包内的资源文件夹寻找资源文件，然后保存到数据文件夹。<br>
     * For jar-packed Nukkit plugins, Nukkit will look for your resource file in the resources folder,
     * which is normally named 'resources' and placed in plugin jar file, and copy it into data folder.</p>
     * <p>
     * <p>这个函数通常用来在插件被加载(load)时，保存默认的资源文件。这样插件在启用(enable)时不会错误读取空的资源文件，
     * 用户也无需从开发者处手动下载资源文件后再使用插件。<br>
     * This is usually used to save the default plugin resource when the plugin is LOADED .If this is used,
     * it won't happen to load an empty resource when plugin is ENABLED, and plugin users are not required to get
     * default resources from the developer and place it manually. </p>
     * <p>
     * <p>如果需要替换已存在的资源文件，建议使用{@link cn.nukkit.plugin.Plugin#saveResource(String, boolean)}<br>
     * If you need to REPLACE an existing resource file, it's recommended
     * to use {@link cn.nukkit.plugin.Plugin#saveResource(String, boolean)}.</p>
     *
     * @param filename 要保存的资源文件名字。<br>The name of the resource file to save.
     * @return 保存是否成功。<br>true if the saving action is successful.
     * @see cn.nukkit.plugin.Plugin#saveDefaultConfig
     * @see cn.nukkit.plugin.Plugin#saveResource(String, boolean)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean saveResource(String filename);

    /**
     * 保存或替换这个Nukkit插件的资源。<br>
     * Saves or replaces the resource of this plugin.
     * <p>
     * <p>对于jar格式的Nukkit插件，Nukkit会在jar包内的资源文件夹寻找资源文件，然后保存到数据文件夹。<br>
     * For jar-packed Nukkit plugins, Nukkit will look for your resource file in the resources folder,
     * which is normally named 'resources' and placed in plugin jar file, and copy it into data folder.</p>
     * <p>
     * <p>如果需要保存默认的资源文件，建议使用{@link cn.nukkit.plugin.Plugin#saveResource(String)}<br>
     * If you need to SAVE DEFAULT resource file, it's recommended
     * to use {@link cn.nukkit.plugin.Plugin#saveResource(String)}.</p>
     *
     * @param filename 要保存的资源文件名字。<br>The name of the resource file to save.
     * @param replace  是否替换目标文件。<br>if true, Nukkit will replace the target resource file.
     * @return 保存是否成功。<br>true if the saving action is successful.
     * @see cn.nukkit.plugin.Plugin#saveResource(String)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */

    boolean saveResource(String filename, boolean replace);

    boolean saveResource(String filename, String outputName, boolean replace);

    /**
     * 返回这个Nukkit插件配置文件的{@link cn.nukkit.utils.Config}对象。<br>
     * The config file this Nukkit plugin as a {@link cn.nukkit.utils.Config} object.
     * <p>
     * <p>一般地，插件的配置保存在数据文件夹下的config.yml文件。<br>
     * Normally, the plugin config is saved in the 'config.yml' file in its data folder.</p>
     *
     * @return 插件的配置文件。<br>The configuration of this plugin.
     * @see cn.nukkit.plugin.Plugin#getDataFolder
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Config getConfig();

    /**
     * 保存这个Nukkit插件的配置文件。<br>
     * Saves the plugin config.
     *
     * @see cn.nukkit.plugin.Plugin#getDataFolder
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void saveConfig();

    /**
     * 保存这个Nukkit插件的默认配置文件。<br>
     * Saves the DEFAULT plugin config.
     * <p>
     * <p>执行这个函数时，Nukkit会在资源文件夹内寻找开发者配置好的默认配置文件config.yml，然后保存在数据文件夹。
     * 如果数据文件夹已经有一个config.yml文件，Nukkit不会替换这个文件。<br>
     * When this is used, Nukkit will look for the default 'config.yml' file which is configured by plugin developer
     * and save it to the data folder. If a config.yml file exists in the data folder, Nukkit won't replace it.</p>
     * <p>
     * <p>这个函数通常用来在插件被加载(load)时，保存默认的配置文件。这样插件在启用(enable)时不会错误读取空的配置文件，
     * 用户也无需从开发者处手动下载配置文件保存后再使用插件。<br>
     * This is usually used to save the default plugin config when the plugin is LOADED .If this is used,
     * it won't happen to load an empty config when plugin is ENABLED, and plugin users are not required to get
     * default config from the developer and place it manually. </p>
     *
     * @see cn.nukkit.plugin.Plugin#getDataFolder
     * @see cn.nukkit.plugin.Plugin#saveResource
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void saveDefaultConfig();

    /**
     * 重新读取这个Nukkit插件的默认配置文件。<br>
     * Reloads the plugin config.
     * <p>
     * <p>执行这个函数时，Nukkit会从数据文件夹中的config.yml文件重新加载配置。
     * 这样用户在调整插件配置后，无需重启就可以马上使用新的配置。<br>
     * By using this, Nukkit will reload the config from 'config.yml' file, then it isn't necessary to restart
     * for plugin user who changes the config and needs to use new config at once.</p>
     *
     * @see cn.nukkit.plugin.Plugin#getDataFolder
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void reloadConfig();

    /**
     * 返回运行这个插件的服务器的{@link cn.nukkit.Server}对象。<br>
     * Gets the NukkitServer which is running this plugin, and returns as a {@link cn.nukkit.Server} object.
     *
     * @see cn.nukkit.Server
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    NukkitServer getServer();

    /**
     * 返回这个插件的名字。<br>
     * Returns the name of this plugin.
     * <p>
     * <p>Nukkit会从已经读取的插件描述中获取插件的名字。<br>
     * Nukkit will read plugin name from plugin description.</p>
     *
     * @see cn.nukkit.plugin.Plugin#getDescription
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    String getName();

    /**
     * 返回这个插件的日志记录器为{@link cn.nukkit.plugin.PluginLogger}对象。<br>
     * Returns the logger of this plugin as a {@link cn.nukkit.plugin.PluginLogger} object.
     * <p>
     * <p>使用日志记录器，你可以在控制台和日志文件输出信息。<br>
     * You can use a plugin logger to output messages to the console and log file.</p>
     *
     * @see cn.nukkit.plugin.PluginLogger
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginLogger getLogger();

    /**
     * 返回这个插件的加载器为{@link cn.nukkit.plugin.PluginLoader}对象。<br>
     * Returns the loader of this plugin as a {@link cn.nukkit.plugin.PluginLoader} object.
     *
     * @see cn.nukkit.plugin.PluginLoader
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginLoader getPluginLoader();

}
