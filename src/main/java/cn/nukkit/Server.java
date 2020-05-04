package cn.nukkit;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.console.NukkitConsole;
import cn.nukkit.entity.Attribute;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.server.BatchPacketsEvent;
import cn.nukkit.event.server.PlayerDataSerializeEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.event.server.RegistriesClosedEvent;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.*;
import cn.nukkit.level.storage.StorageIds;
import cn.nukkit.locale.LocaleManager;
import cn.nukkit.locale.TextContainer;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.network.BedrockInterface;
import cn.nukkit.network.Network;
import cn.nukkit.network.ProtocolInfo;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.query.QueryHandler;
import cn.nukkit.pack.PackManager;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.permission.DefaultPermissions;
import cn.nukkit.permission.Permissible;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.OfflinePlayer;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.JavaPluginLoader;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLoadOrder;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.plugin.service.NKServiceManager;
import cn.nukkit.plugin.service.ServiceManager;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.registry.*;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.*;
import cn.nukkit.utils.bugreport.ExceptionHandler;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.FloatTag;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.SerializedSkin;
import com.nukkitx.protocol.bedrock.packet.PlayerListPacket;
import com.spotify.futures.CompletableFutures;
import io.netty.buffer.ByteBuf;
import lombok.extern.log4j.Log4j2;
import net.daporkchop.ldbjni.LevelDB;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private boolean hasStopped = false;

    private PluginManager pluginManager;

    private final int profilingTickrate = 20;

    private ServerScheduler scheduler;

    private int tickCounter;

    private long nextTick;

    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};

    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private float maxTick = 20;

    private float maxUse = 0;

    private int sendUsageTicker = 0;

    private final boolean dispatchSignals = false;

    private final NukkitConsole console;
    private final ConsoleThread consoleThread;

    private CraftingManager craftingManager;

    private final PackManager packManager = new PackManager();

    private ConsoleCommandSender consoleSender;

    private int maxPlayers;

    private boolean autoSave = true;

    private EntityMetadataStore entityMetadata;

    private PlayerMetadataStore playerMetadata;

    private LevelMetadataStore levelMetadata;

    private Network network;

    private boolean networkCompressionAsync = true;
    public int networkCompressionLevel = 7;

    private boolean autoTickRate = true;
    private int autoTickRateLimit = 20;
    private boolean alwaysTickPlayers = false;
    private int baseTickRate = 1;
    private Boolean getAllowFlight = null;
    private int difficulty = Integer.MAX_VALUE;
    private int defaultGamemode = Integer.MAX_VALUE;

    private int autoSaveTicker = 0;
    private int autoSaveTicks = 6000;

    private boolean forceLanguage = false;

    private UUID serverID;

    private final LevelManager levelManager = new LevelManager(this);

    private final String filePath;
    private final String dataPath;
    private final String pluginPath;

    private final Set<UUID> uniquePlayers = new HashSet<>();

    private QueryHandler queryHandler;

    private QueryRegenerateEvent queryRegenerateEvent;
    private Config config;

    private final LocaleManager localeManager = LocaleManager.from("locale/nukkit/languages.json",
            "locale/nukkit/texts", "locale/vanilla");
    private final GameRuleRegistry gameRuleRegistry = GameRuleRegistry.get();
    private final GeneratorRegistry generatorRegistry = GeneratorRegistry.get();
    private final StorageRegistry storageRegistry = StorageRegistry.get();
    private final BlockRegistry blockRegistry = BlockRegistry.get();
    private final BlockEntityRegistry blockEntityRegistry = BlockEntityRegistry.get();
    private final ItemRegistry itemRegistry = ItemRegistry.get();
    private final EntityRegistry entityRegistry = EntityRegistry.get();
    private final BiomeRegistry biomeRegistry = BiomeRegistry.get();
    private final CommandRegistry commandRegistry = CommandRegistry.get();

    private final Map<InetSocketAddress, Player> players = new HashMap<>();

    private final Map<UUID, Player> playerList = new HashMap<>();
    private final LevelData defaultLevelData = new LevelData();
    private String predefinedLanguage;

    private final ServiceManager serviceManager = new NKServiceManager();

    private boolean allowNether;

    private final Thread currentThread;

    private Watchdog watchdog;

    private DB nameLookup;

    private PlayerDataSerializer playerDataSerializer = new DefaultPlayerDataSerializer(this);
    private Properties properties;
    private volatile Identifier defaultStorageId;

    private final Set<String> ignoredPackets = new HashSet<>();

    public Server(String filePath, String dataPath, String pluginPath, String predefinedLanguage) {
        Preconditions.checkState(instance == null, "Already initialized!");
        instance = this;
        currentThread = Thread.currentThread(); // Saves the current thread instance as a reference, used in Server#isPrimaryThread()
        this.filePath = filePath;
        this.dataPath = dataPath;
        this.pluginPath = pluginPath;
        this.predefinedLanguage = predefinedLanguage;

        this.console = new NukkitConsole(this);
        this.consoleThread = new ConsoleThread();
    }

    public static void broadcastPackets(Player[] players, BedrockPacket[] packets) {
        Server.getInstance().batchPackets(players, packets);
    }

    public static void broadcastPacket(Player[] players, BedrockPacket packet) {
        Server.getInstance().batchPackets(players, new BedrockPacket[]{packet});
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

    public static void broadcastPacket(Collection<Player> players, BedrockPacket packet) {
        broadcastPacket(players.toArray(new Player[0]), packet);
    }

    public void boot() throws IOException {
        // Create directories
        if (!new File(dataPath + "worlds/").exists()) {
            new File(dataPath + "worlds/").mkdirs();
        }

        if (!new File(dataPath + "players/").exists()) {
            new File(dataPath + "players/").mkdirs();
        }

        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }

        this.consoleThread.start();

        if (!new File(this.dataPath + "nukkit.yml").exists()) {
            log.info(TextFormat.GREEN + "Welcome! Please choose a language first!");

            for (Locale locale : localeManager.getAvailableLocales()) {
                log.info("{}: {}", locale.toString(), locale.getDisplayName(locale));
            }

            String locale;
            do {
                if (this.predefinedLanguage != null) {
                    locale = this.predefinedLanguage;
                    this.predefinedLanguage = null;
                } else {
                    locale = this.console.readLine();
                }
            } while (!localeManager.setLocale(locale));

            // Generate config with specified locale
            LocaleManager configLocaleManager = LocaleManager.from("locale/nukkit/languages.json",
                    "locale/nukkit/configs");
            configLocaleManager.setLocale(locale);

            File configFile = new File(this.dataPath + "nukkit.yml");
            InputStream stream = Nukkit.class.getClassLoader().getResourceAsStream("nukkit.yml");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        line = configLocaleManager.translate(line);
                    }
                    writer.write(line + '\n');
                }
            }
        }

        this.console.setExecutingCommands(true);

        log.info("Loading {} ...", TextFormat.GREEN + "nukkit.yml" + TextFormat.WHITE);
        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML);

        ignoredPackets.addAll(getConfig().getStringList("debug.ignored-packets"));

        Nukkit.DEBUG = Math.max(this.getConfig("debug.level", 1), 1);

        int logLevel = (Nukkit.DEBUG + 3) * 100;
        for (org.apache.logging.log4j.Level level : org.apache.logging.log4j.Level.values()) {
            if (level.intLevel() == logLevel) {
                if (level.intLevel() > Nukkit.getLogLevel().intLevel()) {
                    Nukkit.setLogLevel(level);
                }
                break;
            }
        }
        log.debug("DataPath Directory: {}", this.dataPath);

        log.info("Loading {} ...", TextFormat.GREEN + "server.properties" + TextFormat.WHITE);
        this.properties = new Properties();
        this.properties.setProperty("motd", "A Nukkit Powered Server");
        this.properties.setProperty("sub-motd", "https://nukkitx.com");
        this.properties.setProperty("server-port", "19132");
        this.properties.setProperty("server-ip", "0.0.0.0");
        this.properties.setProperty("view-distance", "10");
        this.properties.setProperty("white-list", "false");
        this.properties.setProperty("achievements", "true");
        this.properties.setProperty("announce-player-achievements", "true");
        this.properties.setProperty("spawn-protection", "16");
        this.properties.setProperty("max-players", "20");
        this.properties.setProperty("allow-flight", "false");
        this.properties.setProperty("spawn-animals", "true");
        this.properties.setProperty("spawn-mobs", "true");
        this.properties.setProperty("gamemode", "0");
        this.properties.setProperty("force-gamemode", "false");
        this.properties.setProperty("hardcore", "false");
        this.properties.setProperty("pvp", "true");
        this.properties.setProperty("difficulty", "1");
        this.properties.setProperty("default-level", "world");
        this.properties.setProperty("allow-nether", "true");
        this.properties.setProperty("enable-query", "true");
        this.properties.setProperty("auto-save", "true");
        this.properties.setProperty("force-resources", "false");
        this.properties.setProperty("bug-report", "true");
        this.properties.setProperty("xbox-auth", "true");
        this.loadProperties();

        // Allow Nether? (determines if we create a nether world if one doesn't exist on startup)
        this.allowNether = this.getPropertyBoolean("allow-nether", true);

        this.forceLanguage = this.getConfig("settings.force-language", false);
        this.localeManager.setLocaleOrFallback(this.getConfig("settings.language"));
        Locale locale = this.getLanguage().getLocale();
        log.info(this.getLanguage().translate("language.selected", locale.getDisplayCountry(locale), locale));
        log.info(this.getLanguage().translate("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.RESET));

        Object poolSize = this.getConfig("settings.async-workers", (Object) (-1));
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = -1;
            }
        }
        int parallelism = (int) poolSize;
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(parallelism));
        System.setProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler", "cn.nukkit.scheduler.ServerScheduler.ExceptionHandler");
        log.debug("Async pool parallelism: {}", parallelism == -1 ? "auto" : parallelism);

        this.scheduler = new ServerScheduler();

//        this.networkZlibProvider = this.getConfig("network.zlib-provider", 2);
//        Zlib.setProvider(this.networkZlibProvider);

        this.networkCompressionLevel = this.getConfig("network.compression-level", 7);
        this.networkCompressionAsync = this.getConfig("network.async-compression", true);

        this.autoTickRate = this.getConfig("level-settings.auto-tick-rate", true);
        this.autoTickRateLimit = this.getConfig("level-settings.auto-tick-rate-limit", 20);
        this.alwaysTickPlayers = this.getConfig("level-settings.always-tick-players", false);
        this.baseTickRate = this.getConfig("level-settings.base-tick-rate", 1);

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

        if (this.getConfig().getBoolean("bug-report", true)) {
            ExceptionHandler.registerExceptionHandler();
        }

        log.info(this.getLanguage().translate("nukkit.server.info", this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.WHITE, TextFormat.AQUA + "" + TextFormat.WHITE, this.getApiVersion()));
        log.info(this.getLanguage().translate("nukkit.server.license", this.getName()));

        this.consoleSender = new ConsoleCommandSender();
        // this.commandMap = new SimpleCommandMap(this);

        // Convert legacy data before plugins get the chance to mess with it.
        try {
            nameLookup = LevelDB.PROVIDER.open(new File(dataPath, "players"), new Options()
                    .createIfMissing(true)
                    .compressionType(CompressionType.ZLIB_RAW));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.convertLegacyPlayerData();

        this.craftingManager = new CraftingManager();

        this.pluginManager = new PluginManager(this);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);

        this.pluginManager.registerInterface(JavaPluginLoader.class);

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);

        this.pluginManager.loadPlugins(this.pluginPath);

        this.enablePlugins(PluginLoadOrder.STARTUP);

        // load packs before registry closes to register new blocks and after plugins to register block factories.
        this.loadPacks();

        // Close registries
        try {
            this.blockEntityRegistry.close();
            this.blockRegistry.close();
            this.itemRegistry.close();
            this.entityRegistry.close();
            this.biomeRegistry.close();
            this.gameRuleRegistry.close();
            this.generatorRegistry.close();
            this.storageRegistry.close();
            this.packManager.closeRegistration();
            this.commandRegistry.close();
        } catch (RegistryException e) {
            throw new IllegalStateException("Unable to close registries", e);
        } finally {
            this.pluginManager.callEvent(new RegistriesClosedEvent(this.packManager));
        }

        this.registerVanillaComponents();

        Identifier defaultStorageId = Identifier.fromString(this.getConfig().get(
                "level-settings.default-format", "minecraft:leveldb"));
        if (storageRegistry.isRegistered(defaultStorageId)) {
            this.defaultStorageId = defaultStorageId;
        } else {
            log.warn("Unknown default storage type. Reverting to 'minecraft:leveldb' instead");
            this.defaultStorageId = StorageIds.LEVELDB;
        }

        this.loadLevels();

        this.saveProperties();

        if (this.getDefaultLevel() == null) {
            log.fatal(this.getLanguage().translate("nukkit.level.defaultError"));
            this.forceShutdown();

            return;
        }

        EnumLevel.initLevels();

        if (this.getConfig("ticks-per.autosave", 6000) > 0) {
            this.autoSaveTicks = this.getConfig("ticks-per.autosave", 6000);
        }

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        log.info(this.getLanguage().translate("nukkit.server.networkStart", this.getIp().equals("") ? "*" : this.getIp(), this.getPort()));
        this.serverID = UUID.randomUUID();

        this.network = new Network(this);
        this.network.setName(this.getMotd());
        this.network.setSubName(this.getSubMotd());

        try {
            this.network.registerInterface(new BedrockInterface(this));
        } catch (Exception e) {
            log.fatal("**** FAILED TO BIND TO " + getIp() + ":" + getPort() + "!");
            log.fatal("Perhaps a server is already running on that port?");
            this.forceShutdown();
        }

        if (Nukkit.DEBUG < 2) {
            this.watchdog = new Watchdog(this, 60000);
            this.watchdog.start();
        }

        this.start();
    }

    public void batchPackets(Player[] players, BedrockPacket[] packets) {
        this.batchPackets(players, packets, false);
    }

    public void batchPackets(Player[] players, BedrockPacket[] packets, boolean forceSync) {
        if (players == null || packets == null || players.length == 0 || packets.length == 0) {
            return;
        }

        BatchPacketsEvent ev = new BatchPacketsEvent(players, packets, forceSync);
        getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        try (Timing ignored = Timings.playerNetworkSendTimer.startTiming()) {
            List<Player> targets = new ArrayList<>();
            for (Player p : players) {
                if (p.isConnected()) {
                    for (BedrockPacket packet : packets) {
                        p.sendPacket(packet);
                    }
                }
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
            log.warn("Command Dispatched Async: " + commandLine);
            log.warn("Please notify author of plugin causing this execution to fix this bug!", new Throwable());
            // TODO: We should sync the command to the main thread too!
        }
        if (sender == null) {
            throw new ServerException("CommandSender is not valid");
        }

        if (this.commandRegistry.dispatch(sender, commandLine)) {
            return true;
        }

        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", commandLine));

        return false;
    }

    //todo: use ticker to check console
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
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

            for (Player player : new ArrayList<>(this.players.values())) {
                player.close(player.getLeaveMessage(), this.getConfig("settings.shutdown-message", "Server closed"));
            }

            log.debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            log.debug("Removing event handlers");
            HandlerList.unregisterAll();

            log.debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            log.debug("Unloading all levels");
            this.levelManager.close();

            log.debug("Closing console");
            this.consoleThread.interrupt();

            log.debug("Stopping network interfaces");
            for (SourceInterface interfaz : this.network.getInterfaces()) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }

            if (nameLookup != null) {
                nameLookup.close();
            }

            log.debug("Disabling timings");
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
            try {
                this.network.blockAddress(InetAddress.getByName(entry.getName()));
            } catch (UnknownHostException e) {
                // ignore
            }
        }

        //todo send usage setting
        this.tickCounter = 0;

        log.info(this.getLanguage().translate("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));

        log.info(this.getLanguage().translate("nukkit.server.startFinished", (System.currentTimeMillis() - Nukkit.START_TIME) / 1000d));

        this.tickProcessor();
        this.forceShutdown();
    }

    public void handlePacket(InetSocketAddress address, ByteBuf payload) {
        try {
            if (!payload.isReadable(3)) {
                return;
            }
            byte[] prefix = new byte[2];
            payload.readBytes(prefix);

            if (!Arrays.equals(prefix, new byte[]{(byte) 0xfe, (byte) 0xfd})) {
                return;
            }
            if (this.queryHandler != null) {
                this.queryHandler.handle(address, payload);
            }
        } catch (Exception e) {
            log.error("Error whilst handling packet", e);

            this.network.blockAddress(address.getAddress());
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

                        if (allocated > 0) {
                            Thread.sleep(allocated, 900000);
                        }
                    }
                } catch (RuntimeException e) {
                    log.error("Error whilst ticking server", e);
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
            this.uniquePlayers.add(player.getServerId());
        }
    }

    public void addPlayer(InetSocketAddress socketAddress, Player player) {
        this.players.put(socketAddress, player);
    }

    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getServerId(), player);
        this.updatePlayerListData(player.getServerId(), player.getUniqueId(), player.getDisplayName(), player.getSkin(), player.getXuid());
    }

    public void removeOnlinePlayer(Player player) {
        if (this.playerList.containsKey(player.getServerId())) {
            this.playerList.remove(player.getServerId());

            PlayerListPacket packet = new PlayerListPacket();
            packet.setAction(PlayerListPacket.Action.REMOVE);
            packet.getEntries().add(new PlayerListPacket.Entry(player.getServerId()));

            Server.broadcastPacket(this.playerList.values(), packet);
        }
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, SerializedSkin skin) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", this.playerList.values());
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, SerializedSkin skin, String xboxUserId) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, this.playerList.values());
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, SerializedSkin skin, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", players);
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, SerializedSkin skin, String xboxUserId, Player[] players) {
        PlayerListPacket packet = new PlayerListPacket();
        packet.setAction(PlayerListPacket.Action.ADD);
        PlayerListPacket.Entry entry = new PlayerListPacket.Entry(uuid);
        entry.setEntityId(entityId);
        entry.setName(name);
        entry.setSkin(skin);
        entry.setXuid(xboxUserId);
        entry.setPlatformChatId("");
        packet.getEntries().add(entry);
        Server.broadcastPacket(players, packet);
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, SerializedSkin skin, String xboxUserId, Collection<Player> players) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId,
                players.stream()
                        .filter(p -> !p.getServerId().equals(uuid))
                        .toArray(Player[]::new));
    }

    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket packet = new PlayerListPacket();
        packet.setAction(PlayerListPacket.Action.REMOVE);
        packet.getEntries().add(new PlayerListPacket.Entry(uuid));
        Server.broadcastPacket(players, packet);
    }

    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.toArray(new Player[0]));
    }

    public void sendFullPlayerListData(Player player) {
        PlayerListPacket packet = new PlayerListPacket();
        packet.setAction(PlayerListPacket.Action.ADD);
        packet.getEntries().addAll(this.playerList.values().stream()
                .map(p -> {
                    PlayerListPacket.Entry entry = new PlayerListPacket.Entry(p.getServerId());
                    entry.setEntityId(p.getUniqueId());
                    entry.setName(p.getDisplayName());
                    entry.setSkin(p.getSkin());
                    entry.setXuid(p.getXuid());
                    entry.setPlatformChatId("");
                    return entry;
                }).collect(Collectors.toList()));

        player.sendPacket(packet);
    }

    public void sendRecipeList(Player player) {
        this.craftingManager.sendRecipesTo(player);
    }

    private void checkTickUpdates(int currentTick, long tickTime) {
        for (Player p : new ArrayList<>(this.players.values())) {
            /*if (!p.loggedIn && (tickTime - p.creationTime) >= 10000 && p.kick(PlayerKickEvent.Reason.LOGIN_TIMEOUT, "Login timeout")) {
                continue;
            }

            client freezes when applying resource packs
            todo: fix*/
            p.onUpdate(currentTick);
        }
    }

    public void doAutoSave() {
        if (this.getAutoSave()) {
            try (Timing ignored = Timings.levelSaveTimer.startTiming()) {
                for (Player player : new ArrayList<>(this.players.values())) {
                    if (player.isOnline()) {
                        player.save(true);
                    } else if (!player.isConnected()) {
                        this.removePlayer(player);
                    }
                }

                this.levelManager.save();
            }
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
                log.error("Server interrupted whilst sleeping", e);
            }
        }

        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -25) {
            return false;
        }

        try (Timing ignored = Timings.fullServerTickTimer.startTiming()) {

            ++this.tickCounter;

            try (Timing ignored2 = Timings.connectionTimer.startTiming()) {
                this.network.processInterfaces();
            }

            try (Timing ignored2 = Timings.schedulerTimer.startTiming()) {
                this.scheduler.mainThreadHeartbeat(this.tickCounter);
            }

            this.checkTickUpdates(this.tickCounter, tickTime);

            this.levelManager.tick(this.tickCounter);

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
        }
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

    public String getVersion() {
        return ProtocolInfo.getDefaultMinecraftVersion();
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
        return this.getPropertyInt("server-port", 19132);
    }

    public int getViewDistance() {
        return this.getPropertyInt("view-distance", 10);
    }

    public String getIp() {
        return this.getProperty("server-ip", "0.0.0.0");
    }

    public UUID getServerUniqueId() {
        return this.serverID;
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        for (Level level : this.levelManager.getLevels()) {
            level.setAutoSave(this.autoSave);
        }
    }

    public boolean getGenerateStructures() {
        return this.getPropertyBoolean("generate-structures", true);
    }

    public int getGamemode() {
        try {
            return this.getPropertyInt("gamemode", 0) & 0b11;
        } catch (NumberFormatException exception) {
            return getGamemodeFromString(this.getProperty("gamemode")) & 0b11;
        }
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
            this.defaultGamemode = this.getGamemode();
        }
        return this.defaultGamemode;
    }

    public String getMotd() {
        return this.getProperty("motd", "A Nukkit Powered Server");
    }

    public String getSubMotd() {
        return this.getProperty("sub-motd", "https://nukkitx.com");
    }

    public boolean getForceResources() {
        return this.getPropertyBoolean("force-resources", false);
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

    public PackManager getPackManager() {
        return packManager;
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

    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
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

    public void updateName(UUID uuid, String name) {
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
            dataStream = event.getSerializer().read(name, event.getUuid().orElse(null));
            if (dataStream.isPresent()) {
                try (NBTInputStream stream = NbtUtils.createGZIPReader(dataStream.get())) {
                    return (CompoundTag) stream.readTag();
                }
            }
        } catch (IOException e) {
            log.warn(this.getLanguage().translate("nukkit.data.playerCorrupted", name));
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
            log.info(this.getLanguage().translate("nukkit.data.playerNotFound", name));
            Location spawn = this.getDefaultLevel().getSafeSpawn();
            nbt = CompoundTag.builder()
                    .longTag("firstPlayed", System.currentTimeMillis() / 1000)
                    .longTag("lastPlayed", System.currentTimeMillis() / 1000)
                    .listTag("Pos", FloatTag.class, Arrays.asList(
                            new FloatTag("", spawn.getPosition().getX()),
                            new FloatTag("", spawn.getPosition().getY()),
                            new FloatTag("", spawn.getPosition().getZ())
                    ))
                    .stringTag("Level", this.getDefaultLevel().getName())
                    .intTag("playerGameType", this.getGamemode())
                    .listTag("Rotation", FloatTag.class, Arrays.asList(
                            new FloatTag("", spawn.getYaw()),
                            new FloatTag("", spawn.getPitch())
                    ))
                    .buildRootTag();

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
        try (OutputStream dataStream = serializer.write(name, uuid);
             NBTOutputStream stream = NbtUtils.createGZIPWriter(dataStream)) {
            stream.write(tag);
        } catch (Exception e) {
            log.error(this.getLanguage().translate("nukkit.data.saveError", name, e));
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

            CompoundTag tag = getOfflinePlayerDataInternal(name, false, false);

            if (tag == null || !tag.contains("UUIDLeast") || !tag.contains("UUIDMost")) {
                // No UUID so we cannot convert. Wait until player logs in.
                continue;
            }

            UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
            if (!tag.contains("NameTag")) {
                tag = tag.toBuilder().stringTag("NameTag", name).buildRootTag();
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
        Player toRemove = this.players.remove(player.getSocketAddress());
        if (toRemove != null) {
            return;
        }

        for (InetSocketAddress socketAddress : new ArrayList<>(this.players.keySet())) {
            Player p = this.players.get(socketAddress);
            if (player == p) {
                this.players.remove(socketAddress);
                break;
            }
        }
    }

    public Set<Level> getLevels() {
        return this.levelManager.getLevels();
    }

    public Level getDefaultLevel() {
        return this.levelManager.getDefaultLevel();
    }

    public void setDefaultLevel(Level level) {
        this.levelManager.setDefaultLevel(level);
    }

    public boolean isLevelLoaded(String name) {
        return this.getLevelByName(name) != null;
    }

    public Level getLevel(String id) {
        return this.levelManager.getLevel(id);
    }

    public Level getLevelByName(String name) {
        return this.levelManager.getLevelByName(name);
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

    public LevelBuilder loadLevel() {
        return new LevelBuilder(this);
    }

    public LocaleManager getLanguage() {
        return localeManager;
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

    public Properties getProperties() {
        return this.properties;
    }

    public String getProperty(String property) {
        return this.getProperty(property, null);
    }

    public String getProperty(String property, String defaultValue) {
        return this.properties.getProperty(property, defaultValue);
    }

    public void setProperty(String property, String value) {
        this.properties.setProperty(property, value);
        this.saveProperties();
    }

    public int getPropertyInt(String property) {
        return this.getPropertyInt(property, 0);
    }

    public int getPropertyInt(String property, int defaultValue) {
        String value = this.properties.getProperty(property, Integer.toString(0));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void setPropertyInt(String property, int value) {
        this.properties.setProperty(property, Integer.toString(value));
        this.saveProperties();
    }

    public boolean getPropertyBoolean(String variable) {
        return this.getPropertyBoolean(variable, false);
    }

    public boolean getPropertyBoolean(String property, boolean defaultValue) {
        if (!this.properties.containsKey(property)) {
            return defaultValue;
        }
        String value = this.properties.getProperty(property);

        switch (value) {
            case "on":
            case "true":
            case "1":
            case "yes":
                return true;
            default:
                return false;
        }
    }

    public void setPropertyBoolean(String property, boolean value) {
        this.properties.setProperty(property, Boolean.toString(value));
    }

    private void loadProperties() {
        File propertiesFile = new File(this.dataPath, "server.properties");

        try (InputStream stream = new FileInputStream(propertiesFile)) {
            this.properties.load(stream);
        } catch (FileNotFoundException | NoSuchFileException e) {
            this.saveProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveProperties() {
        File propertiesFile = new File(this.dataPath, "server.properties");

        try (OutputStream stream = new FileOutputStream(propertiesFile)) {
            this.properties.store(stream, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public int getPlayerSkinChangeCooldown() {
        return this.getConfig("player.skin-change-cooldown", 30);
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

    private void loadPacks() {
        Path dataPath = Paths.get(this.dataPath);
        Path resourcePath = dataPath.resolve("resource_packs");
        if (Files.notExists(resourcePath)) {
            try {
                Files.createDirectory(resourcePath);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create resource_packs directory");
            }
        }
        this.packManager.loadPacks(resourcePath);
    }

    private void registerVanillaComponents() {
        Enchantment.init();
        Item.initCreativeItems();
        Effect.init();
        Potion.init();
        Attribute.init();
        this.defaultLevelData.getGameRules().putAll(this.gameRuleRegistry.getDefaultRules());
    }

    private void loadLevels() {
        Map<String, Object> worldNames = this.getConfig("worlds", Collections.emptyMap());
        if (worldNames.isEmpty()) {
            throw new IllegalStateException("No worlds configured! Add a world to nukkit.yml and try again!");
        }
        List<CompletableFuture<Level>> levelFutures = new ArrayList<>(worldNames.size());

        for (String name : worldNames.keySet()) {
            //fallback to level name if no seed is set
            Object seedObj = this.getConfig("worlds." + name + ".seed", name);
            long seed;
            if (seedObj instanceof Number) {
                seed = ((Number) seedObj).longValue();
            } else if (seedObj instanceof String) {
                if (seedObj == name) {
                    log.warn("World \"{}\" does not have a seed! Using a the name as the seed", name);
                }

                //this internally generates an MD5 hash of the seed string
                UUID uuid = UUID.nameUUIDFromBytes(((String) seedObj).getBytes(StandardCharsets.UTF_8));
                seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
            } else {
                throw new IllegalStateException("Seed for world \"" + name + "\" is invalid: " + (seedObj == null ? "null" : seedObj.getClass().getCanonicalName()));
            }

            Identifier generator = Identifier.fromString(this.getConfig("worlds." + name + ".generator"));
            String options = this.getConfig("worlds." + name + ".options", "");

            levelFutures.add(this.loadLevel().id(name)
                    .seed(seed)
                    .generator(generator == null ? this.generatorRegistry.getFallback() : generator)
                    .generatorOptions(options)
                    .load());
        }

        // Wait for levels to load.
        CompletableFutures.allAsList(levelFutures).join();

        //set default level
        if (this.getDefaultLevel() == null) {
            String defaultName = this.getProperty("default-level");
            if (defaultName == null || defaultName.trim().isEmpty()) {
                this.setProperty("default-level", defaultName = worldNames.keySet().iterator().next());
                log.warn("default-level is unset or empty, falling back to \"" + defaultName + '"');
            }

            Level defaultLevel = this.levelManager.getLevel(defaultName);
            if (defaultLevel == null) {
                throw new IllegalArgumentException("default-level refers to unknown level: \"" + defaultName + '"');
            }
            this.levelManager.setDefaultLevel(defaultLevel);
        }
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

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public LevelData getDefaultLevelData() {
        return defaultLevelData;
    }

    public Identifier getDefaultStorageId() {
        return defaultStorageId;
    }

    public StorageRegistry getStorageRegistry() {
        return storageRegistry;
    }

    public GameRuleRegistry getGameRuleRegistry() {
        return gameRuleRegistry;
    }

    public BlockRegistry getBlockRegistry() {
        return blockRegistry;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public GeneratorRegistry getGeneratorRegistry() {
        return this.generatorRegistry;
    }

    public int getBaseTickRate() {
        return baseTickRate;
    }

    public int getAutoTickRateLimit() {
        return autoTickRateLimit;
    }

    public boolean isAutoTickRate() {
        return autoTickRate;
    }

    public static Server getInstance() {
        return instance;
    }

    public boolean isIgnoredPacket(Class<? extends BedrockPacket> clazz) {
        return this.ignoredPackets.contains(clazz.getSimpleName());
    }

    private class ConsoleThread extends Thread implements InterruptibleThread {

        private ConsoleThread() {
            super("Console Thread");
        }

        @Override
        public void run() {
            console.start();
        }
    }
}
