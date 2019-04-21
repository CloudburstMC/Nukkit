package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.*;
import cn.nukkit.command.*;
import cn.nukkit.console.NukkitConsole;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.server.BatchPacketsEvent;
import cn.nukkit.event.server.PlayerDataSerializeEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
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
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Nether;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.CompressBatchedTask;
import cn.nukkit.network.Network;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.BatchPacket;
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
import co.aikar.timings.Timings;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author MagicDroidX
 * @author Box
 */
@Log4j2
public class Server {

    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";

    private static Server instance = null;

    private BanList banByName;

    private BanList banByIP;

    private Config operators;

    private Config whitelist;

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private boolean hasStopped = false;

    private PluginManager pluginManager;

    private int profilingTickrate = 20;

    private ServerScheduler scheduler;

    private int tickCounter;

    private long nextTick;

    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};

    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private float maxTick = 20;

    private float maxUse = 0;

    private int sendUsageTicker = 0;

    private boolean dispatchSignals = false;

    private final NukkitConsole console;
    private final ConsoleThread consoleThread;

    private SimpleCommandMap commandMap;

    private CraftingManager craftingManager;

    private ResourcePackManager resourcePackManager;

    private ConsoleCommandSender consoleSender;

    private int maxPlayers;

    private boolean autoSave = true;

    private RCON rcon;

    private EntityMetadataStore entityMetadata;

    private PlayerMetadataStore playerMetadata;

    private LevelMetadataStore levelMetadata;

    private Network network;

    private boolean networkCompressionAsync = true;
    public int networkCompressionLevel = 7;
    private int networkZlibProvider = 0;

    private boolean autoTickRate = true;
    private int autoTickRateLimit = 20;
    private boolean alwaysTickPlayers = false;
    private int baseTickRate = 1;
    private Boolean getAllowFlight = null;
    private int difficulty = Integer.MAX_VALUE;
    private int defaultGamemode = Integer.MAX_VALUE;

    private int autoSaveTicker = 0;
    private int autoSaveTicks = 6000;

    private BaseLang baseLang;

    private boolean forceLanguage = false;

    private UUID serverID;

    private final String filePath;
    private final String dataPath;
    private final String pluginPath;

    private final Set<UUID> uniquePlayers = new HashSet<>();

    private QueryHandler queryHandler;

    private QueryRegenerateEvent queryRegenerateEvent;

    private Config properties;
    private Config config;

    private final Map<String, Player> players = new HashMap<>();

    private final Map<UUID, Player> playerList = new HashMap<>();

    private final Map<Integer, String> identifier = new HashMap<>();

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

    private Level defaultLevel = null;

    private boolean allowNether;

    private final Thread currentThread;

    private Watchdog watchdog;

    private DB nameLookup;

    private PlayerDataSerializer playerDataSerializer = new DefaultPlayerDataSerializer(this);

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

        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";

        this.console = new NukkitConsole(this);
        this.consoleThread = new ConsoleThread();
        this.consoleThread.start();

        //todo: VersionString 现在不必要

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
                if(predefinedLanguage != null)  {
                    log.info("Trying to load language from predefined language: " + predefinedLanguage);
                    language = predefinedLanguage;
                }

                String lang = this.console.readLine();
                InputStream conf = this.getClass().getClassLoader().getResourceAsStream("lang/" + lang + "/lang.ini");
                if (conf != null) {
                    language = lang;
                } else if(predefinedLanguage != null) {
                    log.warn("No language found for predefined language: " + predefinedLanguage + ", please choose a valid language");
                }
                if(predefinedLanguage != null) {
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

        this.console.setExecutingCommands(true);

        log.info("Loading {} ...", TextFormat.GREEN + "nukkit.yml" + TextFormat.WHITE);
        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML);

        log.info("Loading {} ...", TextFormat.GREEN + "server properties" + TextFormat.WHITE);
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
            {
                put("motd", "A Nukkit Powered Server");
                put("sub-motd", "https://nukkitx.com");
                put("server-port", 19132);
                put("server-ip", "0.0.0.0");
                put("view-distance", 10);
                put("white-list", false);
                put("achievements", true);
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
                put("allow-nether", true);
                put("enable-query", true);
                put("enable-rcon", false);
                put("rcon.password", Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13));
                put("auto-save", true);
                put("force-resources", false);
                put("bug-report", true);
                put("xbox-auth", true);
            }
        });

        // Allow Nether? (determines if we create a nether world if one doesn't exist on startup)
        this.allowNether = this.properties.getBoolean("allow-nether", true);

        this.forceLanguage = this.getConfig("settings.force-language", false);
        this.baseLang = new BaseLang(this.getConfig("settings.language", BaseLang.FALLBACK_LANGUAGE));
        log.info(this.getLanguage().translateString("language.selected", new String[]{getLanguage().getName(), getLanguage().getLang()}));
        log.info(getLanguage().translateString("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.WHITE));

        Object poolSize = this.getConfig("settings.async-workers", (Object) "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors() + 1, 4);
            }
        }

        ServerScheduler.WORKERS = (int) poolSize;

        this.networkZlibProvider = this.getConfig("network.zlib-provider", 2);
        Zlib.setProvider(this.networkZlibProvider);

        this.networkCompressionLevel = this.getConfig("network.compression-level", 7);
        this.networkCompressionAsync = this.getConfig("network.async-compression", true);

        this.autoTickRate = this.getConfig("level-settings.auto-tick-rate", true);
        this.autoTickRateLimit = this.getConfig("level-settings.auto-tick-rate-limit", 20);
        this.alwaysTickPlayers = this.getConfig("level-settings.always-tick-players", false);
        this.baseTickRate = this.getConfig("level-settings.base-tick-rate", 1);

        this.scheduler = new ServerScheduler();

        if (this.getPropertyBoolean("enable-rcon", false)) {
            try {
                this.rcon = new RCON(this, this.getPropertyString("rcon.password", ""), (!this.getIp().equals("")) ? this.getIp() : "0.0.0.0", this.getPropertyInt("rcon.port", this.getPort()));
            } catch (IllegalArgumentException e) {
                log.error(getLanguage().translateString(e.getMessage(), e.getCause().getMessage()));
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

        this.maxPlayers = this.getPropertyInt("max-players", 20);
        this.setAutoSave(this.getPropertyBoolean("auto-save", true));

        if (this.getPropertyBoolean("hardcore", false) && this.getDifficulty() < 3) {
            this.setPropertyInt("difficulty", 3);
        }

        Nukkit.DEBUG = Math.max(this.getConfig("debug.level", 1), 1);

        int logLevel = (Nukkit.DEBUG + 3) * 100;
        for (org.apache.logging.log4j.Level level : org.apache.logging.log4j.Level.values()) {
            if (level.intLevel() == logLevel) {
                Nukkit.setLogLevel(level);
                break;
            }
        }

        if (this.getConfig().getBoolean("bug-report", true)) {
            ExceptionHandler.registerExceptionHandler();
        }

        log.info(this.getLanguage().translateString("nukkit.server.networkStart", new String[]{this.getIp().equals("") ? "*" : this.getIp(), String.valueOf(this.getPort())}));
        this.serverID = UUID.randomUUID();

        this.network = new Network(this);
        this.network.setName(this.getMotd());
        this.network.setSubName(this.getSubMotd());

        log.info(this.getLanguage().translateString("nukkit.server.info", this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.WHITE, TextFormat.AQUA + this.getCodename() + TextFormat.WHITE, this.getApiVersion()));
        log.info(this.getLanguage().translateString("nukkit.server.license", this.getName()));

        this.consoleSender = new ConsoleCommandSender();
        this.commandMap = new SimpleCommandMap(this);

        this.registerEntities();
        this.registerBlockEntities();

        Block.init();
        Enchantment.init();
        Item.init();
        EnumBiome.values(); //load class, this also registers biomes
        Effect.init();
        Potion.init();
        Attribute.init();
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0); //Force it to load

        // Convert legacy data before plugins get the chance to mess with it.
        try {
            nameLookup = Iq80DBFactory.factory.open(new File(dataPath, "players"), new Options()
                    .createIfMissing(true)
                    .compressionType(CompressionType.ZLIB_RAW));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        convertLegacyPlayerData();

        this.craftingManager = new CraftingManager();
        this.resourcePackManager = new ResourcePackManager(new File(Nukkit.DATA_PATH, "resource_packs"));

        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);

        this.pluginManager.registerInterface(JavaPluginLoader.class);

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);

        this.network.registerInterface(new RakNetInterface(this));

        this.pluginManager.loadPlugins(this.pluginPath);

        this.enablePlugins(PluginLoadOrder.STARTUP);

        LevelProviderManager.addProvider(this, Anvil.class);
        LevelProviderManager.addProvider(this, McRegion.class);
        LevelProviderManager.addProvider(this, LevelDB.class);

        Generator.addGenerator(Flat.class, "flat", Generator.TYPE_FLAT);
        Generator.addGenerator(Normal.class, "normal", Generator.TYPE_INFINITE);
        Generator.addGenerator(Normal.class, "default", Generator.TYPE_INFINITE);
        Generator.addGenerator(Nether.class, "nether", Generator.TYPE_NETHER);
        //todo: add old generator and hell generator

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
                    String preset = "";
                    for (int i = 1; i < opts.length; i++) {
                        preset += opts[i] + ":";
                    }
                    preset = preset.substring(0, preset.length() - 1);

                    options.put("preset", preset);
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
                    seed = Long.valueOf(seedString);
                } catch (NumberFormatException e) {
                    seed = seedString.hashCode();
                }
                this.generateLevel(defaultName, seed == 0 ? System.currentTimeMillis() : seed);
            }

            this.setDefaultLevel(this.getLevelByName(defaultName));
        }

        this.properties.save(true);

        if (this.getDefaultLevel() == null) {
            this.getLogger().emergency(this.getLanguage().translateString("nukkit.level.defaultError"));
            this.forceShutdown();

            return;
        }

        EnumLevel.initLevels();

        if (this.getConfig("ticks-per.autosave", 6000) > 0) {
            this.autoSaveTicks = this.getConfig("ticks-per.autosave", 6000);
        }

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        if (Nukkit.DEBUG < 2) {
            this.watchdog = new Watchdog(this, 60000);
            this.watchdog.start();
        }

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
        broadcastPacket(players.toArray(new Player[0]), packet);
    }

    public static void broadcastPacket(Player[] players, DataPacket packet) {
        packet.encode();
        packet.isEncoded = true;

        if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
            for (Player player : players) {
                player.dataPacket(packet);
            }
        } else {
            getInstance().batchPackets(players, new DataPacket[]{packet}, true);
        }

        if (packet.encapsulatedPacket != null) {
            packet.encapsulatedPacket = null;
        }
    }

    public void batchPackets(Player[] players, DataPacket[] packets) {
        this.batchPackets(players, packets, false);
    }

    public void batchPackets(Player[] players, DataPacket[] packets, boolean forceSync) {
        if (players == null || packets == null || players.length == 0 || packets.length == 0) {
            return;
        }

        BatchPacketsEvent ev = new BatchPacketsEvent(players, packets, forceSync);
        getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        Timings.playerNetworkSendTimer.startTiming();
        byte[][] payload = new byte[packets.length * 2][];
        int size = 0;
        for (int i = 0; i < packets.length; i++) {
            DataPacket p = packets[i];
            if (!p.isEncoded) {
                p.encode();
            }
            byte[] buf = p.getBuffer();
            payload[i * 2] = Binary.writeUnsignedVarInt(buf.length);
            payload[i * 2 + 1] = buf;
            packets[i] = null;
            size += payload[i * 2].length;
            size += payload[i * 2 + 1].length;
        }

        List<String> targets = new ArrayList<>();
        for (Player p : players) {
            if (p.isConnected()) {
                targets.add(this.identifier.get(p.rawHashCode()));
            }
        }

        if (!forceSync && this.networkCompressionAsync) {
            this.getScheduler().scheduleAsyncTask(new CompressBatchedTask(payload, targets, this.networkCompressionLevel));
        } else {
            try {
                byte[] data = Binary.appendBytes(payload);
                this.broadcastPacketsCallback(Zlib.deflate(data, this.networkCompressionLevel), targets);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Timings.playerNetworkSendTimer.stopTiming();
    }

    public void broadcastPacketsCallback(byte[] data, List<String> identifiers) {
        BatchPacket pk = new BatchPacket();
        pk.payload = data;

        for (String i : identifiers) {
            if (this.players.containsKey(i)) {
                this.players.get(i).dataPacket(pk);
            }
        }
    }

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

    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    public void disablePlugins() {
        this.pluginManager.disablePlugins();
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) throws ServerException {
        // First we need to check if this command is on the main thread or not, if not, warn the user
        if (!this.isPrimaryThread()) {
            getLogger().warning("Command Dispatched Async: " + commandLine);
            getLogger().warning("Please notify author of plugin causing this execution to fix this bug!", new Throwable());
            // TODO: We should sync the command to the main thread too!
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

    //todo: use ticker to check console
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    public void reload() {
        log.info("Reloading...");

        log.info("Saving levels...");

        for (Level level : this.levelArray) {
            level.save();
        }

        this.pluginManager.disablePlugins();
        this.pluginManager.clearPlugins();
        this.commandMap.clearCommands();

        log.info("Reloading properties...");
        this.properties.reload();
        this.maxPlayers = this.getPropertyInt("max-players", 20);

        if (this.getPropertyBoolean("hardcore", false) && this.getDifficulty() < 3) {
            this.setPropertyInt("difficulty", difficulty = 3);
        }

        this.banByIP.load();
        this.banByName.load();
        this.reloadWhitelist();
        this.operators.reload();

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            this.getNetwork().blockAddress(entry.getName(), -1);
        }

        this.pluginManager.registerInterface(JavaPluginLoader.class);
        this.pluginManager.loadPlugins(this.pluginPath);
        this.enablePlugins(PluginLoadOrder.STARTUP);
        this.enablePlugins(PluginLoadOrder.POSTWORLD);
        Timings.reset();
    }

    public void shutdown() {
        isRunning.compareAndSet(true, false);
    }

    public void forceShutdown() {
        if (this.hasStopped) {
            return;
        }

        try {
            isRunning.compareAndSet(true, false);

            this.hasStopped = true;

            if (this.rcon != null) {
                this.rcon.close();
            }

            this.getLogger().debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            for (Player player : new ArrayList<>(this.players.values())) {
                player.close(player.getLeaveMessage(), this.getConfig("settings.shutdown-message", "Server closed"));
            }

            this.getLogger().debug("Removing event handlers");
            HandlerList.unregisterAll();

            this.getLogger().debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            this.getLogger().debug("Unloading all levels");
            for (Level level : this.levelArray) {
                this.unloadLevel(level, true);
            }

            this.getLogger().debug("Closing console");
            this.consoleThread.interrupt();

            this.getLogger().debug("Stopping network interfaces");
            for (SourceInterface interfaz : this.network.getInterfaces()) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }

            if (nameLookup != null) {
                nameLookup.close();
            }

            this.getLogger().debug("Disabling timings");
            Timings.stopServer();
            if (this.watchdog != null) {
                this.watchdog.kill();
            }
            //todo other things
        } catch (Exception e) {
            log.fatal("Exception happened while shutting down, exiting the process", e);
            System.exit(1);
        }
    }

    public void start() {
        if (this.getPropertyBoolean("enable-query", true)) {
            this.queryHandler = new QueryHandler();
        }

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            this.network.blockAddress(entry.getName(), -1);
        }

        //todo send usage setting
        this.tickCounter = 0;

        log.info(this.getLanguage().translateString("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));

        log.info(this.getLanguage().translateString("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)));

        this.tickProcessor();
        this.forceShutdown();
    }

    public void handlePacket(String address, int port, byte[] payload) {
        try {
            if (payload.length > 2 && Arrays.equals(Binary.subBytes(payload, 0, 2), new byte[]{(byte) 0xfe, (byte) 0xfd}) && this.queryHandler != null) {
                this.queryHandler.handle(address, port, payload);
            }
        } catch (Exception e) {
            log.error("Error whilst handling packet", e);

            this.getNetwork().blockAddress(address, 600);
        }
    }

    private int lastLevelGC;

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

                        { // Instead of wasting time, do something potentially useful
                            int offset = 0;
                            for (int i = 0; i < levelArray.length; i++) {
                                offset = (i + lastLevelGC) % levelArray.length;
                                Level level = levelArray[offset];
                                level.doGarbageCollection(allocated - 1);
                                allocated = next - System.currentTimeMillis();
                                if (allocated <= 0) {
                                    break;
                                }
                            }
                            lastLevelGC = offset + 1;
                        }

                        if (allocated > 0) {
                            Thread.sleep(allocated, 900000);
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
        this.sendFullPlayerListData(player);
    }

    public void onPlayerLogin(Player player) {
        if (this.sendUsageTicker > 0) {
            this.uniquePlayers.add(player.getUniqueId());
        }
    }

    public void addPlayer(String identifier, Player player) {
        this.players.put(identifier, player);
        this.identifier.put(player.rawHashCode(), identifier);
    }

    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID());
    }

    public void removeOnlinePlayer(Player player) {
        if (this.playerList.containsKey(player.getUniqueId())) {
            this.playerList.remove(player.getUniqueId());

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
        Server.broadcastPacket(players, pk);
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Collection<Player> players) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId,
                players.stream()
                        .filter(p -> !p.getUniqueId().equals(uuid))
                        .toArray(Player[]::new));
    }

    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        Server.broadcastPacket(players, pk);
    }

    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.toArray(new Player[0]));
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

    private void checkTickUpdates(int currentTick, long tickTime) {
        for (Player p : new ArrayList<>(this.players.values())) {
            /*if (!p.loggedIn && (tickTime - p.creationTime) >= 10000 && p.kick(PlayerKickEvent.Reason.LOGIN_TIMEOUT, "Login timeout")) {
                continue;
            }

            client freezes when applying resource packs
            todo: fix*/

            if (this.alwaysTickPlayers) {
                p.onUpdate(currentTick);
            }
        }

        //Do level ticks
        for (Level level : this.levelArray) {
            if (level.getTickRate() > this.baseTickRate && --level.tickRateCounter > 0) {
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
                            this.getLogger().debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        } else if ((tickMs / level.getTickRate()) >= 50 && level.getTickRate() < this.autoTickRateLimit) {
                            level.setTickRate(level.getTickRate() + 1);
                            this.getLogger().debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        }
                        level.tickRateCounter = level.getTickRate();
                    }
                }
            } catch (Exception e) {
                log.error(this.getLanguage().translateString("nukkit.level.tickError"), e);
            }
        }
    }

    public void doAutoSave() {
        if (this.getAutoSave()) {
            Timings.levelSaveTimer.startTiming();
            for (Player player : new ArrayList<>(this.players.values())) {
                if (player.isOnline()) {
                    player.save(true);
                } else if (!player.isConnected()) {
                    this.removePlayer(player);
                }
            }

            for (Level level : this.levelArray) {
                level.save();
            }
            Timings.levelSaveTimer.stopTiming();
        }
    }

    private boolean tick() {
        long tickTime = System.currentTimeMillis();

        // TODO
        long time = tickTime - this.nextTick;
        if (time < -25) {
            try {
                Thread.sleep(Math.max(5, -time - 25));
            } catch (InterruptedException e) {
                Server.getInstance().getLogger().logException(e);
            }
        }

        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -25) {
            return false;
        }

        Timings.fullServerTickTimer.startTiming();

        ++this.tickCounter;

        Timings.connectionTimer.startTiming();
        this.network.processInterfaces();

        if (this.rcon != null) {
            this.rcon.check();
        }
        Timings.connectionTimer.stopTiming();

        Timings.schedulerTimer.startTiming();
        this.scheduler.mainThreadHeartbeat(this.tickCounter);
        Timings.schedulerTimer.stopTiming();

        this.checkTickUpdates(this.tickCounter, tickTime);

        for (Player player : new ArrayList<>(this.players.values())) {
            player.checkNetwork();
        }

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.network.resetStatistics();
            this.maxTick = 20;
            this.maxUse = 0;

            if ((this.tickCounter & 0b111111111) == 0) {
                try {
                    this.getPluginManager().callEvent(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                    if (this.queryHandler != null) {
                        this.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }

            this.getNetwork().updateName();
        }

        if (this.autoSave && ++this.autoSaveTicker >= this.autoSaveTicks) {
            this.autoSaveTicker = 0;
            this.doAutoSave();
        }

        if (this.sendUsageTicker > 0 && --this.sendUsageTicker == 0) {
            this.sendUsageTicker = 6000;
            //todo sendUsage
        }

        if (this.tickCounter % 100 == 0) {
            for (Level level : this.levelArray) {
                level.doChunkGarbageCollection();
            }
        }

        Timings.fullServerTickTimer.stopTiming();
        //long now = System.currentTimeMillis();
        long nowNano = System.nanoTime();
        //float tick = Math.min(20, 1000 / Math.max(1, now - tickTime));
        //float use = Math.min(1, (now - tickTime) / 50);

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

        return true;
    }

    public long getNextTick() {
        return nextTick;
    }

    // TODO: Fix title tick
    public void titleTick() {
        if (!Nukkit.ANSI || !Nukkit.TITLE) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        double used = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double max = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        String usage = Math.round(used / max * 100) + "%";
        String title = (char) 0x1b + "]0;" + this.getName() + " "
                + this.getNukkitVersion()
                + " | Online " + this.players.size() + "/" + this.getMaxPlayers()
                + " | Memory " + usage;
        if (!Nukkit.shortTitle) {
            title += " | U " + NukkitMath.round((this.network.getUpload() / 1024 * 1000), 2)
                    + " D " + NukkitMath.round((this.network.getDownload() / 1024 * 1000), 2) + " kB/s";
        }
        title += " | TPS " + this.getTicksPerSecond()
                + " | Load " + this.getTickUsage() + "%" + (char) 0x07;

        System.out.print(title);
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
        return Nukkit.CODENAME;
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

    public int getPort() {
        return this.getPropertyInt("server-port", 19132);
    }

    public int getViewDistance() {
        return this.getPropertyInt("view-distance", 10);
    }

    public String getIp() {
        return this.getPropertyString("server-ip", "0.0.0.0");
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
        if (this.difficulty == Integer.MAX_VALUE) {
            this.difficulty = this.getPropertyInt("difficulty", 1);
        }
        return this.difficulty;
    }

    public boolean hasWhitelist() {
        return this.getPropertyBoolean("white-list", false);
    }

    public int getSpawnRadius() {
        return this.getPropertyInt("spawn-protection", 16);
    }

    public boolean getAllowFlight() {
        if (getAllowFlight == null) {
            getAllowFlight = this.getPropertyBoolean("allow-flight", false);
        }
        return getAllowFlight;
    }

    public boolean isHardcore() {
        return this.getPropertyBoolean("hardcore", false);
    }

    public int getDefaultGamemode() {
        if (this.defaultGamemode == Integer.MAX_VALUE) {
            this.defaultGamemode = this.getPropertyInt("gamemode", 0);
        }
        return this.defaultGamemode;
    }

    public String getMotd() {
        return this.getPropertyString("motd", "A Nukkit Powered Server");
    }

    public String getSubMotd() {
        return this.getPropertyString("sub-motd", "https://nukkitx.com");
    }

    public boolean getForceResources() {
        return this.getPropertyBoolean("force-resources", false);
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
        return (float) NukkitMath.round(sum / count, 2);
    }

    public float getTickUsage() {
        return (float) NukkitMath.round(this.maxUse * 100, 2);
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

    public Map<UUID, Player> getOnlinePlayers() {
        return ImmutableMap.copyOf(playerList);
    }

    public void addRecipe(Recipe recipe) {
        this.craftingManager.registerRecipe(recipe);
    }

    public Optional<Player> getPlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return Optional.ofNullable(playerList.get(uuid));
    }

    public Optional<UUID> lookupName(String name) {
        byte[] nameBytes = name.toLowerCase().getBytes(StandardCharsets.UTF_8);
        byte[] uuidBytes = nameLookup.get(nameBytes);
        if (uuidBytes == null) {
            return Optional.empty();
        }

        if (uuidBytes.length != 16) {
            log.warn("Invalid uuid in name lookup database detected! Removing");
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
        IPlayer result = this.getPlayerExact(name.toLowerCase());
        if (result != null) {
            return result;
        }

        return lookupName(name).map(uuid -> new OfflinePlayer(this, uuid))
                .orElse(new OfflinePlayer(this, name));
    }

    public IPlayer getOfflinePlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        Optional<Player> onlinePlayer = getPlayer(uuid);
        //noinspection OptionalIsPresent
        if (onlinePlayer.isPresent()) {
            return onlinePlayer.get();
        }

        return new OfflinePlayer(this, uuid);
    }

    public CompoundTag getOfflinePlayerData(UUID uuid) {
        return getOfflinePlayerDataInternal(uuid.toString(), true);
    }

    @Deprecated
    public CompoundTag getOfflinePlayerData(String name) {
        Optional<UUID> uuid = lookupName(name);
        return getOfflinePlayerDataInternal(uuid.map(UUID::toString).orElse(name), true);
    }

    private CompoundTag getOfflinePlayerDataInternal(String name, boolean runEvent) {
        Preconditions.checkNotNull(name, "name");

        PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(name, playerDataSerializer);
        if (runEvent) {
            pluginManager.callEvent(event);
        }

        Optional<InputStream> dataStream = Optional.empty();
        try {
            dataStream = event.getSerializer().read(name, event.getUuid().orElse(null));
            if (dataStream.isPresent()) {
                return NBTIO.readCompressed(dataStream.get());
            } else {
                log.warn(this.getLanguage().translateString("nukkit.data.playerNotFound", name));
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

        Position spawn = this.getDefaultLevel().getSafeSpawn();
        CompoundTag nbt = new CompoundTag()
                .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                .putLong("lastPlayed", System.currentTimeMillis() / 1000)
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
                .putShort("Air", 300)
                .putBoolean("OnGround", true)
                .putBoolean("Invulnerable", false);

        this.saveOfflinePlayerData(name, nbt, true, runEvent);
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
        String nameLower = name.toLowerCase();
        if (this.shouldSavePlayerData()) {
            PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(nameLower, playerDataSerializer);
            if (runEvent) {
                pluginManager.callEvent(event);
            }

            this.getScheduler().scheduleTask(new Task() {
                boolean hasRun = false;

                @Override
                public void onRun(int currentTick) {
                    this.onCancel();
                }

                //doing it like this ensures that the playerdata will be saved in a server shutdown
                @Override
                public void onCancel() {
                    if (!this.hasRun)    {
                        this.hasRun = true;
                        saveOfflinePlayerDataInternal(event.getSerializer(), tag, nameLower, event.getUuid().orElse(null));
                    }
                }
            }, async);
        }
    }

    private void saveOfflinePlayerDataInternal(PlayerDataSerializer serializer, CompoundTag tag, String name, UUID uuid) {
        try (OutputStream dataStream = serializer.write(name, uuid)) {
            NBTIO.writeGZIPCompressed(tag, dataStream, ByteOrder.BIG_ENDIAN);
        } catch (Exception e) {
            log.error(this.getLanguage().translateString("nukkit.data.saveError", name, e));
        }
    }

    private void convertLegacyPlayerData() {
        File dataDirectory = new File(getDataPath(), "players/");
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}.dat$");

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

            CompoundTag tag = getOfflinePlayerDataInternal(name, false);

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

        return matchedPlayer.toArray(new Player[0]);
    }

    public void removePlayer(Player player) {
        if (this.identifier.containsKey(player.rawHashCode())) {
            String identifier = this.identifier.get(player.rawHashCode());
            this.players.remove(identifier);
            this.identifier.remove(player.rawHashCode());
            return;
        }

        for (String identifier : new ArrayList<>(this.players.keySet())) {
            Player p = this.players.get(identifier);
            if (player == p) {
                this.players.remove(identifier);
                this.identifier.remove(player.rawHashCode());
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
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getFolderName()) && defaultLevel != this.defaultLevel)) {
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
        for (Level level : this.levelArray) {
            if (level.getFolderName().equals(name)) {
                return level;
            }
        }

        return null;
    }

    public boolean unloadLevel(Level level) {
        return this.unloadLevel(level, false);
    }

    public boolean unloadLevel(Level level, boolean forceUnload) {
        if (level == this.getDefaultLevel() && !forceUnload) {
            throw new IllegalStateException("The default level cannot be unloaded while running, please switch levels.");
        }

        return level.unload(forceUnload);

    }

    public boolean loadLevel(String name) {
        if (Objects.equals(name.trim(), "")) {
            throw new LevelException("Invalid empty level name");
        }
        if (this.isLevelLoaded(name)) {
            return true;
        } else if (!this.isLevelGenerated(name)) {
            log.warn(this.getLanguage().translateString("nukkit.level.notFound", name));

            return false;
        }

        String path;

        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        Class<? extends LevelProvider> provider = LevelProviderManager.getProvider(path);

        if (provider == null) {
            log.error(this.getLanguage().translateString("nukkit.level.loadError", new String[]{name, "Unknown provider"}));

            return false;
        }

        Level level;
        try {
            level = new Level(this, name, path, provider);
        } catch (Exception e) {
            log.error(this.getLanguage().translateString("nukkit.level.loadError", new String[]{name, e.getMessage()}));
            return false;
        }

        this.levels.put(level.getId(), level);

        level.initLevel();

        this.getPluginManager().callEvent(new LevelLoadEvent(level));

        level.setTickRate(this.baseTickRate);

        return true;
    }

    public boolean generateLevel(String name) {
        return this.generateLevel(name, new java.util.Random().nextLong());
    }

    public boolean generateLevel(String name, long seed) {
        return this.generateLevel(name, seed, null);
    }

    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator) {
        return this.generateLevel(name, seed, generator, new HashMap<>());
    }

    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options) {
        return generateLevel(name, seed, generator, options, null);
    }

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
            provider = LevelProviderManager.getProviderByName(this.getConfig().get("level-settings.default-format", "anvil"));
        }

        String path;

        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        Level level;
        try {
            provider.getMethod("generate", String.class, String.class, long.class, Class.class, Map.class).invoke(null, path, name, seed, generator, options);

            level = new Level(this, name, path, provider);
            this.levels.put(level.getId(), level);

            level.initLevel();
            level.setTickRate(this.baseTickRate);
        } catch (Exception e) {
            log.error(this.getLanguage().translateString("nukkit.level.generationError", new String[]{name, e.getMessage()}));
            return false;
        }

        this.getPluginManager().callEvent(new LevelInitEvent(level));

        this.getPluginManager().callEvent(new LevelLoadEvent(level));

        /*this.getLogger().notice(this.getLanguage().translateString("nukkit.level.backgroundGeneration", name));

        int centerX = (int) level.getSpawnLocation().getX() >> 4;
        int centerZ = (int) level.getSpawnLocation().getZ() >> 4;

        TreeMap<String, Integer> order = new TreeMap<>();

        for (int X = -3; X <= 3; ++X) {
            for (int Z = -3; Z <= 3; ++Z) {
                int distance = X * X + Z * Z;
                int chunkX = X + centerX;
                int chunkZ = Z + centerZ;
                order.put(Level.chunkHash(chunkX, chunkZ), distance);
            }
        }

        List<Map.Entry<String, Integer>> sortList = new ArrayList<>(order.entrySet());

        Collections.sort(sortList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (String index : order.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            level.populateChunk(entry.chunkX, entry.chunkZ, true);
        }*/
        return true;
    }

    public boolean isLevelGenerated(String name) {
        if (Objects.equals(name.trim(), "")) {
            return false;
        }

        String path = this.getDataPath() + "worlds/" + name + "/";
        if (this.getLevelByName(name) == null) {

            return LevelProviderManager.getProvider(path) != null;
        }

        return true;
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

    //Revising later...
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

    public Config getProperties() {
        return this.properties;
    }

    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    public Object getProperty(String variable, Object defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
    }

    public void setPropertyString(String variable, String value) {
        this.properties.set(variable, value);
        this.properties.save();
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
        return this.properties.exists(variable) ? (!this.properties.get(variable).equals("") ? Integer.parseInt(String.valueOf(this.properties.get(variable))) : defaultValue) : defaultValue;
    }

    public void setPropertyInt(String variable, int value) {
        this.properties.set(variable, value);
        this.properties.save();
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
        this.properties.save();
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

    public void addOp(String name) {
        this.operators.set(name.toLowerCase(), true);
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
        }
        this.operators.save(true);
    }

    public void removeOp(String name) {
        this.operators.remove(name.toLowerCase());
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
        }
        this.operators.save();
    }

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

    public ServiceManager getServiceManager() {
        return serviceManager;
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
                    commands.addAll((List<String>) value);
                } else {
                    commands.add((String) value);
                }

                result.put(key, commands);
            }
        }

        return result;

    }

    public boolean shouldSavePlayerData() {
        return this.getConfig("player.save-player-data", true);
    }

    /**
     * Checks the current thread against the expected primary thread for the
     * server.
     * <p>
     * <b>Note:</b> this method should not be used to indicate the current
     * synchronized state of the runtime. A current thread matching the main
     * thread indicates that it is synchronized, but a mismatch does not
     * preclude the same assumption.
     *
     * @return true if the current thread matches the expected primary thread,
     * false otherwise
     */
    public final boolean isPrimaryThread() {
        return (Thread.currentThread() == currentThread);
    }

    public Thread getPrimaryThread() {
        return currentThread;
    }

    private void registerEntities() {
        Entity.registerEntity("Arrow", EntityArrow.class);
        Entity.registerEntity("Item", EntityItem.class);
        Entity.registerEntity("FallingSand", EntityFallingBlock.class);
        Entity.registerEntity("PrimedTnt", EntityPrimedTNT.class);
        Entity.registerEntity("Snowball", EntitySnowball.class);
        Entity.registerEntity("EnderPearl", EntityEnderPearl.class);
        Entity.registerEntity("Painting", EntityPainting.class);
        //Monsters
        Entity.registerEntity("Creeper", EntityCreeper.class);
        Entity.registerEntity("Blaze", EntityBlaze.class);
        Entity.registerEntity("CaveSpider", EntityCaveSpider.class);
        Entity.registerEntity("Cod", EntityCod.class);
        Entity.registerEntity("Drowned", EntityDrowned.class);
        Entity.registerEntity("ElderGuardian", EntityElderGuardian.class);
        Entity.registerEntity("EnderDragon", EntityEnderDragon.class);
        Entity.registerEntity("Enderman", EntityEnderman.class);
        Entity.registerEntity("Endermite", EntityEndermite.class);
        Entity.registerEntity("Evoker", EntityEvoker.class);
        Entity.registerEntity("Firework", EntityFirework.class);
        Entity.registerEntity("Ghast", EntityGhast.class);
        Entity.registerEntity("Guardian", EntityGuardian.class);
        Entity.registerEntity("Husk", EntityHusk.class);
        Entity.registerEntity("MagmaCube", EntityMagmaCube.class);
        Entity.registerEntity("Phantom", EntityPhantom.class);
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
        Entity.registerEntity("ZombieVillager", EntityZombieVillager.class);
        Entity.registerEntity("Zombie", EntityZombie.class);

        //Passive
        Entity.registerEntity("Bat", EntityBat.class);
        Entity.registerEntity("Cat", EntityCat.class);
        Entity.registerEntity("Chicken", EntityChicken.class);
        Entity.registerEntity("Cow", EntityCow.class);
        Entity.registerEntity("Dolphin", EntityDolphin.class);
        Entity.registerEntity("Donkey", EntityDonkey.class);
        Entity.registerEntity("Horse", EntityHorse.class);
        Entity.registerEntity("Llama", EntityLlama.class);
        Entity.registerEntity("Mooshroom", EntityMooshroom.class);
        Entity.registerEntity("Mule", EntityMule.class);
        Entity.registerEntity("Panda", EntityPanda.class);
        Entity.registerEntity("PolarBear", EntityPolarBear.class);
        Entity.registerEntity("Pig", EntityPig.class);
        Entity.registerEntity("Rabbit", EntityRabbit.class);
        Entity.registerEntity("Sheep", EntitySheep.class);
        Entity.registerEntity("Squid", EntitySquid.class);
        Entity.registerEntity("Wolf", EntityWolf.class);
        Entity.registerEntity("Ocelot", EntityOcelot.class);
        Entity.registerEntity("Pufferfish", EntityPufferfish.class);
        Entity.registerEntity("Salmon", EntitySalmon.class);
        Entity.registerEntity("TropicalFish", EntityTropicalFish.class);
        Entity.registerEntity("Turtle", EntityTurtle.class);
        Entity.registerEntity("Villager", EntityVillager.class);

        Entity.registerEntity("ThrownExpBottle", EntityExpBottle.class);
        Entity.registerEntity("XpOrb", EntityXPOrb.class);
        Entity.registerEntity("ThrownPotion", EntityPotion.class);
        Entity.registerEntity("Egg", EntityEgg.class);
        Entity.registerEntity("ThrownTrident", EntityThrownTrident.class);

        Entity.registerEntity("Human", EntityHuman.class, true);

        Entity.registerEntity("MinecartRideable", EntityMinecartEmpty.class);
        Entity.registerEntity("MinecartChest", EntityMinecartChest.class);
        Entity.registerEntity("MinecartHopper", EntityMinecartHopper.class);
        Entity.registerEntity("MinecartTnt", EntityMinecartTNT.class);
        Entity.registerEntity("Boat", EntityBoat.class);

        Entity.registerEntity("EndCrystal", EntityEndCrystal.class);
        Entity.registerEntity("FishingHook", EntityFishingHook.class);
    }

    private void registerBlockEntities() {
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
        BlockEntity.registerBlockEntity(BlockEntity.MUSIC, BlockEntityMusic.class);
    }

    public boolean isNetherAllowed() {
        return this.allowNether;
    }

    public PlayerDataSerializer getPlayerDataSerializer() {
        return playerDataSerializer;
    }

    public void setPlayerDataSerializer(PlayerDataSerializer playerDataSerializer) {
        this.playerDataSerializer = Preconditions.checkNotNull(playerDataSerializer, "playerDataSerializer");
    }

    public static Server getInstance() {
        return instance;
    }

    private class ConsoleThread extends Thread implements InterruptibleThread {

        @Override
        public void run() {
            console.start();
        }
    }
}
