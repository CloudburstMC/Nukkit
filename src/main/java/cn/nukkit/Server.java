package cn.nukkit;

import cn.nukkit.command.CommandReader;
import cn.nukkit.utils.MainLogger;

import java.io.File;

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

    }

    public MainLogger getLogger() {
        return logger;
    }
}
