package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.utils.Config;

import java.io.File;
import java.io.InputStream;

/**
 * 所有Nukkit插件必须实现的接口。<br />
 * An interface what must be implemented by all Nukkit plugins.
 *
 * <p>对于插件作者，我们建议让插件主类继承{@code PluginBase}类，而不是实现这个接口。<br />
 * For plugin developers: it's recommended to use {@code PluginBase} for an actual plugin
 * instead of implement this interface.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.plugin.PluginBase
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface Plugin extends CommandExecutor {
    /**
     * 在一个Nukkit插件被加载时调用的方法。这个方法会在{@code onEnable()}之前调用。<br />
     * Called when a Nukkit plugin is loaded, before {@code onEnable()} .
     *
     * <p>应该填写加载插件时需要作出的动作。例如：初始化数组、初始化数据库连接。<br />
     * Use this to init a Nukkit plugin, such as init arrays or init database connections.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onLoad();

    /**
     * 在一个Nukkit插件被启用时调用的方法。<br />
     * Called when a Nukkit plugin is enabled.
     *
     * <p>应该填写插件启用时需要作出的动作。例如：读取配置文件、读取资源、连接数据库。<br />
     * Use this to open config files, open resources, connect databases.</p>
     *
     * <p>注意到可能存在的插件管理器插件，这个方法在插件多次重启时可能被调用多次。<br />
     * Notes that there may be plugin manager plugins,
     * this method can be called many times when a plugin is restarted many times.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onEnable();

    /**
     * 返回这个Nukkit插件是否已启用。<br />
     * Whether this Nukkit plugin is enabled.
     *
     * @return 这个插件是否已经启用。<br />Whether this plugin is enabled.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isEnabled();

    /**
     * 在一个Nukkit插件被停用时调用的方法。<br />
     * Called when a Nukkit plugin is disabled.
     *
     * <p>应该填写插件停用时需要作出的动作。例如：关闭数据库，断开资源。<br />
     * Use this to free open things and finish actions,
     * such as disconnecting databases and close resources.</p>
     *
     * <p>注意到可能存在的插件管理器插件，这个方法在插件多次重启时可能被调用多次。<br />
     * Notes that there may be plugin manager plugins,
     * this method can be called many times when a plugin is restarted many times.</p>
     *
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void onDisable();

    /**
     * 返回这个Nukkit插件是否已停用。<br />
     * Whether this Nukkit plugin is disabled.
     *
     * @return 这个插件是否已经停用。<br />Whether this plugin is disabled.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isDisabled();

    /**
     * 返回这个Nukkit插件的数据文件夹。<br />
     * The data folder of this Nukkit plugin.
     *
     * <p>一般情况下，数据文件夹名字与插件名字相同，而且都放在nukkit安装目录下的plugins文件夹里。<br />
     * Under normal circumstances, the data folder has the same name with the plugin,
     * and is placed in the 'plugins' folder inside the nukkit installation directory.</p>
     *
     * @return 这个插件的数据文件夹。<br />The data folder of this plugin.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    File getDataFolder();

    /**
     * 返回描述这个Nukkit插件的{@code PluginDescription}对象。<br />
     * The description this Nukkit plugin as a {@code PluginDescription} object.
     *
     * <p>对于jar格式的Nukkit插件，插件的描述在plugin.yml文件内定义。<br />
     * For jar-packed Nukkit plugins, the description is defined in the 'plugin.yml' file.</p>
     *
     * @return 这个插件的描述。<br />A description of this plugin.
     * @see cn.nukkit.plugin.PluginDescription
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginDescription getDescription();

    /**
     * 读取这个插件特定的资源文件，并返回为{@code InputStream}对象。<br />
     * Reads a resource of this plugin, and returns as an {@code InputStream} object.
     *
     * <p>对于jar格式的Nukkit插件，Nukkit会在jar包内的resources文件夹寻找资源文件。<br />
     * For jar-packed Nukkit plugins, Nukkit will look for your resource file
     * in the 'resources' folder in plugin jar file.</p>
     *
     * <p>当你需要把一个文件的所有内容读取为字符串，可以使用{@link cn.nukkit.utils.Utils#readFile}函数，
     * 来从{@code InputStream}读取所有内容为字符串。例如：<br />
     * When you need to read the whole file content as a String, you can use {@link cn.nukkit.utils.Utils#readFile}
     * to read from a {@code InputStream} and get whole content as a String. For example:</p>
     * <p><code>String string = Utils.readFile(this.getResource("string.txt"));</code></p>
     *
     * @param filename 要读取的资源文件名字。<br />The name of the resource file.
     * @return 读取的资源文件的 {@code InputStream}对象。若错误会返回{@code null}<br />
     *          The resource as an {@code InputStream} object, or {@code null} when an error occurred.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    InputStream getResource(String filename);

    boolean saveResource(String filename);

    boolean saveResource(String filename, boolean replace);

    Config getConfig();

    void saveConfig();

    void saveDefaultConfig();

    void reloadConfig();

    Server getServer();

    String getName();

    PluginLogger getLogger();

    PluginLoader getPluginLoader();

}
