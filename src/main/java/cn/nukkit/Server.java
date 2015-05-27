package cn.nukkit;

import cn.nukkit.command.CommandReader;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;

import java.io.File;
import java.util.HashMap;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class Server {
    private static Server instance;

    private MainLogger logger;
    private String filePath;
    private String dataPath;
    private String pluginPath;
    private CommandReader console;
    private Config config;


    public static Server getInstance() {
        return instance;
    }

    public Server(MainLogger logger, String filePath, String dataPath, String pluginPath) {
        instance = this;
        this.logger = logger;
        this.filePath = filePath;
        if (!new File(dataPath + "worlds/").exists()) {
            new File(dataPath + "worlds/").mkdirs();
        }

        if (!new File(dataPath + "players/").exists()) {
            new File(dataPath + "players/").mkdirs();
        }

        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }

        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";

        this.console = new CommandReader();

        //todo: VersionString 现在不必要

        this.logger.info("加载 nukkit.yml 中...");

        /**This is not the real part of the loading process of Server, Just for test
         * 这段并不是Server类应有的东西，仅是用于测试Config类的功能*/
        HashMap<String, Object> hashMap = new HashMap<String, Object>() {
            {
                put("233", true);
                put("test", "test");
            }
        };
        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML, hashMap);
        this.config.set("test", new String[]{"1", "2", "3"});
        this.config.save();
        /**This is not the real part of the loading process of Server, Just for test
         * 这段并不是Server类应有的东西，仅是用于测试Config类的功能*/

        this.logger.info("加载 服务器配置文档 中...");
    }

    public MainLogger getLogger() {
        return logger;
    }
}
