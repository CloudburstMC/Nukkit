package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.command.*;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.item.Item;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.level.Level;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.permission.BanList;
import cn.nukkit.permission.DefaultPermissions;
import cn.nukkit.plugin.JarPluginLoader;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLoadOrder;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.*;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class Server {

    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";

    private static Server instance;

    private BanList banByName;

    private BanList banByIP;

    private Config operators;

    private Config whitelist;

    private boolean isRunning = true;

    private boolean hasStopped = false;

    private PluginManager pluginManager;

    private ServerScheduler scheduler;

    private int tickCounter;

    private long nextTick;

    private float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};

    private float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private float maxTick = 20;

    private float maxUse = 0;

    private MainLogger logger;

    private CommandReader console;

    private SimpleCommandMap commandMap;

    private ConsoleCommandSender consoleSender;

    private int maxPlayers;

    private boolean autoSave;

    private EntityMetadataStore entityMetadata;
    private PlayerMetadataStore playerMetadata;
    private LevelMetadataStore levelMetadata;

    private BaseLang baseLang;

    private boolean forceLanguage;

    private long serverID;

    private String filePath;
    private String dataPath;
    private String pluginPath;


    private Config properties;
    private Config config;

    private Map<String, Player> players = new HashMap<>();

    public Server(MainLogger logger, final String filePath, String dataPath, String pluginPath) throws Exception {
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
            try {
                Utils.writeFile(this.dataPath + "nukkit.yml", this.getClass().getClassLoader().getResourceAsStream("resources/nukkit.yml"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML);

        this.logger.info("Loading " + TextFormat.GREEN + "server properties" + TextFormat.WHITE + "...");
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new LinkedHashMap<String, Object>() {
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

        this.forceLanguage = (Boolean) this.getConfig("settings.force-language", false);
        this.baseLang = new BaseLang((String) this.getConfig("settings.language", BaseLang.FALLBACK_LANGUAGE));
        this.logger.info(this.getLanguage().translateString("language.selected", new String[]{getLanguage().getName(), getLanguage().getLang()}));
        this.logger.info(getLanguage().translateString("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.WHITE));
        //todo 一些tick配置
        Object poolSize = this.getConfig("settings.async-workers", "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) this.getConfig("settings.async-workers", "auto"));
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors() * 2, 4);
            }
        }

        ServerScheduler.WORKERS = (int) poolSize;

        this.scheduler = new ServerScheduler();

        this.entityMetadata = new EntityMetadataStore();
        this.playerMetadata = new PlayerMetadataStore();
        this.levelMetadata = new LevelMetadataStore();

        this.operators = new Config(this.dataPath + "ops.txt", Config.ENUM);
        this.whitelist = new Config(this.dataPath + "white-list.txt", Config.ENUM);
        this.banByName = new BanList(this.dataPath + "banned-players.json");
        this.banByName.load();
        this.banByIP = new BanList(this.dataPath + "banned-ips.json");
        this.banByIP.load();

        this.maxPlayers = this.getPropertyInt("max-players", 20);

        if (this.getPropertyBoolean("hardcore", false) && this.getDifficulty() < 3) {
            this.setPropertyInt("difficulty", 3);
        }

        Nukkit.DEBUG = (int) this.getConfig("debug.level", 1);
        if (this.logger != null) {
            this.logger.setLogDebug(Nukkit.DEBUG > 1);
        }

        //todo NETWORK PART ***

        this.logger.info(this.getLanguage().translateString("nukkit.server.info", new String[]{this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.WHITE, this.getCodename(), this.getApiVersion()}));
        this.logger.info(this.getLanguage().translateString("nukkit.server.license", this.getName()));

        this.consoleSender = new ConsoleCommandSender();
        this.commandMap = new SimpleCommandMap(this);

        Block.init();
        Item.init();

        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);

        this.pluginManager.registerInterface(JarPluginLoader.class);

        this.pluginManager.loadPlugins(this.pluginPath);

        this.enablePlugins(PluginLoadOrder.STARTUP);

        //do a lot thing

        this.properties.save(true);

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        this.start();
    }

    public void enablePlugins(PluginLoadOrder type) {
        for (Plugin plugin : this.pluginManager.getPlugins().values()) {
            if (!plugin.isEnabled() && type == plugin.getDescription().getOrder()) {
                this.enablePlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            this.commandMap.registerServerAliases();
            DefaultPermissions.registerCorePermissions();
        }
    }

    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    public void disablePlugins() {
        this.pluginManager.disablePlugins();
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) throws Exception {
        if (sender == null) {
            throw new ServerException("CommandSender is not valid");
        }

        if (this.commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.notFound"));

        return false;
    }

    //todo: remove this 测试用
    @Deprecated
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    //todo: public void reload

    public void shutdown() {
        if (this.isRunning) {
            ServerKiller killer = new ServerKiller(90);
            killer.start();
        }
        this.isRunning = false;
    }

    public void forceShutdown() {
        if (this.hasStopped) {
            return;
        }

        try {
            if (!this.isRunning) {
                //todo sendUsage
            }

            this.hasStopped = true;

            this.shutdown();

            this.getLogger().debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            //todo alot

            this.getLogger().debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Long.MAX_VALUE);

            this.getLogger().debug("Saving properties");
            this.properties.save();

            this.getLogger().debug("Closing console");
            this.console.stop();

            //todo other things
        } catch (Exception e) {
            this.logger.emergency("Exception happened while shutting down, exit the process");
            System.exit(1);
        }
    }

    public void start() {
        //todo a lot
        this.logger.info(this.getLanguage().translateString("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));
        this.logger.info(this.getLanguage().translateString("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)));
        this.tickProcessor();
        this.forceShutdown();
    }

    public void tickProcessor() {
        this.nextTick = System.currentTimeMillis();
        while (this.isRunning) {
            this.tick();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean tick() {
        long tickTime = System.currentTimeMillis();
        if ((tickTime - this.nextTick) < -25) {
            return false;
        }

        ++this.tickCounter;

        this.scheduler.mainThreadHeartbeat(this.tickCounter);

        //todo a lot

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.maxTick = 20;
            this.maxUse = 0;

        }

        //todo a lot

        if (this.tickCounter % 100 == 0) {
            //todo clearCache
            if (this.getTicksPerSecondAverage() < 12) {
                this.logger.warning(this.getLanguage().translateString("nukkit.server.tickOverload"));
            }
        }

        long now = System.currentTimeMillis();
        int tick = (int) Math.min(20, 1000 / Math.max(1, now - tickTime));
        int use = (int) Math.min(1, (now - tickTime) / 50);

        if (this.maxTick > tick) {
            this.maxTick = tick;
        }

        if (this.maxUse < use) {
            this.maxUse = use;
        }

        System.arraycopy(this.tickAverage, 1, this.tickAverage, 0, this.tickAverage.length - 1);
        this.tickAverage[this.tickAverage.length - 1] = tick;

        System.arraycopy(this.useAverage, 1, this.useAverage, 0, this.useAverage.length - 1);
        this.useAverage[this.useAverage.length - 1] = use;

        if ((this.nextTick - tickTime) < -1000) {
            this.nextTick = tickTime;
        } else {
            this.nextTick += 50;
        }

        return true;
    }

    public void titleTick() {
        if (!Nukkit.ANSI) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        float used = ((float) Math.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 * 100)) / 100;
        float totol = ((float) Math.round(((double) runtime.totalMemory()) / 1024 / 1024 * 100)) / 100;
        float max = ((float) Math.round(((double) runtime.maxMemory()) / 1024 / 1024 * 100)) / 100;
        String usage = used + "/" + totol + "/" + max + " MB @ ";
        System.out.print((char) 0x1b + "]0;" + this.getName() + " " +
                this.getNukkitVersion() +
                " | Online " + this.players.size() + "/" + this.getMaxPlayers() +
                " | Memory " + usage + Thread.getAllStackTraces().keySet().size() + " threads" +
                " | TPS " + this.getTicksPerSecond() +
                " | Load " + this.getTickUsage() + "%" + (char) 0x07);
    }

    public String getName() {
        return "Nukkit";
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getNukkitVersion() {
        return Nukkit.VERSION;
    }

    public String getCodename() {
        return Nukkit.CODENAME;
    }

    public String getVersion() {
        return Nukkit.MINECRAFT_VERSION;
    }

    public String getApiVersion() {
        return Nukkit.API_VERSION;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPort() {
        return this.getPropertyInt("server-port", 19132);
    }

    public int getViewDistance() {
        return Math.max(56, (Integer) this.getConfig("chunk-sending.max-chunks", 256));
    }

    public String getIp() {
        return this.getPropertyString("server-ip", "0.0.0.0");
    }

    @Deprecated
    public String getServerName() {
        return this.getPropertyString("motd", "Nukkit Server For Minecraft: PE");
    }

    public long getServerUniqueId() {
        return this.serverID;
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    //todo setAutoSave

    public String getLevelType() {
        return this.getPropertyString("level-type", "DEFAULT");
    }

    public boolean getGenerateStructures() {
        return this.getPropertyBoolean("generate-structures", true);
    }

    public int getGamemode() {
        return this.getPropertyInt("gamemode", 0) & 0b11;
    }

    public boolean getForceGamemode() {
        return this.getPropertyBoolean("force-gamemode", false);
    }

    public static String getGamemodeString(int mode) {
        switch (mode) {
            case Player.SURVIVAL:
                return "%gameMode.survival";
            case Player.CREATIVE:
                return "%gameMode.creative";
            case Player.ADVENTURE:
                return "%gameMode.adventure";
            case Player.SPECTATOR:
                return "%gameMode.spectator";
        }
        return "UNKNOWN";
    }

    public static int getGamemodeFromString(String str) {
        switch (str.trim().toLowerCase()) {
            case "0":
            case "survival":
            case "s":
                return Player.SURVIVAL;

            case "1":
            case "creative":
            case "c":
                return Player.CREATIVE;

            case "2":
            case "adventure":
            case "a":
                return Player.ADVENTURE;

            case "3":
            case "spectator":
            case "view":
            case "v":
                return Player.SPECTATOR;
        }
        return -1;
    }

    public static int getDifficultyFromString(String str) {
        switch (str.trim().toLowerCase()) {
            case "0":
            case "peaceful":
            case "p":
                return 0;

            case "1":
            case "easy":
            case "e":
                return 1;

            case "2":
            case "normal":
            case "n":
                return 2;

            case "3":
            case "hard":
            case "h":
                return 3;
        }
        return -1;
    }

    public int getDifficulty() {
        return this.getPropertyInt("difficulty", 1);
    }

    public boolean hasWhitelist() {
        return this.getPropertyBoolean("white-list", false);
    }

    public int getSpawnRadius() {
        return this.getPropertyInt("spawn-protection", 16);
    }

    public boolean getAllowFlight() {
        return this.getPropertyBoolean("allow-flight", false);
    }

    public boolean isHardcore() {
        return this.getPropertyBoolean("hardcore", false);
    }

    public int getDefaultGamemode() {
        return this.getPropertyInt("gamemode", 0);
    }

    public String getMotd() {
        return this.getPropertyString("motd", "Nukkit Server For Minecraft: PE");
    }

    public MainLogger getLogger() {
        return this.logger;
    }

    public EntityMetadataStore getEntityMetadata() {
        return entityMetadata;
    }

    public PlayerMetadataStore getPlayerMetadata() {
        return playerMetadata;
    }

    public LevelMetadataStore getLevelMetadata() {
        return levelMetadata;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public ServerScheduler getScheduler() {
        return scheduler;
    }

    public int getTick() {
        return tickCounter;
    }

    public float getTicksPerSecond() {
        return ((float) Math.round(this.maxTick * 100)) / 100;
    }

    public float getTicksPerSecondAverage() {
        float sum = 0;
        int count = this.tickAverage.length;
        for (float aTickAverage : this.tickAverage) {
            sum += aTickAverage;
        }
        return ((float) Math.round(sum / count * 100)) / 100;
    }

    public float getTickUsage() {
        return ((float) Math.round(this.maxUse * 100 * 100)) / 100;
    }

    public float getTickUsageAverage() {
        float sum = 0;
        int count = this.useAverage.length;
        for (float aUseAverage : this.useAverage) {
            sum += aUseAverage;
        }
        return ((float) Math.round(sum / count * 100)) / 100;
    }

    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    public Level getLevel(int levelId) {
        //todo
        return null;
    }

    public BaseLang getLanguage() {
        return baseLang;
    }

    public Object getConfig(String variable) {
        return this.getConfig(variable, null);
    }

    public Object getConfig(String variable, Object defaultValue) {
        Object value = this.config.getNested(variable);
        return value == null ? defaultValue : value;
    }

    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    public Object getProperty(String variable, Object defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
    }

    public void setPropertyString(String variable, String value) {
        this.properties.set(variable, value);
    }

    public String getPropertyString(String variable) {
        return this.getPropertyString(variable, null);
    }

    public String getPropertyString(String variable, String defaultValue) {
        return this.properties.exists(variable) ? (String) this.properties.get(variable) : defaultValue;
    }

    public int getPropertyInt(String variable) {
        return this.getPropertyInt(variable, null);
    }

    public int getPropertyInt(String variable, Integer defaultValue) {
        return this.properties.exists(variable) ? Integer.parseInt((String) this.properties.get(variable)) : defaultValue;
    }

    public void setPropertyInt(String variable, int value) {
        this.properties.set(variable, value);
    }

    public boolean getPropertyBoolean(String variable) {
        return this.getPropertyBoolean(variable, null);
    }

    public boolean getPropertyBoolean(String variable, Object defaultValue) {
        Object value = this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        switch (String.valueOf(value)) {
            case "on":
            case "true":
            case "1":
            case "yes":
                return true;
        }
        return false;
    }

    public void setPropertyBoolean(String variable, boolean value) {
        this.properties.set(variable, value ? "1" : "0");
    }

    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) command;
        } else {
            return null;
        }
    }

    public BanList getNameBans() {
        return this.banByName;
    }

    public BanList getIPBans() {
        return this.banByIP;
    }

    //todo: addOp removeOp

    public void addWhitelist(String name) {
        this.whitelist.set(name.toLowerCase(), true);
        this.whitelist.save(true);
    }

    public void removeWhitelist(String name) {
        this.whitelist.remove(name.toLowerCase());
        this.whitelist.save(true);
    }

    public boolean isWhitelisted(String name) {
        return !this.hasWhitelist() || this.operators.exists(name, true) || this.whitelist.exists(name, true);
    }

    public boolean isOp(String name) {
        return this.operators.exists(name, true);
    }

    public Config getWhitelist() {
        return whitelist;
    }

    public Config getOps() {
        return operators;
    }

    public void reloadWhitelist() {
        this.whitelist.reload();
    }

    public Map<String, List<String>> getCommandAliases() {
        Object section = this.getConfig("aliases");
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (section instanceof Map) {
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) section).entrySet()) {
                List<String> commands = new ArrayList<>();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value instanceof List) {
                    for (String string : (List<String>) value) {
                        commands.add(string);
                    }
                } else {
                    commands.add((String) value);
                }

                result.put(key, commands);
            }
        }

        return result;

    }

    public static Server getInstance() {
        return instance;
    }

}
