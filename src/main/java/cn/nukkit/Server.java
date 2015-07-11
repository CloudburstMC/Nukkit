package cn.nukkit;

import cn.nukkit.command.CommandReader;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

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
    private Config properties;
    private ServerScheduler scheduler;

    private BaseLang baseLang;

    public BaseLang getLanguage() {
        return baseLang;
    }

    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    public Object getProperty(String variable, Object defaultValue) {
        Object value = this.config.getNested(variable);
        return value == null ? defaultValue : value;
    }

    public static Server getInstance() {
        return instance;
    }

    public Server(MainLogger logger, final String filePath, String dataPath, String pluginPath) {
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

        this.logger.info("Loading " + TextFormat.GREEN + "nukkit.yml" + TextFormat.WHITE + "...");
        if (!new File(this.dataPath + "nukkit.yml").exists()) {
            Utils.writeFile(this.dataPath + "nukkit.yml", this.getClass().getClassLoader().getResourceAsStream("resources/nukkit.yml"));
        }
        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML);

        this.logger.info("Loading " + TextFormat.GREEN + "server properties" + TextFormat.WHITE + "...");
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new HashMap<String, Object>() {
            {
                put("motd", "Nukkit Server For Minecraft: PE");
                put("server-port", 19132);
                put("write-list", false);
                put("announce-player-achievements", true);
                put("spawn-protection", 16);
                put("max-players", 20);
                put("allow-flight", false);
                put("spawn-animals", true);
                put("spawn-mobs", true);
                put("gamemode", 0);
                put("force-gamemode", false);
                put("hardcore", false);
                put("pvp", true);
                put("difficulty", 1);
                put("generator-settings", "");
                put("level-name", "world");
                put("level-seed", "");
                put("level-type", "DEFAULT");
                put("enable-query", true);
                put("enable-rcon", false);
                put("rcon.password", new BASE64Encoder().encode(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13));
                put("auto-save", false);
            }
        });
        //this.baseLang = new BaseLang("zho");
        this.baseLang = new BaseLang((String) this.getProperty("settings.language", BaseLang.FALLBACK_LANGUAGE));
        this.logger.info(this.getLanguage().translateString("language.selected", new String[]{getLanguage().getName(), getLanguage().getLang()}));
        this.logger.info(getLanguage().translateString("nukkit.server.start", new String[]{TextFormat.AQUA + Nukkit.MINECRAFT_VERSION + TextFormat.WHITE}));
        //todo 一些tick配置
        this.scheduler = new ServerScheduler();

        this.start();
    }

    public void start() {
        //todo a lot
        this.logger.info(this.getLanguage().translateString("nukkit.server.startFinished", new String[]{String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)}));
    }

    public MainLogger getLogger() {
        return this.logger;
    }
}
