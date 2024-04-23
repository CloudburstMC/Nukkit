package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.*;
import cn.nukkit.command.*;
import cn.nukkit.console.NukkitConsole;
import cn.nukkit.customblock.CustomBlockManager;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.server.BatchPacketsEvent;
import cn.nukkit.event.server.PlayerDataSerializeEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.event.server.ServerStopEvent;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.EnumLevel;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.level.generator.*;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.metrics.NukkitMetrics;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.BatchingHelper;
import cn.nukkit.network.Network;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.query.QueryHandler;
import cn.nukkit.network.rcon.RCON;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.permission.DefaultPermissions;
import cn.nukkit.permission.Permissible;
import cn.nukkit.plugin.JavaPluginLoader;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLoadOrder;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.plugin.service.NKServiceManager;
import cn.nukkit.plugin.service.ServiceManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.*;
import cn.nukkit.utils.bugreport.ExceptionHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * The main server class
 *
 * @author MagicDroidX
 * @author Box
 */
@Log4j2
public class Server {

    /**
     * Permission to receive admin broadcasts such as command usage.
     */
    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    /**
     * Permission to receive common broadcasts such as join/quit/death/achievement messages.
     */
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";

    private static Server instance;

    private final BanList banByName;
    private final BanList banByIP;
    private final Config operators;
    private final Config whitelist;
    private final Config properties;
    private final Config config;

    private final String filePath;
    private final String dataPath;
    private final String pluginPath;

    private final PluginManager pluginManager;
    private final ServerScheduler scheduler;
    private final BaseLang baseLang;
    private final NukkitConsole console;
    private final ConsoleThread consoleThread;
    private final SimpleCommandMap commandMap;
    private final CraftingManager craftingManager;
    private final ResourcePackManager resourcePackManager;
    private final ConsoleCommandSender consoleSender;

    private boolean hasStopped;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private int tickCounter;
    private long nextTick;
    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float maxTick = 20;
    private float maxUse = 0;
    private int baseTickRate;
    private int autoSaveTicker;
    private int maxPlayers; // setMaxPlayers
    private boolean autoSave = true; // setAutoSave
    private int difficulty; // setDifficulty
    int spawnThresholdRadius;
    private String ip;
    private int port;
    private final UUID serverID;
    private RCON rcon;
    private final Network network;
    private QueryHandler queryHandler;
    private QueryRegenerateEvent queryRegenerateEvent;
    private final EntityMetadataStore entityMetadata;
    private final PlayerMetadataStore playerMetadata;
    private final LevelMetadataStore levelMetadata;

    private final Map<InetSocketAddress, Player> players = new HashMap<>();
    final Map<UUID, Player> playerList = new HashMap<>();

    private static final Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}.dat$");

    private final Map<Integer, Level> levels = new HashMap<Integer, Level>() {
        public Level put(Integer key, Level value) {
            Level result = super.put(key, value);
            levelArray = levels.values().toArray(new Level[0]);
            return result;
        }
        public boolean remove(Object key, Object value) {
            boolean result = super.remove(key, value);
            levelArray = levels.values().toArray(new Level[0]);
            return result;
        }
        public Level remove(Object key) {
            Level result = super.remove(key);
            levelArray = levels.values().toArray(new Level[0]);
            return result;
        }
    };

    private Level[] levelArray = new Level[0];
    private final ServiceManager serviceManager = new NKServiceManager();
    private Level defaultLevel;
    private final Thread currentThread;
    private Watchdog watchdog;
    private final DB nameLookup;
    private PlayerDataSerializer playerDataSerializer;
    private final BatchingHelper batchingHelper;

    /**
     * The server's MOTD. Remember to call network.setName() when updated.
     */
    public String motd;
    /**
     * Default player data saving enabled.
     */
    public boolean shouldSavePlayerData;
    /**
     * Anti fly checks enabled.
     */
    public boolean allowFlight;
    /**
     * Hardcore mode enabled.
     */
    public boolean isHardcore;
    /**
     * Force resource packs.
     */
    public boolean forceResources;
    /**
     * Force player gamemode to default on every join.
     */
    public boolean forceGamemode;
    /**
     * The nether dimension and portals enabled.
     */
    public boolean netherEnabled;
    /**
     * Whitelist enabled.
     */
    public boolean whitelistEnabled;
    /**
     * Xbox authentication enabled.
     */
    public boolean xboxAuth;
    /**
     * Server side achievements enabled.
     */
    public boolean achievementsEnabled;
    /**
     * The end dimension and portals enabled.
     */
    public boolean endEnabled;
    /**
     * Pvp enabled. Can be changed per world using game rules.
     */
    public boolean pvpEnabled;
    /**
     * Announce server side announcements to all players.
     */
    public boolean announceAchievements;
    /**
     * How many chunks are sent to player per tick.
     */
    public int chunksPerTick;
    /**
     * How many chunks needs to be sent before the player can spawn.
     */
    public int spawnThreshold;
    /**
     * Zlib compression level for sent packets.
     */
    public int networkCompressionLevel;
    /**
     * Maximum view distance in chunks.
     */
    public int viewDistance;
    /**
     * Server's default gamemode.
     */
    public int gamemode;
    /**
     * Minimum amount of time between player skin changes.
     */
    public int skinChangeCooldown;
    /**
     * Spawn protection radius.
     */
    public int spawnRadius;
    /**
     * How often auto save should happen.
     */
    public int autoSaveTicks;
    /**
     * Limit automatic tick rate.
     */
    public int autoTickRateLimit;
    /**
     * Set LevelDB cache size.
     */
    public int levelDbCache;
    /**
     * Use native LevelDB implementation for better performance.
     */
    public boolean useNativeLevelDB;
    /**
     * Showing plugins in query enabled.
     */
    public boolean queryPlugins;
    /**
     * Chunk caching enabled.
     */
    public boolean cacheChunks;
    /**
     * Whether attacking an entity should stop player from sprinting.
     */
    public boolean attackStopSprint;
    /**
     * Enable automatic tick rate adjustments.
     */
    public boolean autoTickRate;
    /**
     * Force server side translations.
     */
    public boolean forceLanguage;
    /**
     * Always tick players.
     */
    public boolean alwaysTickPlayers;
    /**
     * Don't disable client's own packs when force-resources is enabled.
     */
    public boolean forceResourcesAllowOwnPacks;
    /**
     * Enable encryption.
     */
    public boolean encryptionEnabled;
    /**
     * Use Snappy for packet compression for 1.19.30+ clients.
     */
    public final boolean useSnappy;
    /**
     * Batch packets smaller than this will not be compressed.
     */
    public int networkCompressionThreshold;
    /**
     * Temporary disable world saving to allow safe backup of leveldb worlds.
     */
    public boolean holdWorldSave;

    Server(final String filePath, String dataPath, String pluginPath, String predefinedLanguage) {
        Preconditions.checkState(instance == null, "Already initialized!");
        currentThread = Thread.currentThread(); // Saves the current thread instance as a reference, used in Server#isPrimaryThread()
        instance = this;

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

        this.dataPath = new File(dataPath).getAbsolutePath() + '/';
        this.pluginPath = new File(pluginPath).getAbsolutePath() + '/';

        this.playerDataSerializer = new DefaultPlayerDataSerializer(this);

        this.console = new NukkitConsole();
        this.consoleThread = new ConsoleThread();
        this.consoleThread.start();

        if (!new File(this.dataPath + "nukkit.yml").exists()) {
            this.getLogger().info(TextFormat.GREEN + "Welcome! Please choose a language first!");
            try {
                InputStream languageList = this.getClass().getClassLoader().getResourceAsStream("lang/language.list");
                if (languageList == null) {
                    throw new IllegalStateException("lang/language.list is missing. If you are running a development version, make sure you have run 'git submodule update --init'.");
                }
                String[] lines = Utils.readFile(languageList).split("\n");
                for (String line : lines) {
                    this.getLogger().info(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String fallback = BaseLang.FALLBACK_LANGUAGE;
            String language = null;
            while (language == null) {
                String lang;
                if (predefinedLanguage != null) {
                    log.info("Trying to load language from predefined language: " + predefinedLanguage);
                    lang = predefinedLanguage;
                } else {
                    lang = this.console.readLine();
                }

                InputStream conf = this.getClass().getClassLoader().getResourceAsStream("lang/" + lang + "/lang.ini");
                if (conf != null) {
                    language = lang;
                } else if(predefinedLanguage != null) {
                    log.warn("No language found for predefined language: " + predefinedLanguage + ", please choose a valid language");
                    predefinedLanguage = null;
                }
            }

            InputStream advacedConf = this.getClass().getClassLoader().getResourceAsStream("lang/" + language + "/nukkit.yml");
            if (advacedConf == null) {
                advacedConf = this.getClass().getClassLoader().getResourceAsStream("lang/" + fallback + "/nukkit.yml");
            }

            try {
                Utils.writeFile(this.dataPath + "nukkit.yml", advacedConf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML);

        log.info("Loading server properties...");

        Nukkit.DEBUG = NukkitMath.clamp(this.getConfig("debug.level", 1), 1, 3);

        int logLevel = (Nukkit.DEBUG + 3) * 100;
        org.apache.logging.log4j.Level currentLevel = Nukkit.getLogLevel();
        for (org.apache.logging.log4j.Level level : org.apache.logging.log4j.Level.values()) {
            if (level.intLevel() == logLevel && level.intLevel() > currentLevel.intLevel()) {
                Nukkit.setLogLevel(level);
                break;
            }
        }

        //ignoredPackets.addAll(getConfig().getStringList("debug.ignored-packets"));
        //ignoredPackets.add("BatchPacket");

        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ServerProperties());

        // Should not be modified after startup
        this.useSnappy = this.getConfig("network.compression-use-snappy", false);

        this.baseLang = new BaseLang(this.getConfig("settings.language", BaseLang.FALLBACK_LANGUAGE));

        this.loadSettings();

        log.info(this.getLanguage().translateString("language.selected", new String[]{getLanguage().getName(), getLanguage().getLang()}));
        log.info(getLanguage().translateString("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.RESET));

        Object poolSize = this.getConfig("settings.async-workers", "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors() + 1, 4);
            }
        }

        ServerScheduler.WORKERS = (int) poolSize;

        this.scheduler = new ServerScheduler();

        this.console.setExecutingCommands(true); // Scheduler needs to be ready

        this.batchingHelper = new BatchingHelper();

        if (this.getPropertyBoolean("enable-rcon", false)) {
            try {
                this.rcon = new RCON(this, this.getPropertyString("rcon.password", ""), (!this.getIp().isEmpty()) ? this.getIp() : "0.0.0.0", this.getPropertyInt("rcon.port", this.getPort()));
            } catch (IllegalArgumentException e) {
                log.error(baseLang.translateString(e.getMessage(), e.getCause().getMessage()));
            }
        }

        this.entityMetadata = new EntityMetadataStore();
        this.playerMetadata = new PlayerMetadataStore();
        this.levelMetadata = new LevelMetadataStore();

        this.operators = new Config(this.dataPath + "ops.txt", Config.ENUM);
        this.whitelist = new Config(this.dataPath + "white-list.txt", Config.ENUM);
        this.banByName = new BanList(this.dataPath + "banned-players.json");
        this.banByName.load();
        this.banByIP = new BanList(this.dataPath + "banned-ips.json");
        this.banByIP.load();

        this.consoleSender = new ConsoleCommandSender();
        this.commandMap = new SimpleCommandMap(this);

        registerEntities();
        registerBlockEntities();

        Block.init();
        Enchantment.init();
        GlobalBlockPalette.init();
        RuntimeItems.init();
        Item.init();
        EnumBiome.values();
        Effect.init();
        Potion.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        CustomBlockManager.init(this);

        // Convert legacy data before plugins get the chance to mess with it
        try {
            nameLookup = Iq80DBFactory.factory.open(new File(dataPath, "players"), new Options()
                            .createIfMissing(true)
                            .compressionType(CompressionType.ZLIB_RAW));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        convertLegacyPlayerData();

        this.serverID = UUID.randomUUID();

        this.craftingManager = new CraftingManager();
        this.resourcePackManager = new ResourcePackManager(new File(Nukkit.DATA_PATH, "resource_packs"));

        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);

        this.pluginManager.registerInterface(JavaPluginLoader.class);

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);

        log.info(this.baseLang.translateString("nukkit.server.networkStart", new String[]{this.getIp().isEmpty() ? "*" : this.getIp(), String.valueOf(this.getPort())}));
        this.network = new Network(this);
        this.network.setName(this.getMotd());
        this.network.setSubName(this.getSubMotd());
        this.network.registerInterface(new RakNetInterface(this));

        if (!this.encryptionEnabled) {
            this.getLogger().warning("Encryption is not enabled! For better security, it's recommended to enable it (network.encryption: true in nukkit.yml) if you don't use a proxy software.");
        }

        if (!this.xboxAuth) {
            this.getLogger().warning("Xbox authentication is not enabled! It's recommended to enable it (xbox-auth=on in server.properties) if you don't use a proxy software or an authentication plugin.");
        }

        log.info(this.getLanguage().translateString("nukkit.server.info", this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.WHITE, TextFormat.AQUA + this.getCodename() + TextFormat.WHITE, this.getApiVersion()));
        log.info(this.getLanguage().translateString("nukkit.server.license", this.getName()));

        ExceptionHandler.registerExceptionHandler();

        this.pluginManager.loadPlugins(this.pluginPath);
        this.enablePlugins(PluginLoadOrder.STARTUP);

        try {
            if (CustomBlockManager.get().closeRegistry()) {
                RuntimeItems.getMapping().generatePalette();
            }

            Item.initCreativeItems();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to init custom blocks", e);
        }

        craftingManager.rebuildPacket();

        LevelProviderManager.addProvider(this, Anvil.class);
        LevelProviderManager.addProvider(this, LevelDBProvider.class);

        Generator.addGenerator(Flat.class, "flat", Generator.TYPE_FLAT);
        Generator.addGenerator(Normal.class, "normal", Generator.TYPE_INFINITE);
        Generator.addGenerator(Normal.class, "default", Generator.TYPE_INFINITE);
        Generator.addGenerator(Nether.class, "nether", Generator.TYPE_NETHER);
        Generator.addGenerator(End.class, "the_end", Generator.TYPE_THE_END);
        Generator.addGenerator(cn.nukkit.level.generator.Void.class, "void", Generator.TYPE_VOID);

        for (String name : this.getConfig("worlds", new HashMap<String, Object>()).keySet()) {
            if (!this.loadLevel(name)) {
                long seed;
                try {
                    seed = ((Integer) this.getConfig("worlds." + name + ".seed")).longValue();
                } catch (Exception e) {
                    seed = System.currentTimeMillis();
                }

                Map<String, Object> options = new HashMap<>();
                String[] opts = (this.getConfig("worlds." + name + ".generator", Generator.getGenerator("default").getSimpleName())).split(":");
                Class<? extends Generator> generator = Generator.getGenerator(opts[0]);
                if (opts.length > 1) {
                    StringBuilder preset = new StringBuilder();
                    for (int i = 1; i < opts.length; i++) {
                        preset.append(opts[i]).append(":");
                    }
                    preset = new StringBuilder(preset.substring(0, preset.length() - 1));

                    options.put("preset", preset.toString());
                }

                this.generateLevel(name, seed, generator, options);
            }
        }

        if (this.getDefaultLevel() == null) {
            String defaultName = this.getPropertyString("level-name", "world");
            if (defaultName == null || defaultName.trim().isEmpty()) {
                this.getLogger().warning("level-name cannot be null, using default");
                defaultName = "world";
                this.setPropertyString("level-name", defaultName);
            }

            if (!this.loadLevel(defaultName)) {
                long seed;
                String seedString = String.valueOf(this.getProperty("level-seed", System.currentTimeMillis()));
                try {
                    seed = Long.parseLong(seedString);
                } catch (NumberFormatException e) {
                    seed = seedString.hashCode();
                }
                this.generateLevel(defaultName, seed == 0 ? System.currentTimeMillis() : seed);
            }

            this.setDefaultLevel(this.getLevelByName(defaultName));
        }

        if (this.defaultLevel == null) {
            this.getLogger().emergency(this.baseLang.translateString("nukkit.level.defaultError"));
            this.forceShutdown();
            return;
        }

        this.properties.save(true);

        //for (Map.Entry<Integer, Level> entry : this.getLevels().entrySet()) {
            Level level = this.defaultLevel;//entry.getValue();
            this.getLogger().debug("Preparing spawn region for level " + level.getName());
            Position spawn = level.getSpawnLocation();
            level.populateChunk(spawn.getChunkX(), spawn.getChunkZ(), true);
        //}

        EnumLevel.initLevels();

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        if (Nukkit.DEBUG < 2) {
            this.watchdog = new Watchdog(this, 60000);
            this.watchdog.start();
        }

        // Initialize metrics
        new NukkitMetrics(this);

        this.start();
    }

    public int broadcastMessage(String message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    public int broadcastMessage(TextContainer message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    public int broadcastMessage(String message, CommandSender[] recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.length;
    }

    public int broadcastMessage(String message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public int broadcastMessage(TextContainer message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public int broadcast(String message, String permissions) {
        Set<CommandSender> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : this.pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                    recipients.add((CommandSender) permissible);
                }
            }
        }

        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public int broadcast(TextContainer message, String permissions) {
        Set<CommandSender> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : this.pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                    recipients.add((CommandSender) permissible);
                }
            }
        }

        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    public static void broadcastPacket(Collection<Player> players, DataPacket packet) {
        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    public static void broadcastPacket(Player[] players, DataPacket packet) {
        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    public void batchPackets(Player[] players, DataPacket[] packets) {
        if (players == null || packets == null || players.length == 0 || packets.length == 0) {
            return;
        }

        BatchPacketsEvent ev = new BatchPacketsEvent(players, packets, true);
        pluginManager.callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        this.batchingHelper.batchPackets(players, packets);
    }

    /**
     * Enable all plugins with matching load order
     * @param type load order
     */
    public void enablePlugins(PluginLoadOrder type) {
        for (Plugin plugin : new ArrayList<>(this.pluginManager.getPlugins().values())) {
            if (!plugin.isEnabled() && type == plugin.getDescription().getOrder()) {
                this.enablePlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            this.commandMap.registerServerAliases();
            DefaultPermissions.registerCorePermissions();
        }
    }

    /**
     * Enable a plugin
     * @param plugin plugin
     */
    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    /**
     * Disable all loaded plugins
     */
    public void disablePlugins() {
        this.pluginManager.disablePlugins();
    }

    /**
     * Run a command as CommandSender. Use server.getConsoleSender() to run as CONSOLE.
     * @param sender command sender
     * @param commandLine command without slash
     * @return command was found and attempted to be executed
     */
    public boolean dispatchCommand(CommandSender sender, String commandLine) throws ServerException {
        // First we need to check if this command is on the main thread or not, if not, warn the user
        if (!this.isPrimaryThread()) {
            getLogger().warning("Command dispatched asynchronously: " + commandLine);
        }

        if (sender == null) {
            throw new ServerException("CommandSender is not valid");
        }

        if (this.commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", commandLine));

        return false;
    }

    /**
     * Get server console CommandSender
     * @return ConsoleCommandSender
     */
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    /**
     * Reload the server. Notice: may cause issues with some plugins.
     */
    public void reload() {
        log.info("Saving levels...");
        for (Level level : this.levelArray) {
            level.save();
        }

        this.pluginManager.clearPlugins();
        this.commandMap.clearCommands();

        log.info("Reloading server properties...");
        this.properties.reload();
        this.loadSettings();

        this.banByIP.load();
        this.banByName.load();
        this.reloadWhitelist();
        this.operators.reload();

        for (BanEntry entry : this.banByIP.getEntires().values()) {
            try {
                this.network.blockAddress(InetAddress.getByName(entry.getName()));
            } catch (UnknownHostException ignore) {}
        }

        log.info("Reloading plugins...");
        this.pluginManager.registerInterface(JavaPluginLoader.class);
        this.pluginManager.loadPlugins(this.pluginPath);
        this.enablePlugins(PluginLoadOrder.STARTUP);
        this.enablePlugins(PluginLoadOrder.POSTWORLD);
    }

    /**
     * Mark the server to be shut down.
     */
    public void shutdown() {
        isRunning.compareAndSet(true, false);
    }

    /**
     * Shut down the server immediately.
     */
    public void forceShutdown() {
        this.forceShutdown(this.getConfig("settings.shutdown-message", "Server closed"));
    }

    /**
     * Shut down the server immediately.
     *
     * @param reason message that shows to players on disconnect
     */
    public void forceShutdown(String reason) {
        if (this.hasStopped) {
            return;
        }

        try {
            isRunning.compareAndSet(true, false);

            this.hasStopped = true;

            ServerStopEvent serverStopEvent = new ServerStopEvent();
            pluginManager.callEvent(serverStopEvent);

            if (this.holdWorldSave) {
                this.getLogger().warning("World save hold was not released! Any backup currently being taken may be invalid");
            }

            if (this.rcon != null) {
                this.getLogger().debug("Closing RCON...");
                this.rcon.close();
            }

            this.getLogger().debug("Disconnecting all players...");
            for (Player player : new ArrayList<>(this.players.values())) {
                player.close(player.getLeaveMessage(), reason);
            }

            this.getLogger().debug("Disabling all plugins...");
            this.disablePlugins();

            this.getLogger().debug("Removing event handlers...");
            HandlerList.unregisterAll();

            this.getLogger().debug("Stopping all tasks...");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            this.getLogger().debug("Unloading all levels...");
            for (Level level : this.levelArray) {
                this.unloadLevel(level, true);
                this.nextTick = System.currentTimeMillis(); // Fix Watchdog killing the server while saving worlds
            }

            this.getLogger().debug("Closing console...");
            this.consoleThread.interrupt();

            this.getLogger().debug("Closing BatchingHelper...");
            this.batchingHelper.shutdown();

            this.getLogger().debug("Stopping network interfaces...");
            for (SourceInterface interfaz : this.network.getInterfaces()) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }

            if (nameLookup != null) {
                this.getLogger().debug("Closing name lookup DB...");
                nameLookup.close();
            }

            this.getLogger().debug("Stopping Watchdog...");
            if (this.watchdog != null) {
                this.watchdog.kill();
            }
        } catch (Exception e) {
            log.fatal("Exception happened while shutting down, exiting the process", e);
            System.exit(1);
        }
    }

    /**
     * Internal: Start the server
     */
    public void start() {
        if (this.getPropertyBoolean("enable-query", false)) {
            this.queryHandler = new QueryHandler();
        }

        for (BanEntry entry : this.banByIP.getEntires().values()) {
            try {
                this.network.blockAddress(InetAddress.getByName(entry.getName()));
            } catch (UnknownHostException ignore) {}
        }

        this.tickCounter = 0;

        //log.info(this.getLanguage().translateString("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));

        log.info(this.baseLang.translateString("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)));

        this.tickProcessor();
        this.forceShutdown();
    }

    private static final byte[] PREFIX = {(byte) 0xfe, (byte) 0xfd};

    /**
     * Internal: Handle query
     * @param address sender address
     * @param payload payload
     */
    public void handlePacket(InetSocketAddress address, ByteBuf payload) {
        try {
            if (this.queryHandler == null || !payload.isReadable(3)) {
                return;
            }
            byte[] prefix = new byte[2];
            payload.readBytes(prefix);
            if (Arrays.equals(prefix, PREFIX)) {
                this.queryHandler.handle(address, payload);
            }
        } catch (Exception e) {
            log.error("Error whilst handling packet", e);
            this.network.blockAddress(address.getAddress(), 300);
        }
    }

    private int lastLevelGC;

    /**
     * Internal: Tick the server
     */
    public void tickProcessor() {
        this.nextTick = System.currentTimeMillis();
        try {
            while (this.isRunning.get()) {
                try {
                    this.tick();

                    long next = this.nextTick;
                    long current = System.currentTimeMillis();

                    if (next - 0.1 > current) {
                        long allocated = next - current - 1;

                        // Instead of wasting time, do something potentially useful
                        int offset = 0;
                        for (int i = 0; i < levelArray.length; i++) {
                            offset = (i + lastLevelGC) % levelArray.length;
                            Level level = levelArray[offset];
                            if (!level.isBeingConverted) {
                                level.doGarbageCollection(allocated - 1);
                            }
                            allocated = next - System.currentTimeMillis();
                            if (allocated <= 0) break;
                        }
                        lastLevelGC = offset + 1;

                        if (allocated > 0) {
                            try {
                                Thread.sleep(allocated, 900000);
                            } catch (Exception e) {
                                this.getLogger().logException(e);
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    this.getLogger().logException(e);
                }
            }
        } catch (Throwable e) {
            log.fatal("Exception happened while ticking server", e);
            log.fatal(Utils.getAllThreadDumps());
        }
    }

    public void onPlayerCompleteLoginSequence(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID());
    }

    public void addPlayer(InetSocketAddress socketAddress, Player player) {
        this.players.put(socketAddress, player);
    }

    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID());
    }

    public void removeOnlinePlayer(Player player) {
        if (this.playerList.remove(player.getUniqueId()) != null) {
            PlayerListPacket pk = new PlayerListPacket();
            pk.type = PlayerListPacket.TYPE_REMOVE;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(player.getUniqueId())};

            Server.broadcastPacket(this.playerList.values(), pk);
        }
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", this.playerList.values());
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, this.playerList.values());
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", players);
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId)};
        this.batchPackets(players, new DataPacket[]{pk}); // This is sent "directly" so it always gets through before possible TYPE_REMOVE packet for NPCs etc.
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Collection<Player> players) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, players.toArray(new Player[0]));
    }

    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        for (Player player : players) {
            player.dataPacket(pk);
        }
    }

    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.toArray(new Player[0]));
    }

    public void removePlayerListData(UUID uuid, Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        player.dataPacket(pk);
    }

    public void sendFullPlayerListData(Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = this.playerList.values().stream()
                .map(p -> new PlayerListPacket.Entry(
                        p.getUniqueId(),
                        p.getId(),
                        p.getDisplayName(),
                        p.getSkin(),
                        p.getLoginChainData().getXUID()))
                .toArray(PlayerListPacket.Entry[]::new);
        player.dataPacket(pk);
    }

    public void sendRecipeList(Player player) {
        player.dataPacket(CraftingManager.packet);
    }

    private void checkTickUpdates(int currentTick) {
        if (this.alwaysTickPlayers) {
            for (Player p : new ArrayList<>(this.players.values())) {
                p.onUpdate(currentTick);
            }
        }

        for (Player p : this.getOnlinePlayers().values()) {
            p.resetPacketCounters();
        }

        // Do level ticks
        for (Level level : this.levelArray) {
            if (level.isBeingConverted || (level.getTickRate() > this.baseTickRate && --level.tickRateCounter > 0)) {
                continue;
            }

            try {
                long levelTime = System.currentTimeMillis();
                level.doTick(currentTick);
                int tickMs = (int) (System.currentTimeMillis() - levelTime);
                level.tickRateTime = tickMs;

                if (this.autoTickRate) {
                    if (tickMs < 50 && level.getTickRate() > this.baseTickRate) {
                        int r;
                        level.setTickRate(r = level.getTickRate() - 1);
                        if (r > this.baseTickRate) {
                            level.tickRateCounter = level.getTickRate();
                        }
                        this.getLogger().debug("Raising level \"" + level.getName() + "\" tick rate to " + level.getTickRate() + " ticks");
                    } else if (tickMs >= 50) {
                        if (level.getTickRate() == this.baseTickRate) {
                            level.setTickRate(Math.max(this.baseTickRate + 1, Math.min(this.autoTickRateLimit, tickMs / 50)));
                            this.getLogger().debug("Level \"" + level.getName() + "\" took " + tickMs + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        } else if ((tickMs / level.getTickRate()) >= 50 && level.getTickRate() < this.autoTickRateLimit) {
                            level.setTickRate(level.getTickRate() + 1);
                            this.getLogger().debug("Level \"" + level.getName() + "\" took " + tickMs + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        }
                        level.tickRateCounter = level.getTickRate();
                    }
                }
            } catch (Exception e) {
                log.error(this.baseLang.translateString("nukkit.level.tickError", new String[]{level.getFolderName(), Utils.getExceptionMessage(e)}));
            }
        }
    }

    public void doAutoSave() {
        if (this.autoSave) {
            log.debug("Running auto save...");

            for (Player player : this.players.values()) {
                if (player.isOnline()) {
                    player.save(true);
                }
            }

            for (Level level : this.levelArray) {
                if (level.getAutoSave()) {
                    if (level.getProvider() != null) {
                        try {
                            level.save();
                        } catch (Exception ex) {
                            getLogger().error("Failed to auto save " + level.getName(), ex);
                        }
                    }
                }
            }
        }
    }

    private void tick() {
        long tickTime = System.currentTimeMillis();

        long time = tickTime - this.nextTick;
        if (time < -25) {
            try {
                Thread.sleep(Math.max(5, -time - 25));
            } catch (InterruptedException e) {
                this.getLogger().logException(e);
            }
        }

        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -25) {
            return;
        }

        ++this.tickCounter;

        this.network.processInterfaces();

        if (this.rcon != null) {
            this.rcon.check();
        }

        this.scheduler.mainThreadHeartbeat(this.tickCounter);

        this.checkTickUpdates(this.tickCounter);

        for (Player player : new ArrayList<>(this.players.values())) {
            player.checkNetwork();
        }

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();

            //this.network.resetStatistics(); // Unnecessary since addStatistics is not used in the new raknet
            this.maxTick = 20;
            this.maxUse = 0;

            if ((this.tickCounter & 0b111111111) == 0) {
                try {
                    this.pluginManager.callEvent(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                    if (this.queryHandler != null) {
                        this.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }

            this.network.updateName();
        }

        if (++this.autoSaveTicker >= this.autoSaveTicks) {
            this.autoSaveTicker = 0;
            this.doAutoSave();
        }

        if (this.tickCounter % 100 == 0) {
            for (Level level : this.levelArray) {
                if (!level.isBeingConverted) {
                    level.doChunkGarbageCollection();
                }
            }
        }

        long nowNano = System.nanoTime();

        float tick = (float) Math.min(20, 1000000000 / Math.max(1000000, ((double) nowNano - tickTimeNano)));
        float use = (float) Math.min(1, ((double) (nowNano - tickTimeNano)) / 50000000);

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
    }

    public long getNextTick() {
        return nextTick;
    }

    private void titleTick() {
        if (!Nukkit.TITLE) {
            return;
        }
        Runtime runtime = Runtime.getRuntime();
        double used = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double max = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        System.out.print((char) 0x1b + "]0;Nukkit " + Nukkit.VERSION  +
                " | Online " + this.playerList.size() + '/' + this.maxPlayers +
                " | Memory " + Math.round(used / max * 100) + '%' +
                /*" | U " + NukkitMath.round((this.network.getUpload() / 1024 * 1000), 2) +
                " D " + NukkitMath.round((this.network.getDownload() / 1024 * 1000), 2) + " kB/s" +*/
                " | TPS " + this.getTicksPerSecond() +
                " | Load " + this.getTickUsage() + '%' + (char) 0x07);
    }

    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
    }

    public String getName() {
        return "Nukkit";
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public String getNukkitVersion() {
        return Nukkit.VERSION;
    }

    public String getCodename() {
        return "";
    }

    public String getVersion() {
        return ProtocolInfo.MINECRAFT_VERSION;
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

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getPort() {
        return port;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public String getIp() {
        return ip;
    }

    public UUID getServerUniqueId() {
        return this.serverID;
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        for (Level level : this.levelArray) {
            level.setAutoSave(this.autoSave);
        }
    }

    public String getLevelType() {
        return this.getPropertyString("level-type", "default");
    }

    public int getGamemode() {
        return gamemode;
    }

    public boolean getForceGamemode() {
        return this.forceGamemode;
    }

    public static String getGamemodeString(int mode) {
        return getGamemodeString(mode, false);
    }

    public static String getGamemodeString(int mode, boolean direct) {
        switch (mode) {
            case Player.SURVIVAL:
                return direct ? "Survival" : "%gameMode.survival";
            case Player.CREATIVE:
                return direct ? "Creative" : "%gameMode.creative";
            case Player.ADVENTURE:
                return direct ? "Adventure" : "%gameMode.adventure";
            case Player.SPECTATOR:
                return direct ? "Spectator" : "%gameMode.spectator";
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
            case "spc":
            case "view":
            case "v":
                return Player.SPECTATOR;
            case "default":
                return Server.getInstance().getDefaultGamemode();
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
        return this.difficulty;
    }

    public void setDifficulty(int difficulty) {
        int value = difficulty;
        if (value < 0) value = 0;
        if (value > 3) value = 3;
        this.difficulty = value;
        this.setPropertyInt("difficulty", value);
    }

    public boolean hasWhitelist() {
        return this.whitelistEnabled;
    }

    public int getSpawnRadius() {
        return spawnRadius;
    }

    public boolean getAllowFlight() {
        return allowFlight;
    }

    public boolean isHardcore() {
        return this.isHardcore;
    }

    public int getDefaultGamemode() {
        return this.getGamemode();
    }

    /**
     * Get MOTD
     * @return motd
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Get Sub-MOTD (level name)
     * @return sub-motd
     */
    public String getSubMotd() {
        String sub = this.getPropertyString("sub-motd", "Powered by Nukkit");
        if (sub.isEmpty()) sub = "Powered by Nukkit";
        return sub;
    }

    public boolean getForceResources() {
        return this.forceResources;
    }

    public MainLogger getLogger() {
        return MainLogger.getLogger();
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

    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public ServerScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Get current tick
     *
     * @return current tick
     */
    public int getTick() {
        return tickCounter;
    }

    /**
     * Get ticks per second
     *
     * @return TPS
     */
    public float getTicksPerSecond() {
        return ((float) Math.round(this.maxTick * 100)) / 100;
    }

    /**
     * Get average ticks per second
     *
     * @return average TPS
     */
    public float getTicksPerSecondAverage() {
        float sum = 0;
        int count = this.tickAverage.length;
        for (float aTickAverage : this.tickAverage) {
            sum += aTickAverage;
        }
        return (float) NukkitMath.round(sum / count, 2);
    }

    /**
     * Get main thread load
     *
     * @return tick usage %
     */
    public float getTickUsage() {
        return (float) NukkitMath.round(this.maxUse * 100, 2);
    }

    /**
     * Get average main thread load
     *
     * @return average main thread load
     */
    public float getTickUsageAverage() {
        float sum = 0;
        for (float aUseAverage : this.useAverage) {
            sum += aUseAverage;
        }
        return ((float) Math.round(sum / this.useAverage.length * 100)) / 100;
    }

    /**
     * Get command map
     *
     * @return command map
     */
    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    /**
     * Get all online players
     *
     * @return online players
     */
    public Map<UUID, Player> getOnlinePlayers() {
        return ImmutableMap.copyOf(playerList);
    }

    /**
     * Get online player count
     *
     * @return online player count
     */
    public int getOnlinePlayersCount() {
        return this.playerList.size();
    }

    /**
     * Register a recipe to CraftingManager.
     * Please use getCraftingManager().registerRecipe(protocol, recipe) instead
     * @param recipe recipe
     */
    public void addRecipe(Recipe recipe) {
        this.craftingManager.registerRecipe(recipe);
    }

    /**
     * Get an online player by uuid
     *
     * @param uuid uuid
     * @return Optional Player
     */
    public Optional<Player> getPlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return Optional.ofNullable(playerList.get(uuid));
    }

    /**
     * Get known player uuid by player name
     *
     * @param name player name
     * @return Optional UUID
     */
    public Optional<UUID> lookupName(String name) {
        byte[] nameBytes = name.toLowerCase().getBytes(StandardCharsets.UTF_8);
        byte[] uuidBytes = nameLookup.get(nameBytes);
        if (uuidBytes == null) {
            return Optional.empty();
        }

        if (uuidBytes.length != 16) {
            log.warn("Invalid uuid in name lookup database detected! Removing...");
            nameLookup.delete(nameBytes);
            return Optional.empty();
        }

        ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
        return Optional.of(new UUID(buffer.getLong(), buffer.getLong()));
    }

    void updateName(UUID uuid, String name) {
        byte[] nameBytes = name.toLowerCase().getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        nameLookup.put(nameBytes, buffer.array());
    }

    @Deprecated
    public IPlayer getOfflinePlayer(final String name) {
        IPlayer result = this.getPlayerExact(name);
        if (result != null) {
            return result;
        }

        return lookupName(name).map(uuid -> new OfflinePlayer(this, uuid, name))
                .orElse(new OfflinePlayer(this, name));
    }

    public IPlayer getOfflinePlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        Optional<Player> onlinePlayer = getPlayer(uuid);
        if (onlinePlayer.isPresent()) {
            return onlinePlayer.get();
        }

        return new OfflinePlayer(this, uuid);
    }

    public CompoundTag getOfflinePlayerData(UUID uuid) {
        return getOfflinePlayerData(uuid, false);
    }

    public CompoundTag getOfflinePlayerData(UUID uuid, boolean create) {
        return getOfflinePlayerDataInternal(uuid.toString(), true, create);
    }

    @Deprecated
    public CompoundTag getOfflinePlayerData(String name) {
        return getOfflinePlayerData(name, false);
    }

    @Deprecated
    public CompoundTag getOfflinePlayerData(String name, boolean create) {
        Optional<UUID> uuid = lookupName(name);
        return getOfflinePlayerDataInternal(uuid.map(UUID::toString).orElse(name), true, create);
    }

    private CompoundTag getOfflinePlayerDataInternal(String name, boolean runEvent, boolean create) {
        Preconditions.checkNotNull(name, "name");

        PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(name, playerDataSerializer);
        if (runEvent) {
            pluginManager.callEvent(event);
        }

        Optional<InputStream> dataStream = Optional.empty();
        try {
            dataStream = event.getSerializer().read(name, event.getUuid().orElse(null)); // TODO: should the name be lower case?
            if (dataStream.isPresent()) {
                return NBTIO.readCompressed(dataStream.get());
            }
        } catch (IOException e) {
            log.warn(this.getLanguage().translateString("nukkit.data.playerCorrupted", name));
            log.throwing(e);
        } finally {
            if (dataStream.isPresent()) {
                try {
                    dataStream.get().close();
                } catch (IOException e) {
                    log.throwing(e);
                }
            }
        }
        CompoundTag nbt = null;
        if (create) {
            if (this.shouldSavePlayerData()) {
                log.info(this.getLanguage().translateString("nukkit.data.playerNotFound", name));
            }
            Position spawn = this.getDefaultLevel().getSafeSpawn();
            long time = System.currentTimeMillis();
            nbt = new CompoundTag()
                    .putLong("firstPlayed", time / 1000)
                    .putLong("lastPlayed", time / 1000)
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("0", spawn.x))
                            .add(new DoubleTag("1", spawn.y))
                            .add(new DoubleTag("2", spawn.z)))
                    .putString("Level", this.getDefaultLevel().getName())
                    .putList(new ListTag<>("Inventory"))
                    .putCompound("Achievements", new CompoundTag())
                    .putInt("playerGameType", this.getGamemode())
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("0", 0))
                            .add(new DoubleTag("1", 0))
                            .add(new DoubleTag("2", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("0", 0))
                            .add(new FloatTag("1", 0)))
                    .putFloat("FallDistance", 0)
                    .putShort("Fire", 0)
                    .putShort("Air", 400)
                    .putBoolean("OnGround", true)
                    .putBoolean("Invulnerable", false);

            this.saveOfflinePlayerData(name, nbt, true, runEvent);
        }
        return nbt;
    }

    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag) {
        this.saveOfflinePlayerData(uuid, tag, false);
    }

    public void saveOfflinePlayerData(String name, CompoundTag tag) {
        this.saveOfflinePlayerData(name, tag, false);
    }

    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag, boolean async) {
        this.saveOfflinePlayerData(uuid.toString(), tag, async);
    }

    public void saveOfflinePlayerData(String name, CompoundTag tag, boolean async) {
        Optional<UUID> uuid = lookupName(name);
        saveOfflinePlayerData(uuid.map(UUID::toString).orElse(name), tag, async, true);
    }

    private void saveOfflinePlayerData(String name, CompoundTag tag, boolean async, boolean runEvent) {
        if (this.shouldSavePlayerData()) {
            String nameLower = name.toLowerCase();
            PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(nameLower, playerDataSerializer);
            if (runEvent) {
                pluginManager.callEvent(event);
            }

            if (async) {
                this.getScheduler().scheduleTask(new Task() {
                    private volatile boolean hasRun = false;

                    @Override
                    public void onRun(int currentTick) {
                        this.onCancel();
                    }

                    // Doing it like this ensures that the player data will be saved in a server shutdown
                    @Override
                    public void onCancel() {
                        if (!this.hasRun) {
                            this.hasRun = true;
                            saveOfflinePlayerDataInternal(event.getSerializer(), tag, nameLower, event.getUuid().orElse(null));
                        }
                    }
                }, true);
            } else {
                saveOfflinePlayerDataInternal(event.getSerializer(), tag, nameLower, event.getUuid().orElse(null));
            }
        }
    }

    /**
     * Internal: Save offline player data
     *
     * @param serializer serializer
     * @param tag compound tag
     * @param name player name
     * @param uuid player uuid
     */
    private void saveOfflinePlayerDataInternal(PlayerDataSerializer serializer, CompoundTag tag, String name, UUID uuid) {
        try (OutputStream dataStream = serializer.write(name, uuid)) {
            NBTIO.writeGZIPCompressed(tag, dataStream, ByteOrder.BIG_ENDIAN);
        } catch (Exception e) {
            log.error(this.getLanguage().translateString("nukkit.data.saveError", name, e), e);
        }
    }

    /**
     * Internal: Convert legacy player saves to the uuid based saving
     */
    private void convertLegacyPlayerData() {
        File dataDirectory = new File(getDataPath(), "players/");

        File[] files = dataDirectory.listFiles(file -> {
            String name = file.getName();
            return !uuidPattern.matcher(name).matches() && name.endsWith(".dat");
        });

        if (files == null) {
            return;
        }

        for (File legacyData : files) {
            String name = legacyData.getName();
            // Remove file extension
            name = name.substring(0, name.length() - 4);

            log.debug("Attempting legacy player data conversion for {}", name);

            CompoundTag tag = getOfflinePlayerDataInternal(name, false, false);

            if (tag == null || !tag.contains("UUIDLeast") || !tag.contains("UUIDMost")) {
                // No UUID so we cannot convert. Wait until player logs in.
                continue;
            }

            UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
            if (!tag.contains("NameTag")) {
                tag.putString("NameTag", name);
            }

            if (new File(getDataPath() + "players/" + uuid.toString() + ".dat").exists()) {
                // We don't want to overwrite existing data.
                continue;
            }

            saveOfflinePlayerData(uuid.toString(), tag, false, false);

            // Add name to lookup table
            updateName(uuid, name);

            // Delete legacy data
            if (!legacyData.delete()) {
                log.warn("Unable to delete legacy data for {}", name);
            }
        }
    }

    /**
     * Get an online player by name
     *
     * @param name player name
     * @return Player or null
     */
    public Player getPlayer(String name) {
        Player found = null;
        name = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().startsWith(name)) {
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

    /**
     * Get an online player by exact player name
     *
     * @param name exact player name
     * @return Player or null
     */
    public Player getPlayerExact(String name) {
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Get players that match with the name
     *
     * @param partialName name
     * @return matching players
     */
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

        return matchedPlayer.toArray(new Player[0]);
    }

    /**
     * Internal: Remove a player from the server
     *
     * @param player player
     */
    public void removePlayer(Player player) {
        if (this.players.remove(player.getSocketAddress()) != null) {
            return;
        }

        for (InetSocketAddress socketAddress : new ArrayList<>(this.players.keySet())) {
            if (player == this.players.get(socketAddress)) {
                this.players.remove(socketAddress);
                break;
            }
        }
    }

    /**
     * Get all levels
     *
     * @return levels
     */
    public Map<Integer, Level> getLevels() {
        return levels;
    }

    /**
     * Get default level
     *
     * @return default level
     */
    public Level getDefaultLevel() {
        return defaultLevel;
    }

    /**
     * Change the default level
     *
     * @param defaultLevel new default level
     */
    public void setDefaultLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getFolderName()) && defaultLevel != this.defaultLevel)) {
            this.defaultLevel = defaultLevel;
        }
    }

    /**
     * Check whether a level is loaded
     *
     * @param name level name
     * @return is loaded
     */
    public boolean isLevelLoaded(String name) {
        return this.getLevelByName(name) != null;
    }

    /**
     * Get a level by ID
     *
     * @param levelId level ID
     * @return Level or null
     */
    public Level getLevel(int levelId) {
        return this.levels.get(levelId);
    }

    /**
     * Get a level by name
     *
     * @param name level name
     * @return Level or null
     */
    public Level getLevelByName(String name) {
        for (Level level : this.levelArray) {
            if (level.getFolderName().equalsIgnoreCase(name)) {
                return level;
            }
        }

        return null;
    }

    /**
     * Unload a level.
     * Notice that the default level cannot be unloaded without forceUnload=true
     *
     * @param level Level
     * @return unloaded
     */
    public boolean unloadLevel(Level level) {
        return this.unloadLevel(level, false);
    }

    /**
     * Unload a level
     *
     * Notice: the default level cannot be unloaded without forceUnload=true
     *
     * @param level Level
     * @param forceUnload force unload (ignore cancelled events and default level)
     * @return unloaded
     */
    public boolean unloadLevel(Level level, boolean forceUnload) {
        if (level == this.defaultLevel && !forceUnload) {
            throw new IllegalStateException("The default level cannot be unloaded while running, please switch levels.");
        }

        return level.unload(forceUnload);
    }

    /**
     * Load a level by name
     *
     * @param name level name
     * @return loaded
     */
    public boolean loadLevel(String name) {
        if (Objects.equals(name.trim(), "")) {
            throw new LevelException("Invalid empty level name");
        }

        if (!this.isPrimaryThread()) {
            getLogger().warning("Level loaded asynchronously: " + name);
        }

        if (this.isLevelLoaded(name)) {
            return true;
        } else if (!this.isLevelGenerated(name)) {
            log.warn(this.baseLang.translateString("nukkit.level.notFound", name));
            return false;
        }

        String path;

        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = this.dataPath + "worlds/" + name + '/';
        }

        Class<? extends LevelProvider> provider = LevelProviderManager.getProvider(path);

        if (provider == null) {
            log.error(this.baseLang.translateString("nukkit.level.loadError", new String[]{name, "Unknown provider"}));
            return false;
        }

        Level level;
        try {
            level = new Level(this, name, path, provider);
        } catch (Exception e) {
            log.error(this.baseLang.translateString("nukkit.level.loadError", new String[]{name, e.getMessage()}));
            return false;
        }

        level.initLevel();

        this.levels.put(level.getId(), level);

        level.setTickRate(this.baseTickRate);

        this.pluginManager.callEvent(new LevelLoadEvent(level));
        return true;
    }

    /**
     * Generate a new level
     *
     * @param name level name
     * @return generated
     */
    public boolean generateLevel(String name) {
        return this.generateLevel(name, Utils.random.nextLong());
    }

    /**
     * Generate a new level
     *
     * @param name level name
     * @param seed level seed
     * @return generated
     */
    public boolean generateLevel(String name, long seed) {
        return this.generateLevel(name, seed, null);
    }

    /**
     * Generate a new level
     *
     * @param name level name
     * @param seed level seed
     * @param generator level generator
     * @return generated
     */
    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator) {
        return this.generateLevel(name, seed, generator, new HashMap<>());
    }

    /**
     * Generate a new level
     *
     * @param name level name
     * @param seed level seed
     * @param generator level generator
     * @param options level generator options
     * @return generated
     */
    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options) {
        return generateLevel(name, seed, generator, options, null);
    }

    /**
     * Generate a new level
     *
     * @param name level name
     * @param seed level seed
     * @param generator level generator
     * @param options level generator options
     * @param provider level provider
     * @return generated
     */
    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options, Class<? extends LevelProvider> provider) {
        if (Objects.equals(name.trim(), "") || this.isLevelGenerated(name)) {
            return false;
        }

        if (!options.containsKey("preset")) {
            options.put("preset", this.getPropertyString("generator-settings", ""));
        }

        if (generator == null) {
            generator = Generator.getGenerator(this.getLevelType());
        }

        if (provider == null) {
            provider = LevelProviderManager.getProviderByName("leveldb");
        }

        String path;

        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = this.dataPath + "worlds/" + name + '/';
        }

        Level level;
        try {
            provider.getMethod("generate", String.class, String.class, long.class, Class.class, Map.class).invoke(null, path, name, seed, generator, options);

            level = new Level(this, name, path, provider);

            level.initLevel();

            this.levels.put(level.getId(), level);

            level.setTickRate(this.baseTickRate);
        } catch (Exception e) {
            log.error(this.baseLang.translateString("nukkit.level.generationError", new String[]{name, Utils.getExceptionMessage(e)}));
            return false;
        }

        this.pluginManager.callEvent(new LevelInitEvent(level));
        this.pluginManager.callEvent(new LevelLoadEvent(level));
        return true;
    }

    /**
     * Check whether a level by name is generated
     *
     * @param name level name
     * @return level found
     */
    public boolean isLevelGenerated(String name) {
        if (Objects.equals(name.trim(), "")) {
            return false;
        }

        if (this.getLevelByName(name) == null) {
            String path;

            if (name.contains("/") || name.contains("\\")) {
                path = name;
            } else {
                path = this.dataPath + "worlds/" + name + '/';
            }

            return LevelProviderManager.getProvider(path) != null;
        }

        return true;
    }

    /**
     * Get BaseLang (server's default language)
     *
     * @return BaseLang
     */
    public BaseLang getLanguage() {
        return baseLang;
    }

    /**
     * Is forcing language enabled
     *
     * @return force-language enabled
     */
    public boolean isLanguageForced() {
        return forceLanguage;
    }

    /**
     * Get Network
     *
     * @return Network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Get nukkit.yml
     *
     * @return config
     */
    public Config getConfig() {
        return this.config;
    }

    public <T> T getConfig(String variable) {
        return this.getConfig(variable, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String variable, T defaultValue) {
        Object value = this.config.get(variable);
        return value == null ? defaultValue : (T) value;
    }

    /**
     * Get server.properties
     *
     * @return server.properties as a Config
     */
    public Config getProperties() {
        return this.properties;
    }

    /**
     * Get a value from server.properties
     *
     * @param variable key
     * @return value
     */
    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    /**
     * Get a value from server.properties
     *
     * @param variable key
     * @param defaultValue default value
     * @return value
     */
    public Object getProperty(String variable, Object defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
    }

    /**
     * Set a string value in server.properties
     *
     * @param variable key
     * @param value value
     */
    public void setPropertyString(String variable, String value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    /**
     * Get a string value from server.properties
     *
     * @param key key
     * @return value
     */
    public String getPropertyString(String key) {
        return this.getPropertyString(key, null);
    }

    /**
     * Get a string value from server.properties
     *
     * @param key key
     * @param defaultValue default value
     * @return value
     */
    public String getPropertyString(String key, String defaultValue) {
        return this.properties.exists(key) ? this.properties.get(key).toString() : defaultValue;
    }

    /**
     * Get an int value from server.properties
     *
     * @param variable key
     * @return value
     */
    public int getPropertyInt(String variable) {
        return this.getPropertyInt(variable, null);
    }

    /**
     * Get an int value from server.properties
     *
     * @param variable key
     * @param defaultValue default value
     * @return value
     */
    public int getPropertyInt(String variable, Integer defaultValue) {
        return this.properties.exists(variable) ? (!this.properties.get(variable).equals("") ? Integer.parseInt(String.valueOf(this.properties.get(variable))) : defaultValue) : defaultValue;
    }

    /**
     * Set an int value in server.properties
     *
     * @param variable key
     * @param value value
     */
    public void setPropertyInt(String variable, int value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    /**
     * Get a boolean value from server.properties
     *
     * @param variable key
     * @return value
     */
    public boolean getPropertyBoolean(String variable) {
        return this.getPropertyBoolean(variable, null);
    }

    /**
     * Get a boolean value from server.properties
     *
     * @param variable key
     * @param defaultValue default value
     * @return value
     */
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

    /**
     * Set a boolean value in server.properties
     *
     * @param variable key
     * @param value value
     */
    public void setPropertyBoolean(String variable, boolean value) {
        this.properties.set(variable, value ? "1" : "0");
        this.properties.save();
    }

    /**
     * Get plugin commands
     *
     * @param name command name
     * @return PluginIdentifiableCommand or null
     */
    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) command;
        } else {
            return null;
        }
    }

    /**
     * Get list of banned players
     *
     * @return ban list
     */
    public BanList getNameBans() {
        return this.banByName;
    }

    /**
     * Get list of IP bans
     *
     * @return IP bans
     */
    public BanList getIPBans() {
        return this.banByIP;
    }

    /**
     * Give player the operator status
     *
     * @param name player name
     */
    public void addOp(String name) {
        this.operators.set(name.toLowerCase(), true);
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
        }
        this.operators.save(true);
    }

    /**
     * Remove player's operator status
     *
     * @param name player name
     */
    public void removeOp(String name) {
        this.operators.remove(name.toLowerCase());
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
        }
        this.operators.save();
    }

    /**
     * Add a player to whitelist
     *
     * @param name player name
     */
    public void addWhitelist(String name) {
        this.whitelist.set(name.toLowerCase(), true);
        this.whitelist.save(true);
    }

    /**
     * Remove a player from whitelist
     *
     * @param name player name
     */
    public void removeWhitelist(String name) {
        this.whitelist.remove(name.toLowerCase());
        this.whitelist.save(true);
    }

    /**
     * Check whether a player is whitelisted
     *
     * @param name player name
     * @return is whitelisted or whitelist is not enabled
     */
    public boolean isWhitelisted(String name) {
        return !this.hasWhitelist() || this.operators.exists(name, true) || this.whitelist.exists(name, true);
    }

    /**
     * Check whether a player is an operator
     *
     * @param name player name
     * @return is operator
     */
    public boolean isOp(String name) {
        return this.operators.exists(name, true);
    }

    /**
     * Get whitelist config
     *
     * @return whitelist
     */
    public Config getWhitelist() {
        return whitelist;
    }

    /**
     * Get operator list config
     *
     * @return operators
     */
    public Config getOps() {
        return operators;
    }

    /**
     * Reload whitelist
     */
    public void reloadWhitelist() {
        this.whitelist.reload();
    }

    /**
     * Load command aliases from nukkit.yml
     */
    public Map<String, List<String>> getCommandAliases() {
        Object section = this.getConfig("aliases");
        Map<String, List<String>> result = new LinkedHashMap<>();

        if (section instanceof Map) {
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) section).entrySet()) {
                List<String> commands = new ArrayList<>();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value instanceof List) {
                    commands.addAll((List<String>) value);
                } else {
                    commands.add((String) value);
                }

                result.put(key, commands);
            }
        }

        return result;
    }

    /**
     * Get service manager
     *
     * @return service manager
     */
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * Should player data saving be enabled
     *
     * @return player data saving enabled
     */
    public boolean shouldSavePlayerData() {
        return shouldSavePlayerData;
    }

    /**
     * How often player is allowed to change skin in game (in seconds)
     *
     * @return skin change cooldown
     */
    public int getPlayerSkinChangeCooldown() {
        return skinChangeCooldown;
    }

    /**
     * Checks the current thread against the expected primary thread for the server.
     *
     * <b>Note:</b> this method should not be used to indicate the current synchronized state of the runtime. A current thread matching the main thread indicates that it is synchronized, but a mismatch does not preclude the same assumption.
     *
     * @return true if the current thread matches the expected primary thread, false otherwise
     */
    public boolean isPrimaryThread() {
        return Thread.currentThread() == currentThread;
    }

    /**
     * Get server's primary thread
     *
     * @return primary thread
     */
    public Thread getPrimaryThread() {
        return currentThread;
    }

    /**
     * Internal method to register all default entities
     */
    private static void registerEntities() {
        //Items
        Entity.registerEntity("Item", EntityItem.class);
        Entity.registerEntity("Painting", EntityPainting.class);
        Entity.registerEntity("XpOrb", EntityXPOrb.class);
        Entity.registerEntity("ArmorStand", EntityArmorStand.class);
        Entity.registerEntity("EndCrystal", EntityEndCrystal.class);
        Entity.registerEntity("FallingSand", EntityFallingBlock.class);
        Entity.registerEntity("PrimedTnt", EntityPrimedTNT.class);
        Entity.registerEntity("Firework", EntityFirework.class);
        //Projectiles
        Entity.registerEntity("Arrow", EntityArrow.class);
        Entity.registerEntity("Snowball", EntitySnowball.class);
        Entity.registerEntity("EnderPearl", EntityEnderPearl.class);
        Entity.registerEntity("ThrownExpBottle", EntityExpBottle.class);
        Entity.registerEntity("ThrownPotion", EntityPotion.class);
        Entity.registerEntity("Egg", EntityEgg.class);
        Entity.registerEntity("ThrownLingeringPotion", EntityPotionLingering.class);
        Entity.registerEntity("ThrownTrident", EntityThrownTrident.class);
        Entity.registerEntity("FishingHook", EntityFishingHook.class);
        Entity.registerEntity("EnderEye", EntityEnderEye.class);
        Entity.registerEntity("AreaEffectCloud", EntityAreaEffectCloud.class);
        //Monsters
        Entity.registerEntity("Blaze", EntityBlaze.class);
        Entity.registerEntity("Creeper", EntityCreeper.class);
        Entity.registerEntity("CaveSpider", EntityCaveSpider.class);
        Entity.registerEntity("Drowned", EntityDrowned.class);
        Entity.registerEntity("ElderGuardian", EntityElderGuardian.class);
        Entity.registerEntity("EnderDragon", EntityEnderDragon.class);
        Entity.registerEntity("Enderman", EntityEnderman.class);
        Entity.registerEntity("Endermite", EntityEndermite.class);
        Entity.registerEntity("Evoker", EntityEvoker.class);
        Entity.registerEntity("Ghast", EntityGhast.class);
        Entity.registerEntity("Guardian", EntityGuardian.class);
        Entity.registerEntity("Husk", EntityHusk.class);
        Entity.registerEntity("MagmaCube", EntityMagmaCube.class);
        Entity.registerEntity("Phantom", EntityPhantom.class);
        Entity.registerEntity("Ravager", EntityRavager.class);
        Entity.registerEntity("Shulker", EntityShulker.class);
        Entity.registerEntity("Silverfish", EntitySilverfish.class);
        Entity.registerEntity("Skeleton", EntitySkeleton.class);
        Entity.registerEntity("SkeletonHorse", EntitySkeletonHorse.class);
        Entity.registerEntity("Slime", EntitySlime.class);
        Entity.registerEntity("Spider", EntitySpider.class);
        Entity.registerEntity("Stray", EntityStray.class);
        Entity.registerEntity("Vindicator", EntityVindicator.class);
        Entity.registerEntity("Vex", EntityVex.class);
        Entity.registerEntity("WitherSkeleton", EntityWitherSkeleton.class);
        Entity.registerEntity("Wither", EntityWither.class);
        Entity.registerEntity("Witch", EntityWitch.class);
        Entity.registerEntity("ZombiePigman", EntityZombiePigman.class);
        Entity.registerEntity("ZombieVillager", EntityZombieVillagerV1.class);
        Entity.registerEntity("Zombie", EntityZombie.class);
        Entity.registerEntity("Pillager", EntityPillager.class);
        Entity.registerEntity("ZombieVillagerV2", EntityZombieVillager.class);
        Entity.registerEntity("Hoglin", EntityHoglin.class);
        Entity.registerEntity("Piglin", EntityPiglin.class);
        Entity.registerEntity("Zoglin", EntityZoglin.class);
        Entity.registerEntity("PiglinBrute", EntityPiglinBrute.class);
        Entity.registerEntity("Warden", EntityWarden.class);
        //Passive
        Entity.registerEntity("Bat", EntityBat.class);
        Entity.registerEntity("Cat", EntityCat.class);
        Entity.registerEntity("Chicken", EntityChicken.class);
        Entity.registerEntity("Cod", EntityCod.class);
        Entity.registerEntity("Cow", EntityCow.class);
        Entity.registerEntity("Dolphin", EntityDolphin.class);
        Entity.registerEntity("Donkey", EntityDonkey.class);
        Entity.registerEntity("Horse", EntityHorse.class);
        Entity.registerEntity("IronGolem", EntityIronGolem.class);
        Entity.registerEntity("Llama", EntityLlama.class);
        Entity.registerEntity("Mooshroom", EntityMooshroom.class);
        Entity.registerEntity("Mule", EntityMule.class);
        Entity.registerEntity("Panda", EntityPanda.class);
        Entity.registerEntity("Parrot", EntityParrot.class);
        Entity.registerEntity("PolarBear", EntityPolarBear.class);
        Entity.registerEntity("Pig", EntityPig.class);
        Entity.registerEntity("Pufferfish", EntityPufferfish.class);
        Entity.registerEntity("Rabbit", EntityRabbit.class);
        Entity.registerEntity("Salmon", EntitySalmon.class);
        Entity.registerEntity("Sheep", EntitySheep.class);
        Entity.registerEntity("Squid", EntitySquid.class);
        Entity.registerEntity("SnowGolem", EntitySnowGolem.class);
        Entity.registerEntity("TropicalFish", EntityTropicalFish.class);
        Entity.registerEntity("Turtle", EntityTurtle.class);
        Entity.registerEntity("Wolf", EntityWolf.class);
        Entity.registerEntity("Ocelot", EntityOcelot.class);
        Entity.registerEntity("Villager", EntityVillagerV1.class);
        Entity.registerEntity("ZombieHorse", EntityZombieHorse.class);
        Entity.registerEntity("WanderingTrader", EntityWanderingTrader.class);
        Entity.registerEntity("VillagerV2", EntityVillager.class);
        Entity.registerEntity("Fox", EntityFox.class);
        Entity.registerEntity("Bee", EntityBee.class);
        Entity.registerEntity("Strider", EntityStrider.class);
        Entity.registerEntity("Goat", EntityGoat.class);
        Entity.registerEntity("Axolotl", EntityAxolotl.class);
        Entity.registerEntity("GlowSquid", EntityGlowSquid.class);
        Entity.registerEntity("Allay", EntityAllay.class);
        Entity.registerEntity("Frog", EntityFrog.class);
        Entity.registerEntity("Tadpole", EntityTadpole.class);
        Entity.registerEntity("Camel", EntityCamel.class);
        //Vehicles
        Entity.registerEntity("MinecartRideable", EntityMinecartEmpty.class);
        Entity.registerEntity("MinecartChest", EntityMinecartChest.class);
        Entity.registerEntity("MinecartHopper", EntityMinecartHopper.class);
        Entity.registerEntity("MinecartTnt", EntityMinecartTNT.class);
        Entity.registerEntity("Boat", EntityBoat.class);
        Entity.registerEntity("ChestBoat", EntityChestBoat.class);
        //Others
        Entity.registerEntity("Human", EntityHuman.class, true);
        Entity.registerEntity("Lightning", EntityLightning.class);
    }

    /**
     * Internal method to register all default block entities
     */
    private static void registerBlockEntities() {
        BlockEntity.registerBlockEntity(BlockEntity.FURNACE, BlockEntityFurnace.class);
        BlockEntity.registerBlockEntity(BlockEntity.CHEST, BlockEntityChest.class);
        BlockEntity.registerBlockEntity(BlockEntity.SIGN, BlockEntitySign.class);
        BlockEntity.registerBlockEntity(BlockEntity.ENCHANT_TABLE, BlockEntityEnchantTable.class);
        BlockEntity.registerBlockEntity(BlockEntity.SKULL, BlockEntitySkull.class);
        BlockEntity.registerBlockEntity(BlockEntity.FLOWER_POT, BlockEntityFlowerPot.class);
        BlockEntity.registerBlockEntity(BlockEntity.BREWING_STAND, BlockEntityBrewingStand.class);
        BlockEntity.registerBlockEntity(BlockEntity.ITEM_FRAME, BlockEntityItemFrame.class);
        BlockEntity.registerBlockEntity(BlockEntity.CAULDRON, BlockEntityCauldron.class);
        BlockEntity.registerBlockEntity(BlockEntity.ENDER_CHEST, BlockEntityEnderChest.class);
        BlockEntity.registerBlockEntity(BlockEntity.BEACON, BlockEntityBeacon.class);
        BlockEntity.registerBlockEntity(BlockEntity.PISTON_ARM, BlockEntityPistonArm.class);
        BlockEntity.registerBlockEntity(BlockEntity.COMPARATOR, BlockEntityComparator.class);
        BlockEntity.registerBlockEntity(BlockEntity.HOPPER, BlockEntityHopper.class);
        BlockEntity.registerBlockEntity(BlockEntity.BED, BlockEntityBed.class);
        BlockEntity.registerBlockEntity(BlockEntity.JUKEBOX, BlockEntityJukebox.class);
        BlockEntity.registerBlockEntity(BlockEntity.SHULKER_BOX, BlockEntityShulkerBox.class);
        BlockEntity.registerBlockEntity(BlockEntity.BANNER, BlockEntityBanner.class);
        BlockEntity.registerBlockEntity(BlockEntity.DROPPER, BlockEntityDropper.class);
        BlockEntity.registerBlockEntity(BlockEntity.DISPENSER, BlockEntityDispenser.class);
        BlockEntity.registerBlockEntity(BlockEntity.MOB_SPAWNER, BlockEntitySpawner.class);
        BlockEntity.registerBlockEntity(BlockEntity.MUSIC, BlockEntityMusic.class);
        BlockEntity.registerBlockEntity(BlockEntity.CAMPFIRE, BlockEntityCampfire.class);
        BlockEntity.registerBlockEntity(BlockEntity.BARREL, BlockEntityBarrel.class);
        BlockEntity.registerBlockEntity(BlockEntity.LECTERN, BlockEntityLectern.class);
        BlockEntity.registerBlockEntity(BlockEntity.BLAST_FURNACE, BlockEntityBlastFurnace.class);
        BlockEntity.registerBlockEntity(BlockEntity.SMOKER, BlockEntitySmoker.class);
        BlockEntity.registerBlockEntity(BlockEntity.BELL, BlockEntityBell.class);
        BlockEntity.registerBlockEntity(BlockEntity.PERSISTENT_CONTAINER, PersistentDataContainerBlockEntity.class);
    }

    /**
     * Is nether enabled on this server
     *
     * @return nether enabled
     */
    public boolean isNetherAllowed() {
        return this.netherEnabled;
    }

    /**
     * Get player data serializer that is used to save player data
     *
     * @return player data serializer
     */
    public PlayerDataSerializer getPlayerDataSerializer() {
        return playerDataSerializer;
    }

    /**
     * Set player data serializer that is used to save player data
     *
     * @param playerDataSerializer player data serializer
     */
    public void setPlayerDataSerializer(PlayerDataSerializer playerDataSerializer) {
        this.playerDataSerializer = Preconditions.checkNotNull(playerDataSerializer, "playerDataSerializer");
    }

    /**
     * Get the Server instance
     *
     * @return Server
     */
    public static Server getInstance() {
        return instance;
    }

    /**
     * Load server config
     */
    private void loadSettings() {
        /* nukkit.yml */

        this.forceLanguage = this.getConfig("settings.force-language", false);
        this.queryPlugins = this.getConfig("settings.query-plugins", true);

        this.networkCompressionThreshold = this.getConfig("network.batch-threshold", 256);
        this.networkCompressionLevel = Math.max(Math.min(this.getConfig("network.compression-level", 4), 9), 0);
        this.encryptionEnabled = this.getConfig("network.encryption", false);

        this.autoTickRate = this.getConfig("level-settings.auto-tick-rate", true);
        this.autoTickRateLimit = this.getConfig("level-settings.auto-tick-rate-limit", 20);
        this.baseTickRate = this.getConfig("level-settings.base-tick-rate", 1);
        this.alwaysTickPlayers = this.getConfig("level-settings.always-tick-players", false);

        this.useNativeLevelDB = this.getConfig("leveldb.use-native", false);
        this.levelDbCache = this.getConfig("leveldb.cache-size-mb", 80);

        this.autoSaveTicks = this.getConfig("ticks-per.autosave", 6000);

        this.shouldSavePlayerData = this.getConfig("player.save-player-data", true);
        this.skinChangeCooldown = this.getConfig("player.skin-change-cooldown", 15);
        this.attackStopSprint = this.getConfig("player.attack-stop-sprint", true);

        this.chunksPerTick = this.getConfig("chunk-sending.per-tick", 4);
        this.spawnThreshold = this.getConfig("spawn-threshold", 56);
        this.cacheChunks = this.getConfig("cache-chunks", false);

        this.spawnThresholdRadius = (int) Math.ceil(Math.sqrt(this.spawnThreshold));

        /* server.properties */

        this.maxPlayers = this.getPropertyInt("max-players", 20);
        this.netherEnabled = this.getPropertyBoolean("allow-nether", true);
        //this.endEnabled = this.getPropertyBoolean("allow-the-end", true);
        this.xboxAuth = this.getPropertyBoolean("xbox-auth", true);
        this.achievementsEnabled = this.getPropertyBoolean("achievements", true);
        this.pvpEnabled = this.getPropertyBoolean("pvp", true);
        this.announceAchievements = this.getPropertyBoolean("announce-player-achievements", true);
        this.allowFlight = this.getPropertyBoolean("allow-flight", false);
        this.isHardcore = this.getPropertyBoolean("hardcore", false);
        this.forceResources = this.getPropertyBoolean("force-resources", false);
        this.forceResourcesAllowOwnPacks = this.getPropertyBoolean("force-resources-allow-client-packs", false);
        this.whitelistEnabled = this.getPropertyBoolean("white-list", false);
        this.forceGamemode = this.getPropertyBoolean("force-gamemode", false);
        this.motd = this.getPropertyString("motd", "A Minecraft Server");
        this.viewDistance = this.getPropertyInt("view-distance", 10);
        this.port = this.getPropertyInt("server-port", 19132);
        this.ip = this.getPropertyString("server-ip", "0.0.0.0");
        this.spawnRadius = this.getPropertyInt("spawn-protection", 16);

        this.setAutoSave(this.getPropertyBoolean("auto-save", true));

        try {
            this.gamemode = this.getPropertyInt("gamemode", 0) & 0b11;
        } catch (NumberFormatException exception) {
            this.gamemode = getGamemodeFromString(this.getPropertyString("gamemode")) & 0b11;
        }

        if (this.isHardcore && this.difficulty < 3) {
            this.setDifficulty(3);
        } else {
            this.setDifficulty(getDifficultyFromString(this.getPropertyString("difficulty", "2")));
        }
    }

    /**
     * This class contains all default server.properties values.
     */
    private static class ServerProperties extends ConfigSection {
        {
            put("motd", "A Minecraft Server");
            put("sub-motd", "Powered by Nukkit");
            put("server-port", 19132);
            put("server-ip", "0.0.0.0");
            put("view-distance", 10);
            put("achievements", true);
            put("announce-player-achievements", true);
            put("spawn-protection", 16);
            put("gamemode", 0);
            put("force-gamemode", false);
            put("difficulty", 2);
            put("hardcore", false);
            put("pvp", true);
            put("white-list", false);
            put("generator-settings", "");
            put("level-name", "world");
            put("level-seed", "");
            put("level-type", "default");
            put("enable-rcon", false);
            put("rcon.password", Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13));
            put("force-resources", false);
            put("force-resources-allow-client-packs", false);
            put("xbox-auth", true);
            put("auto-save", true);
            put("force-language", false);
            put("enable-query", false);
            put("allow-flight", false);
            put("allow-nether", true);
            //put("allow-the-end", true);
        }
    }

    private class ConsoleThread extends Thread implements InterruptibleThread {

        @Override
        public void run() {
            console.start();
        }
    }
}
