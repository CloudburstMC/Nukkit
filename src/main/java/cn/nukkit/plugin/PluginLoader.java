package cn.nukkit.plugin;

import java.io.File;
import java.util.regex.Pattern;

/**
 * 描述一个插件加载器的接口。<br>
 * An interface to describe a plugin loader.
 *
 * @author iNevet(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see JavaPluginLoader
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface PluginLoader {

    /**
     * 通过文件名字的字符串，来加载和初始化一个插件。<br>
     * Loads and initializes a plugin by its file name.
     *
     * <p>这个方法应该设置好插件的相关属性。比如，插件所在的服务器对象，插件的加载器对象，插件的描述对象，插件的数据文件夹。<br>
     * Properties for loaded plugin should be set in this method. Such as, the {@code Server} object for which this
     * plugin is running in, the {@code PluginLoader} object for its loader, and the {@code File} object for its
     * data folder.</p>
     *
     * <p>如果插件加载失败，这个方法应该返回{@code null}，或者抛出异常。<br>
     * If the plugin loader does not load this plugin successfully, a {@code null} should be returned,
     * or an exception should be thrown.</p>
     *
     * @param filename 这个插件的文件名字字符串。<br>A string of its file name.
     * @return 加载完毕的插件的 {@code Plugin}对象。<br>The loaded plugin as a {@code Plugin} object.
     * @throws java.lang.Exception 插件加载失败所抛出的异常。<br>Thrown when an error occurred.
     * @see #loadPlugin(File)
     * @see cn.nukkit.plugin.PluginBase#init(PluginLoader, cn.nukkit.Server, PluginDescription, File, File)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Plugin loadPlugin(String filename) throws Exception;

    /**
     * 通过插件的 {@code File}对象，来加载和初始化一个插件。<br>
     * Loads and initializes a plugin by a {@code File} object describes the file.
     *
     * <p>这个方法应该设置好插件的相关属性。比如，插件所在的服务器对象，插件的加载器对象，插件的描述对象，插件的数据文件夹。<br>
     * Properties for loaded plugin should be set in this method. Such as, the {@code Server} object for which this
     * plugin is running in, the {@code PluginLoader} object for its loader, and the {@code File} object for its
     * data folder.</p>
     *
     * <p>如果插件加载失败，这个方法应该返回{@code null}，或者抛出异常。<br>
     * If the plugin loader does not load this plugin successfully, a {@code null} should be returned,
     * or an exception should be thrown.</p>
     *
     * @param file 这个插件的文件的 {@code File}对象。<br>A {@code File} object for this plugin.
     * @return 加载完毕的插件的 {@code Plugin}对象。<br>The loaded plugin as a {@code Plugin} object.
     * @throws java.lang.Exception 插件加载失败所抛出的异常。<br>Thrown when an error occurred.
     * @see #loadPlugin(String)
     * @see cn.nukkit.plugin.PluginBase#init(PluginLoader, cn.nukkit.Server, PluginDescription, File, File)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Plugin loadPlugin(File file) throws Exception;

    /**
     * 通过插件文件名的字符串，来获得描述这个插件的 {@code PluginDescription}对象。<br>
     * Gets a {@code PluginDescription} object describes the plugin by its file name.
     *
     * <p>如果插件的描述对象获取失败，这个方法应该返回{@code null}。<br>
     * If the plugin loader does not get its description successfully, a {@code null} should be returned.</p>
     *
     * @param filename 这个插件的文件名字。<br>A string of its file name.
     * @return 描述这个插件的 {@code PluginDescription}对象。<br>
     * A {@code PluginDescription} object describes the plugin.
     * @see #getPluginDescription(File)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginDescription getPluginDescription(String filename);

    /**
     * 通过插件的 {@code File}对象，来获得描述这个插件的 {@code PluginDescription}对象。<br>
     * Gets a {@code PluginDescription} object describes the plugin by a {@code File} object describes the plugin file.
     *
     * <p>如果插件的描述对象获取失败，这个方法应该返回{@code null}。<br>
     * If the plugin loader does not get its description successfully, a {@code null} should be returned.</p>
     *
     * @param file 这个插件的文件的 {@code File}对象。<br>A {@code File} object for this plugin.
     * @return 描述这个插件的 {@code PluginDescription}对象。<br>
     * A {@code PluginDescription} object describes the plugin.
     * @see #getPluginDescription(String)
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    PluginDescription getPluginDescription(File file);

    /**
     * 返回这个插件加载器支持的文件类型。<br>
     * Returns the file types this plugin loader supports.
     *
     * <p>在Nukkit读取所有插件时，插件管理器会查找所有已经安装的插件加载器，通过识别这个插件是否满足下面的条件，
     * 来选择对应的插件加载器。<br>
     * When Nukkit is trying to load all its plugins, the plugin manager will look for all installed plugin loader,
     * and choose the correct one by checking if this plugin matches the filters given below.</p>
     *
     * <p>举个例子，识别这个文件是否以jar为扩展名，它的正则表达式是：<br>
     * For example, to check if this file is has a "jar" extension, the regular expression should be:<br>
     * {@code ^.+\\.jar$}<br>
     * 所以只读取jar扩展名的插件加载器，这个函数应该写成：<br>
     * So, for a jar-extension-only file plugin loader, this method should be:
     * </p>
     * <pre> {@code           @Override}
     *      public Pattern[] getPluginFilters() {
     *          return new Pattern[]{Pattern.compile("^.+\\.jar$")};
     *      }
     * </pre>
     *
     * @return 表达这个插件加载器支持的文件类型的正则表达式数组。<br>
     * An array of regular expressions, that describes what kind of file this plugin loader supports.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Pattern[] getPluginFilters();

    /**
     * 启用一个插件。<br>
     * Enables a plugin.
     *
     * @param plugin 要被启用的插件。<br>The plugin to enable.
     * @see #disablePlugin
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void enablePlugin(Plugin plugin);

    /**
     * 停用一个插件。<br>
     * Disables a plugin.
     *
     * @param plugin 要被停用的插件。<br>The plugin to disable.
     * @see #enablePlugin
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void disablePlugin(Plugin plugin);

}
