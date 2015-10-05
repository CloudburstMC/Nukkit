package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.command.*;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.level.Flat;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
<<<<<<< HEAD
import cn.nukkit.network.CompressBatchedPacket;
=======
import cn.nukkit.network.CompressBatchedTask;
>>>>>>> origin/master
import cn.nukkit.network.Network;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.query.QueryHandler;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.permission.DefaultPermissions;
import cn.nukkit.plugin.JarPluginLoader;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLoadOrder;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.*;
import sun.misc.BASE64Encoder;
import sun.nio.ch.Net;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class Server {

    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";

    private static Server instance = null;

    private BanList banByName = null;

    private BanList banByIP = null;

    private Config operators = null;

    private Config whitelist = null;

    private boolean isRunning = true;

    private boolean hasStopped = false;

    private PluginManager pluginManager = null;

    private int profilingTickrate = 20;

    private ServerScheduler scheduler = null;

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

    private Network network;

    private boolean networkCompressionAsync = true;
    public int networkCompressionLevel = 7;

    private BaseLang baseLang;

    private boolean forceLanguage = false;

    private String serverID;

    private String filePath;
    private String dataPath;
    private String pluginPath;

    private QueryHandler queryHandler;

    private QueryRegenerateEvent queryRegenerateEvent;

    private Config properties;
    private Config config;

    private Map<String, Player> players = new HashMap<>();

    private Map<Player, String> identifier = new HashMap<>();

    private Map<Integer, Level> levels = new HashMap<>();

    private Level defaultLevel = null;

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

        Object poolSize = this.getConfig("settings.async-workers", "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors() * 2, 4);
            }
        }

        ServerScheduler.WORKERS = (int) poolSize;

        Object threshold = this.getConfig("network.batch-threshold", 256);
        if (!(threshold instanceof Integer)) {
            try {
                threshold = Integer.valueOf((String) threshold);
            } catch (Exception e) {
                threshold = 256;
            }
        }
        if ((int) threshold < 0) {
            threshold = -1;
        }
        Network.BATCH_THRESHOLD = (int) threshold;
        this.networkCompressionLevel = (int) this.getConfig("network.compression-level", 7);
        this.networkCompressionAsync = (boolean) this.getConfig("network.async-compression", true);
        //todo Tick配置
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
        if (this.logger instanceof MainLogger) {
            this.logger.setLogDebug(Nukkit.DEBUG > 1);
        }

        this.logger.info(this.getLanguage().translateString("nukkit.server.networkStart", new String[]{this.getIp().equals("") ? "*" : this.getIp(), String.valueOf(this.getPort())}));
        this.serverID = UUID.randomUUID().toString();
        //todo NETWORK PART ***
        this.network = new Network(this);
        this.network.setName(this.getMotd());

        this.logger.info(this.getLanguage().translateString("nukkit.server.info", new String[]{this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.WHITE, this.getCodename(), this.getApiVersion()}));
        this.logger.info(this.getLanguage().translateString("nukkit.server.license", this.getName()));

        this.consoleSender = new ConsoleCommandSender();
        this.commandMap = new SimpleCommandMap(this);

        Block.init();
        Item.init();

        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);

        this.pluginManager.registerInterface(JarPluginLoader.class);

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);

        this.network.registerInterface(new RakNetInterface(this));

        this.pluginManager.loadPlugins(this.pluginPath);

        this.enablePlugins(PluginLoadOrder.STARTUP);

        LevelProviderManager.addProvider(this, Anvil.class);
        LevelProviderManager.addProvider(this, McRegion.class);
        //todo LevelDB provider

        Generator.addGenerator(Flat.class, "flat");
        //todo normal generator

        /*try {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) this.getConfig("worlds", new HashMap<>())).entrySet()) {
                String name = entry.getKey();
                Object worldSetting = entry.getValue();
                if ()
            }
        } catch (ClassCastException e) {
            //ignore
        }*/

        //do a lot thing

        this.properties.save(true);

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        this.start();
    }

    public static void broadcastPacket(Player[] players, DataPacket packet) {
        packet.encode();
        packet.isEncoded = true;
        if (Network.BATCH_THRESHOLD >= 0 && packet.buffer.length >= Network.BATCH_THRESHOLD) {
            Server.getInstance().batchPackets(players, new byte[][]{packet.buffer}, false, packet.getChannel());
            return;
        }

        for (Player player : players) {
            player.dataPacket(packet);
        }

        if (packet.encapsulatedPacket != null) {
            packet.encapsulatedPacket = null;
        }
    }

    public void batchPackets(Player[] players, DataPacket[] packets) {
        this.batchPackets(players, packets, false);
    }

    public void batchPackets(Player[] players, DataPacket[] packets, boolean forceSync) {
        this.batchPackets(players, packets, forceSync, 0);
    }

    public void batchPackets(Player[] players, DataPacket[] packets, boolean forceSync, int channel) {
        byte[][] payload = new byte[packets.length][];
        for (int i = 0; i < packets.length; i++) {
            DataPacket p = packets[i];
            if (!p.isEncoded) {
                p.encode();
            }
            payload[i] = p.buffer;
        }
        this.batchPackets(players, payload, forceSync, channel);
    }

    public void batchPackets(Player[] players, byte[][] payload) {
        this.batchPackets(players, payload, false);
    }

    public void batchPackets(Player[] players, byte[][] payload, boolean forceSync) {
        this.batchPackets(players, payload, forceSync, 0);
    }

    public void batchPackets(Player[] players, byte[][] payload, boolean forceSync, int channel) {
<<<<<<< HEAD
        ByteBuffer buffer = ByteBuffer.allocate(64 * 64 * 64);
        for (byte[] p : payload) {
            buffer.put(p);
        }

        byte[] data = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, data, 0, buffer.position());

=======
        byte[] data = new byte[0];
        data = Binary.appendBytes(data, payload);
>>>>>>> origin/master
        List<String> targets = new ArrayList<>();
        for (Player p : players) {
            if (p.isConnected()) {
                targets.add(this.identifier.get(p));
            }
        }

        if (!forceSync && this.networkCompressionAsync) {
<<<<<<< HEAD
            this.getScheduler().scheduleAsyncTask(new CompressBatchedPacket(data, targets, this.networkCompressionLevel, channel));
=======
            this.getScheduler().scheduleAsyncTask(new CompressBatchedTask(data, targets, this.networkCompressionLevel, channel));
>>>>>>> origin/master
        } else {
            try {
                this.broadcastPacketsCallback(Zlib.deflate(data, this.networkCompressionLevel), targets, channel);
            } catch (Exception e) {
                //ignore
            }
        }
    }

    public void broadcastPacketsCallback(byte[] data, List<String> identifiers) {
        this.broadcastPacketsCallback(data, identifiers, 0);
    }

    public void broadcastPacketsCallback(byte[] data, List<String> identifiers, int channel) {
        BatchPacket pk = new BatchPacket();
        pk.setChannel(channel);
        pk.payload = data;
        pk.encode();
        pk.isEncoded = true;

        for (String i : identifiers) {
            if (this.players.containsKey(i)) {
                this.players.get(i).dataPacket(pk);
            }
        }
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

    public boolean dispatchCommand(CommandSender sender, String commandLine) throws ServerException {
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

            this.getLogger().debug("Removing event handlers");
            HandlerList.unregisterAll();

            this.getLogger().debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            this.getLogger().debug("Saving properties");
            this.properties.save();

            this.getLogger().debug("Closing console");
            this.console.interrupt();

            this.getLogger().debug("Stopping network interfaces");
            for (SourceInterface interfaz : this.network.getInterfaces().values()) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }

            //todo other things
        } catch (Exception e) {
            this.logger.emergency("Exception happened while shutting down, exit the process");
            System.exit(1);
        }
    }

    public void start() {
        if (this.getPropertyBoolean("enable-query", true)) {
            this.queryHandler = new QueryHandler();
        }
        //todo a lot
        for (BanEntry entry : this.getIPBans().getEntires()) {
            this.network.blockAddress(entry.getName(), -1);
        }

        //todo alot

        this.logger.info(this.getLanguage().translateString("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));
        this.logger.info(this.getLanguage().translateString("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)));
        this.tickProcessor();
        this.forceShutdown();
    }

    public void addPlayer(String identifier, Player player) {
        this.players.put(identifier, player);
        this.identifier.put(player, identifier);
    }

    public void handlePacket(String address, int port, byte[] payload) {
        try {
            if (payload.length > 2 && Arrays.equals(Binary.subBytes(payload, 0, 2), new byte[]{(byte) 0xfe, (byte) 0xfd}) && this.queryHandler != null) {
                this.queryHandler.handle(address, port, payload);
            }
        } catch (Exception e) {
            this.logger.logException(e);

            this.getNetwork().blockAddress(address, 600);
        }
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

        this.network.processInterfaces();

        this.scheduler.mainThreadHeartbeat(this.tickCounter);

        //todo a lot

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.maxTick = 20;
            this.maxUse = 0;

            if ((this.tickCounter & 0b111111111) == 0) {
                try {
                    this.getPluginManager().callEvent(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                    if (this.queryHandler != null) {
                        this.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    this.logger.logException(e);
                }
            }

            this.getNetwork().updateName();
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
        //String usage = used + "/" + totol + "/" + max + " MB @ ";
        String usage = used + "/" + max + " MB";
        System.out.print((char) 0x1b + "]0;" + this.getName() + " " +
                this.getNukkitVersion() +
                " | Online " + this.players.size() + "/" + this.getMaxPlayers() +
                " | Memory " + usage +
                " | U " + ((float) Math.round((this.network.getUpload()) / 1024 * 100)) / 100
                + " D " + ((float) Math.round((this.network.getDownload()) / 1024 * 100)) / 100 +
                " kB/s | TPS " + this.getTicksPerSecond() +
                " | Load " + this.getTickUsage() + "%" + (char) 0x07);

        this.network.resetStatistics();
    }

    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
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

    public String getServerUniqueId() {
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

    public Map<String, Player> getOnlinePlayers() {
        return players;
    }

    public Player getPlayer(String name) {
        Player found = null;
        name = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().startsWith(name)) {
                int curDelta = player.getName().length() - name.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) {
                    break;
                }
            }
        }

        return found;
    }

    public Player getPlayerExact(String name) {
        name = name.toLowerCase();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(name)) {
                return player;
            }
        }

        return null;
    }

    public Player[] matchPlayer(String partialName) {
        partialName = partialName.toLowerCase();
        List<Player> matchedPlayer = new ArrayList<>();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(partialName)) {
                return new Player[]{player};
            } else if (player.getName().toLowerCase().contains(partialName)) {
                matchedPlayer.add(player);
            }
        }

        return matchedPlayer.toArray(new Player[matchedPlayer.size()]);
    }

    public void removePlayer(Player player) {
        if (this.identifier.containsKey(player)) {
            String identifier = this.identifier.get(player);
            this.players.remove(identifier);
            this.identifier.remove(player);
            return;
        }

        for (Map.Entry<String, Player> entry : this.players.entrySet()) {
            if (player.equals(entry.getValue())) {
                this.players.remove(entry.getKey());
                this.identifier.remove(player);
                break;
            }
        }
    }

    public Map<Integer, Level> getLevels() {
        return levels;
    }

    public Level getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getFolderName()) && !defaultLevel.equals(this.defaultLevel))) {
            this.defaultLevel = defaultLevel;
        }
    }

    public boolean isLevelLoaded(String name) {
        return this.getLevelByName(name) != null;
    }

    public Level getLevel(int levelId) {
        if (this.levels.containsKey(levelId)) {
            return this.levels.get(levelId);
        }
        return null;
    }

    public Level getLevelByName(String name) {
        for (Level level : this.getLevels().values()) {
            if (level.getFolderName().equals(name)) {
                return level;
            }
        }

        return null;
    }

    public BaseLang getLanguage() {
        return baseLang;
    }

    public boolean isLanguageForced() {
        return forceLanguage;
    }

    public Network getNetwork() {
        return network;
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
