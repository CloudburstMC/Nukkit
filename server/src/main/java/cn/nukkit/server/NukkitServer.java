package cn.nukkit.server;

import cn.nukkit.api.*;
import cn.nukkit.api.command.Command;
import cn.nukkit.api.command.CommandExecutorSource;
import cn.nukkit.api.command.ConsoleCommandExecutorSource;
import cn.nukkit.api.command.PluginCommand;
import cn.nukkit.api.event.EventManager;
import cn.nukkit.api.event.level.LevelInitEvent;
import cn.nukkit.api.event.level.LevelLoadEvent;
import cn.nukkit.api.event.server.QueryRegenerateEvent;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.api.permission.Permissible;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.api.plugin.PluginContainer;
import cn.nukkit.api.plugin.PluginLoadOrder;
import cn.nukkit.api.util.Config;
import cn.nukkit.api.util.TextFormat;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.blockentity.*;
import cn.nukkit.server.command.PluginIdentifiableCommand;
import cn.nukkit.server.command.SimpleCommandMap;
import cn.nukkit.server.console.NukkitCommandConsoleCompletor;
import cn.nukkit.server.console.NukkitConsoleAppender;
import cn.nukkit.server.console.NukkitConsoleCommandExecutorSource;
import cn.nukkit.server.console.NukkitPlayerConsoleCompletor;
import cn.nukkit.server.entity.Attribute;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.EntityHuman;
import cn.nukkit.server.entity.item.*;
import cn.nukkit.server.entity.mob.*;
import cn.nukkit.server.entity.passive.*;
import cn.nukkit.server.entity.projectile.EntityArrow;
import cn.nukkit.server.entity.projectile.EntityEgg;
import cn.nukkit.server.entity.projectile.EntityEnderPearl;
import cn.nukkit.server.entity.projectile.EntitySnowball;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.event.NukkitEventManager;
import cn.nukkit.server.inventory.CraftingManager;
import cn.nukkit.server.inventory.Recipe;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.NukkitItemStackBuilder;
import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.lang.BaseLang;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.Position;
import cn.nukkit.server.level.format.LevelProvider;
import cn.nukkit.server.level.format.LevelProviderManager;
import cn.nukkit.server.level.format.anvil.Anvil;
import cn.nukkit.server.level.format.leveldb.LevelDB;
import cn.nukkit.server.level.format.mcregion.McRegion;
import cn.nukkit.server.level.generator.Flat;
import cn.nukkit.server.level.generator.Generator;
import cn.nukkit.server.level.generator.Nether;
import cn.nukkit.server.level.generator.Normal;
import cn.nukkit.server.level.generator.biome.Biome;
import cn.nukkit.server.math.NukkitMath;
import cn.nukkit.server.metadata.EntityMetadataStore;
import cn.nukkit.server.metadata.LevelMetadataStore;
import cn.nukkit.server.metadata.PlayerMetadataStore;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.DoubleTag;
import cn.nukkit.server.nbt.tag.FloatTag;
import cn.nukkit.server.nbt.tag.ListTag;
import cn.nukkit.server.network.CompressBatchedTask;
import cn.nukkit.server.network.Network;
import cn.nukkit.server.network.RakNetInterface;
import cn.nukkit.server.network.SourceInterface;
import cn.nukkit.server.network.protocol.BatchPacket;
import cn.nukkit.server.network.protocol.DataPacket;
import cn.nukkit.server.network.protocol.PlayerListPacket;
import cn.nukkit.server.network.query.QueryHandler;
import cn.nukkit.server.network.rcon.RCON;
import cn.nukkit.server.permission.BanEntry;
import cn.nukkit.server.permission.BanList;
import cn.nukkit.server.permission.DefaultPermissions;
import cn.nukkit.server.permission.NukkitPermissionManager;
import cn.nukkit.server.plugin.NukkitPluginManager;
import cn.nukkit.server.plugin.java.JavaPluginLoader;
import cn.nukkit.server.plugin.service.NKServiceManager;
import cn.nukkit.server.plugin.service.ServiceManager;
import cn.nukkit.server.potion.Effect;
import cn.nukkit.server.potion.Potion;
import cn.nukkit.server.resourcepacks.ResourcePackManager;
import cn.nukkit.server.scheduler.FileWriteTask;
import cn.nukkit.server.scheduler.ServerScheduler;
import cn.nukkit.server.utils.*;
import cn.nukkit.server.utils.bugreport.ExceptionHandler;
import co.aikar.timings.Timings;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author MagicDroidX
 * @author Box
 */
@Log4j2
public class NukkitServer implements Server {
    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";
    public static final String NAME;
    public static final String API_VERSION;
    public static final String NUKKIT_VERSION;
    public static final String MINECRAFT_VERSION;
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final ObjectMapper YAML_MAPPER = new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final ObjectMapper PROPERTIES_MAPPER = new JavaPropsMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final boolean ansiEnabled;
    private static NukkitServer instance = null;
    private final boolean useShortTitle;
    private final ConsoleCommandExecutorSource consoleCommandExecutorSource = new NukkitConsoleCommandExecutorSource();
    private LineReader lineReader;
    private AtomicBoolean reading = new AtomicBoolean(false);
    private BanList banByName = null;
    private BanList banByIP = null;
    private Config operators = null;
    private BlockingQueue<String> inputLines;
    private ConsoleReader consoleReader;
    private boolean hasStopped = false;
    static {
        NAME = NukkitServer.class.getPackage().getImplementationTitle();
        API_VERSION = NukkitServer.class.getPackage().getSpecificationVersion();
        MINECRAFT_VERSION = NukkitServer.class.getPackage().getImplementationVersion();
        NUKKIT_VERSION = NukkitServer.class.getPackage().getImplementationVendor();
    }

    @Getter
    private final NukkitPluginManager pluginManager = new NukkitPluginManager(this);
    private final NukkitEventManager eventManager = new NukkitEventManager();
    private NukkitServerProperties serverProperties;
    private int profilingTickrate = 20;

    @Getter
    private ServerScheduler scheduler = null;
    private int tickCounter;
    private long nextTick;
    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float maxTick = 20;
    private float maxUse = 0;
    private int sendUsageTicker = 0;
    private boolean dispatchSignals = false;
    private SimpleCommandMap commandMap;

    @Getter
    private CraftingManager craftingManager;

    @Getter
    private ResourcePackManager resourcePackManager;
    private RCON rcon;
    private NukkitWhitelist whitelist;

    @Getter
    private EntityMetadataStore entityMetadata;

    @Getter
    private PlayerMetadataStore playerMetadata;

    @Getter
    private LevelMetadataStore levelMetadata;

    @Getter
    private Network network;
    private boolean networkCompressionAsync = true;
    public int networkCompressionLevel = 7;
    private boolean autoTickRate = true;
    private int autoTickRateLimit = 20;
    private boolean alwaysTickPlayers = false;
    private int baseTickRate = 1;
    private int autoSaveTicker = 0;
    private int autoSaveTicks = 6000;
    private BaseLang baseLang;
    private boolean forceLanguage = false;
    private UUID serverID;
    private final NukkitPermissionManager permissionManager = new NukkitPermissionManager();
    private final Path filePath;
    private final Path dataPath;
    private final Set<UUID> uniquePlayers = new HashSet<>();
    private QueryHandler queryHandler;
    private QueryRegenerateEvent queryRegenerateEvent;
    private Config config;
    private final Map<String, Player> players = new HashMap<>();
    private final Map<UUID, Player> playerList = new HashMap<>();
    private final Map<Integer, String> identifier = new HashMap<>();

    @Getter
    private final Map<Integer, Level> levels = new HashMap<>();
    @Getter
    private final ServiceManager serviceManager = new NKServiceManager();

    @Getter
    private Level defaultLevel = null;
    private Thread currentThread;
    private AtomicBoolean running = new AtomicBoolean(true);
    private final Path pluginPath;

    NukkitServer(final Path filePath, final Path dataPath, final Path pluginPath, final boolean ansiEnabled, final boolean useShortTitle) {
        this.filePath = filePath;
        this.dataPath = dataPath;
        this.pluginPath = pluginPath;
        this.ansiEnabled = ansiEnabled;
        this.useShortTitle = useShortTitle;
    }

    public static void broadcastPacket(Collection<Player> players, DataPacket packet) {
        broadcastPacket(players.toArray(new Player[players.size()]), packet);
    }

    /**
     * Get the server's instance.
     * @deprecated you will now have to rely on getting the instance of the server through other methods such as player
     * or when your plugin loads.
     * @return instance of NukkitServer
     */
    @Deprecated
    public static NukkitServer getInstance() {
        return instance;
    }

    public void boot() throws Exception {
        Preconditions.checkState(instance == null, "Already initialized!");
        instance = this;

        currentThread = Thread.currentThread();
        currentThread.setName("Nukkit Main Thread");

        if (!new File(dataPath + "worlds/").exists()) {
            new File(dataPath + "worlds/").mkdirs();
        }

        if (!new File(dataPath + "players/").exists()) {
            new File(dataPath + "players/").mkdirs();
        }

        Terminal terminal = NukkitConsoleAppender.getTerminal();

        LineReader reader = null;
        if (terminal != null) {
            reader = LineReaderBuilder.builder()
                    .appName("Nukkit")
                    .completer(new NukkitPlayerConsoleCompletor(this))
                    .completer(new NukkitCommandConsoleCompletor(this))
                    .terminal(terminal)
                    .build();
            reader.setKeyMap(LineReader.VISUAL);
            reader.unsetOpt(LineReader.Option.INSERT_TAB);
            NukkitConsoleAppender.setLineReader(reader);
        }

        inputLines = new LinkedBlockingQueue<>();

        if (reader != null) {
            lineReader = reader;
            consoleReader = new ConsoleReader();

            consoleReader.setName("Nukkit Console Reader");
            consoleReader.start();

            // Wait until we read
            while (!reading.get()) {
            }
        }
        //todo: VersionString 现在不必要

        if (!new File(this.dataPath + "nukkit.yml").exists()) {
            log.info(TextFormat.GREEN + "Welcome! Please choose a language first!");
            try {
                String[] lines = Utils.readFile(this.getClass().getClassLoader().getResourceAsStream("lang/language.list")).split("\n");
                for (String line : lines) {
                    log.info(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String fallback = BaseLang.FALLBACK_LANGUAGE;
            String language = null;
            while (language == null) {
                String lang;
                if (consoleReader == null) {
                    lang = "eng"; // Console reader never started!
                }
                lang = consoleReader.readLine();
                InputStream conf = this.getClass().getClassLoader().getResourceAsStream("lang/" + lang + "/lang.ini");
                if (conf != null) {
                    language = lang;
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

        consoleReader.start();

        log.info("Loading " + TextFormat.GREEN + "nukkit.yml" + TextFormat.WHITE + "...");
        config = new NukkitConfig(dataPath + "nukkit.yml", Config.Type.YAML);

        log.info("Loading " + TextFormat.GREEN + "server.properties" + TextFormat.WHITE + "...");
        loadServerProperties();

        this.forceLanguage = (Boolean) this.getConfig("settings.force-language", false);
        this.baseLang = new BaseLang((String) this.getConfig("settings.language", BaseLang.FALLBACK_LANGUAGE));
        log.info(this.getLanguage().translateString("language.selected", new String[]{getLanguage().getName(), getLanguage().getLang()}));
        log.info(getLanguage().translateString("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.WHITE));

        Object poolSize = this.getConfig("settings.async-workers", "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors() + 1, 4);
            }
        }

        ServerScheduler.WORKERS = (int) poolSize;

        this.networkCompressionLevel = (int) this.getConfig("network.compression-level", 7);
        this.networkCompressionAsync = (boolean) this.getConfig("network.async-compression", true);

        this.networkCompressionLevel = (int) this.getConfig("network.compression-level", 7);
        this.networkCompressionAsync = (boolean) this.getConfig("network.async-compression", true);

        this.autoTickRate = (boolean) this.getConfig("level-settings.auto-tick-rate", true);
        this.autoTickRateLimit = (int) this.getConfig("level-settings.auto-tick-rate-limit", 20);
        this.alwaysTickPlayers = (boolean) this.getConfig("level-settings.always-tick-players", false);
        this.baseTickRate = (int) this.getConfig("level-settings.base-tick-rate", 1);

        this.scheduler = new ServerScheduler();

        if (serverProperties.getRcon().isEnabled()) {
            this.rcon = new RCON(this, serverProperties.getRcon().getPassword(), serverProperties.getRcon().getPassword(), serverProperties.getRcon().getPort());
        }

        this.entityMetadata = new EntityMetadataStore();
        this.playerMetadata = new PlayerMetadataStore();
        this.levelMetadata = new LevelMetadataStore();

        this.operators = new NukkitConfig(this.dataPath + "ops.txt", Config.Type.ENUM);
        loadWhitelist();
        this.banByName = new BanList(this.dataPath + "banned-players.json");
        this.banByName.load();
        this.banByIP = new BanList(this.dataPath + "banned-ips.json");
        this.banByIP.load();

        // Check if hardcore is enabled and set to force a sanity check on the difficulty.
        if (serverProperties.isHardcore()) {
            serverProperties.setHardcore(true);
        }

        if (this.getConfig().getBoolean("bug-report", true)) {
            ExceptionHandler.registerExceptionHandler();
        }

        log.info(this.getLanguage().translateString("nukkit.server.networkStart", new String[]{serverProperties.getServerAddress(), Integer.toString(serverProperties.getServerPort())}));
        this.serverID = UUID.randomUUID();

        this.network = new Network(this);
        this.network.setName(serverProperties.getMotd());
        this.network.setSubName(serverProperties.getSubMotd());

        log.info(this.getLanguage().translateString("nukkit.server.info", this.getName(), TextFormat.YELLOW + NUKKIT_VERSION + TextFormat.WHITE, API_VERSION));
        log.info(this.getLanguage().translateString("nukkit.server.license", this.getName()));

        this.commandMap = new SimpleCommandMap(this);

        this.registerEntities();
        this.registerBlockEntities();

        Block.init();
        Enchantment.init();
        Item.init();
        Biome.init();
        Effect.init();
        Potion.init();
        Attribute.init();

        this.craftingManager = new CraftingManager();
        this.resourcePackManager = new ResourcePackManager(dataPath.resolve("resource_packs"));

        // Register plugin loaders
        pluginManager.registerPluginLoader(JavaPluginLoader.class, new JavaPluginLoader(this));

        permissionManager.subscribeToPermission(NukkitServer.BROADCAST_CHANNEL_ADMINISTRATIVE, consoleCommandExecutorSource);


        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);

        this.network.registerInterface(new RakNetInterface(this));

        loadPlugins();

        this.enablePlugins(PluginLoadOrder.STARTUP);

        LevelProviderManager.addProvider(this, Anvil.class);
        LevelProviderManager.addProvider(this, McRegion.class);
        LevelProviderManager.addProvider(this, LevelDB.class);

        Generator.addGenerator(Flat.class, "flat", Generator.TYPE_FLAT);
        Generator.addGenerator(Normal.class, "normal", Generator.TYPE_INFINITE);
        Generator.addGenerator(Normal.class, "default", Generator.TYPE_INFINITE);
        Generator.addGenerator(Nether.class, "nether", Generator.TYPE_NETHER);
        //todo: add old generator and hell generator

        for (String name : ((Map<String, Object>) this.getConfig("worlds", new HashMap<>())).keySet()) {
            if (!this.loadLevel(name)) {
                long seed;
                try {
                    seed = ((Integer) this.getConfig("worlds." + name + ".seed")).longValue();
                } catch (Exception e) {
                    seed = System.currentTimeMillis();
                }

                Map<String, Object> options = new HashMap<>();
                String[] opts = ((String) this.getConfig("worlds." + name + ".generator", Generator.getGenerator("default").getSimpleName())).split(":");
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
            String defaultName = serverProperties.getLevelName();
            if (!this.loadLevel(defaultName)) {
                long seed;
                String seedString = String.valueOf(serverProperties.getLevelSeed());
                try {
                    seed = Long.valueOf(seedString);
                } catch (NumberFormatException e) {
                    seed = seedString.hashCode();
                }
                this.generateLevel(defaultName, seed == 0 ? System.currentTimeMillis() : seed);
            }

            this.setDefaultLevel(this.getLevelByName(defaultName));
        }

        if (this.getDefaultLevel() == null) {
            log.fatal(this.getLanguage().translateString("nukkit.level.defaultError"));
            this.forceShutdown();

            return;
        }

        if ((int) this.getConfig("ticks-per.autosave", 6000) > 0) {
            this.autoSaveTicks = (int) this.getConfig("ticks-per.autosave", 6000);
        }

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        this.start();

    }

    @Override
    public void broadcastMessage(String message) {
        broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    @Nonnull
    @Override
    public String getName() {
        return NAME;
    }

    @Nonnull
    @Override
    public String getVersion() {
        return MINECRAFT_VERSION;
    }

    @Nonnull
    @Override
    public String getApiVersion() {
        return API_VERSION;
    }

    @Nonnull
    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Nonnull
    @Override
    public ConsoleCommandExecutorSource getConsoleCommandExecutorSource() {
        return consoleCommandExecutorSource;
    }

    @Nonnull
    @Override
    public ObjectMapper getJsonMapper() {
        return JSON_MAPPER;
    }

    @Nonnull
    @Override
    public ObjectMapper getYamlMapper() {
        return YAML_MAPPER;
    }

    @Nonnull
    @Override
    public ObjectMapper getPropertiesMapper() {
        return PROPERTIES_MAPPER;
    }

    @Nonnull
    @Override
    public ServerProperties getServerProperties() {
        return serverProperties;
    }

    public void broadcastMessage(Message message) {
        broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    public void broadcastMessage(String message, Collection<MessageRecipient> recipients) {
        for (MessageRecipient recipient : recipients) {
            recipient.sendMessage(message);
        }
    }

    public void broadcastMessage(Message message, Collection<MessageRecipient> recipients) {
        for (MessageRecipient recipient : recipients) {
            recipient.sendMessage(message);
        }
    }

    public void broadcast(String message, String permissions) {
        Set<MessageRecipient> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : permissionManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof MessageRecipient && permissible.hasPermission(permission)) {
                    recipients.add((MessageRecipient) permissible);
                }
            }
        }

        for (MessageRecipient recipient : recipients) {
            recipient.sendMessage(message);
        }
    }

    public static void broadcastPacket(Player[] players, DataPacket packet) {
        packet.encode();
        packet.isEncoded = true;

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
        if (players == null || packets == null || players.length == 0 || packets.length == 0) {
            return;
        }

        Timings.playerNetworkSendTimer.startTiming();
        byte[][] payload = new byte[packets.length * 2][];
        for (int i = 0; i < packets.length; i++) {
            DataPacket p = packets[i];
            if (!p.isEncoded) {
                p.encode();
            }
            byte[] buf = p.getBuffer();
            payload[i * 2] = Binary.writeUnsignedVarInt(buf.length);
            payload[i * 2 + 1] = buf;
        }
        byte[] data;
        data = Binary.appendBytes(payload);

        List<String> targets = new ArrayList<>();
        for (Player p : players) {
            if (p.isConnected()) {
                targets.add(this.identifier.get(p.hashCode()));
            }
        }

        if (!forceSync && this.networkCompressionAsync) {
            this.getScheduler().scheduleAsyncTask(new CompressBatchedTask(data, targets, this.networkCompressionLevel));
        } else {
            try {
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
        for (PluginContainer container : pluginManager.getAllPlugins()) {
            Plugin plugin = container.getPlugin();
            if (plugin.isEnabled()) continue;
            PluginLoadOrder order = plugin.getDescription().getLoadOrder().orElse(PluginLoadOrder.POSTWORLD);
            if (type == order) {
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

    public void broadcast(Message message, String permissions) {
        Set<MessageRecipient> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : permissionManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof MessageRecipient && permissible.hasPermission(permission)) {
                    recipients.add((MessageRecipient) permissible);
                }
            }
        }

        for (MessageRecipient recipient : recipients) {
            recipient.sendMessage(message);
        }
    }

    public boolean dispatchCommand(CommandExecutorSource executorSource, String commandLine) throws ServerException {
        // First we need to check if this command is on the main thread or not, if not, warn the user
        if (!this.isPrimaryThread()) {
            log.warn("Command Dispatched Async: " + commandLine);
            log.warn("Please notify author of plugin causing this execution to fix this bug!", new Throwable());
            // TODO: We should sync the command to the main thread too!
        }
        if (executorSource == null) {
            throw new ServerException("CommandSender is not valid");
        }

        if (this.commandMap.dispatch(executorSource, commandLine)) {
            return true;
        }

        if (executorSource instanceof MessageRecipient) {
            ((MessageRecipient) executorSource).sendMessage(new TranslatedMessage(TextFormat.RED + "%commands.generic.unknown", commandLine));
        }

        return false;
    }

    //todo: use ticker to check console
    public ConsoleCommandExecutorSource getConsoleSender() {
        return consoleCommandExecutorSource;
    }

    public void reload() throws Exception {
        log.info("Reloading...");

        log.info("Saving levels...");

        for (Level level : this.levels.values()) {
            level.save();
        }

        pluginManager.disablePlugins();
        pluginManager.clearPlugins();
        pluginManager.unregisterPluginLoader(JavaPluginLoader.class);
        commandMap.clearCommands();

        log.info("Reloading properties...");
        reloadServerProperties();

        banByIP.load();
        banByName.load();
        reloadWhitelist();
        operators.reload();

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            getNetwork().blockAddress(entry.getName(), -1);
        }

        pluginManager.registerPluginLoader(JavaPluginLoader.class, new JavaPluginLoader(this));
        enablePlugins(PluginLoadOrder.STARTUP);
        enablePlugins(PluginLoadOrder.POSTWORLD);
        Timings.reset();
    }

    public void shutdown() {
        if (running.get()) {
            ServerKiller killer = new ServerKiller(90);
            killer.start();
        }
        running.set(false);
    }

    public void forceShutdown() {
        if (this.hasStopped) {
            return;
        }

        try {
            if (!running.get()) {
                //todo sendUsage
            }

            // clean shutdown of console thread asap
            NukkitConsoleAppender.close();

            this.hasStopped = true;

            this.shutdown();

            if (this.rcon != null) {
                this.rcon.close();
            }

            log.debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            for (Player player : new ArrayList<>(this.players.values())) {
                player.close(player.getLeaveMessage(), (String) this.getConfig("settings.shutdown-message", "Server closed"));
            }

            log.debug("Unloading all levels");
            for (Level level : new ArrayList<>(this.getLevels().values())) {
                this.unloadLevel(level, true);
            }

            log.debug("Removing event handlers");
            HandlerList.unregisterAll();

            log.debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            log.debug("Closing console");
            if (consoleReader != null) {
                try {
                    consoleReader.interrupt();
                    consoleReader.join();
                } catch (InterruptedException e) {
                    log.throwing(e);
                }
            }

            log.debug("Stopping network interfaces");
            for (SourceInterface interfaz : this.network.getInterfaces()) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }

            log.debug("Disabling timings");
            Timings.stopServer();

            LogManager.shutdown();
            //todo other things
        } catch (Exception e) {
            log.throwing(e); //todo remove this?
            log.fatal("Exception happened while shutting down, exit the process");
            System.exit(1);
        }
    }

    public void start() {
        if (serverProperties.isQueryEnabled()) {
            this.queryHandler = new QueryHandler();
        }

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            this.network.blockAddress(entry.getName(), -1);
        }

        //todo send usage setting

        this.tickCounter = 0;

        log.info(this.getLanguage().translateString("nukkit.server.defaultGameMode", getGamemodeString(getGamemodeFromString(serverProperties.getDefaultGamemode()))));

        log.info(this.getLanguage().translateString("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Bootstrap.START_TIME) / 1000)));

        this.tickProcessor();
        this.forceShutdown();
    }

    public void handlePacket(String address, int port, byte[] payload) {
        try {
            if (payload.length > 2 && Arrays.equals(Binary.subBytes(payload, 0, 2), new byte[]{(byte) 0xfe, (byte) 0xfd}) && this.queryHandler != null) {
                this.queryHandler.handle(address, port, payload);
            }
        } catch (Exception e) {
            log.throwing(e);

            this.getNetwork().blockAddress(address, 600);
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
        this.identifier.put(player.hashCode(), identifier);
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

            NukkitServer.broadcastPacket(this.playerList.values(), pk);
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
        NukkitServer.broadcastPacket(players, pk);
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
        NukkitServer.broadcastPacket(players, pk);
    }

    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.stream().toArray(Player[]::new));
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

    public void tickProcessor() {
        this.nextTick = System.currentTimeMillis();
        try {
            while (running.get()) {
                try {
                    this.tick();

                    long next = this.nextTick;
                    long current = System.currentTimeMillis();

                    if (next - 0.1 > current) {
                        Thread.sleep(next - current - 1, 900000);
                    }
                } catch (RuntimeException e) {
                    log.throwing(e);
                }
            }
        } catch (Throwable e) {
            log.fatal("Exception happened while ticking server");
            log.error(Utils.getExceptionMessage(e));
            log.error(Utils.getAllThreadDumps());
        }
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
        for (Level level : this.getLevels().values()) {
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
                        log.debug("Raising level \"" + level.getName() + "\" tick rate to " + level.getTickRate() + " ticks");
                    } else if (tickMs >= 50) {
                        if (level.getTickRate() == this.baseTickRate) {
                            level.setTickRate((int) Math.max(this.baseTickRate + 1, Math.min(this.autoTickRateLimit, Math.floor(tickMs / 50))));
                            log.debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        } else if ((tickMs / level.getTickRate()) >= 50 && level.getTickRate() < this.autoTickRateLimit) {
                            level.setTickRate(level.getTickRate() + 1);
                            log.debug("Level \"" + level.getName() + "\" took " + NukkitMath.round(tickMs, 2) + "ms, setting tick rate to " + level.getTickRate() + " ticks");
                        }
                        level.tickRateCounter = level.getTickRate();
                    }
                }
            } catch (Exception e) {
                log.fatal(this.getLanguage().translateString("nukkit.level.tickError", new String[]{level.getName(), e.toString()}));
                log.throwing(e);
            }
        }
    }

    public void doAutoSave() {
        if (serverProperties.isAutoSaveEnabled()) {
            Timings.levelSaveTimer.startTiming();
            for (Player player : new ArrayList<>(this.players.values())) {
                if (player.isOnline()) {
                    player.save(true);
                } else if (!player.isConnected()) {
                    this.removePlayer(player);
                }
            }

            for (Level level : this.getLevels().values()) {
                level.save();
            }
            Timings.levelSaveTimer.stopTiming();
        }
    }

    private boolean tick() {
        long tickTime = System.currentTimeMillis();
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
                    this.getEventManager().fire(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                    if (this.queryHandler != null) {
                        this.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    log.throwing(e);
                }
            }

            this.getNetwork().updateName();
        }

        if (serverProperties.isAutoSaveEnabled() && ++this.autoSaveTicker >= this.autoSaveTicks) {
            this.autoSaveTicker = 0;
            this.doAutoSave();
        }

        if (this.sendUsageTicker > 0 && --this.sendUsageTicker == 0) {
            this.sendUsageTicker = 6000;
            //todo sendUsage
        }

        if (this.tickCounter % 100 == 0) {
            for (Level level : this.levels.values()) {
                level.clearCache();
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

    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
    }

    // TODO: Fix title tick
    public void titleTick() {
        if (!ansiEnabled) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        double used = ((runtime.totalMemory() - runtime.freeMemory()) / 1048576D);
        double max = (runtime.maxMemory() / 1048576D);
        String usage = Math.round((used / max) * 100) + "%";
        String title = (char) 0x1b + "]0;" + this.getName() + " " +
                MINECRAFT_VERSION +
                " | Online " + players.size() + "/" + serverProperties.getMaxPlayers() +
                " | Memory " + usage;
        if (!useShortTitle) {
            title += " | U " + NukkitMath.round((this.network.getUpload() / 1024 * 1000), 2)
                    + " D " + NukkitMath.round((this.network.getDownload() / 1024 * 1000), 2) + " kB/s";
        }
        title += " | TPS " + this.getTicksPerSecond() +
                " | Load " + this.getTickUsage() + "%" + (char) 0x07;

        System.out.print(title);
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
        return new HashMap<>(playerList);
    }

    public void addRecipe(Recipe recipe) {
        this.craftingManager.registerRecipe(recipe);
    }

    public IPlayer getOfflinePlayer(String name) {
        IPlayer result = this.getPlayerExact(name.toLowerCase());
        if (result == null) {
            return new OfflinePlayer(this, name);
        }

        return result;
    }

    public boolean isRunning() {
        return running.get();
    }

    public void saveOfflinePlayerData(String name, CompoundTag tag) {
        this.saveOfflinePlayerData(name, tag, false);
    }

    public CompoundTag getOfflinePlayerData(String name) {
        name = name.toLowerCase();
        String path = dataPath + "players/";
        File file = new File(path + name + ".dat");

        if (this.shouldSavePlayerData() && file.exists()) {
            try {
                return NBTIO.readCompressed(new FileInputStream(file));
            } catch (Exception e) {
                file.renameTo(new File(path + name + ".dat.bak"));
                log.warn(this.getLanguage().translateString("nukkit.data.playerCorrupted", name));
            }
        } else {
            log.warn(this.getLanguage().translateString("nukkit.data.playerNotFound", name));
        }

        Position spawn = this.getDefaultLevel().getSafeSpawn();
        CompoundTag nbt = new CompoundTag()
                .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                .putLong("lastPlayed", System.currentTimeMillis() / 1000)
                .putList(new ListTag<>("Pos")
                        .add(new DoubleTag("0", spawn.x))
                        .add(new DoubleTag("1", spawn.y))
                        .add(new DoubleTag("2", spawn.z)))
                .putString("Level", this.getDefaultLevel().getName())
                .putList(new ListTag<>("Inventory"))
                .putCompound("Achievements", new CompoundTag())
                .putInt("playerGameType", getGamemodeFromString(serverProperties.getDefaultGamemode()))
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
                .putBoolean("Invulnerable", false)
                .putString("NameTag", name);

        this.saveOfflinePlayerData(name, nbt);
        return nbt;
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

        return matchedPlayer.toArray(new Player[matchedPlayer.size()]);
    }

    public void removePlayer(Player player) {
        if (this.identifier.containsKey(player.hashCode())) {
            String identifier = this.identifier.get(player.hashCode());
            this.players.remove(identifier);
            this.identifier.remove(player.hashCode());
            return;
        }

        for (String identifier : new ArrayList<>(this.players.keySet())) {
            Player p = this.players.get(identifier);
            if (player == p) {
                this.players.remove(identifier);
                this.identifier.remove(player.hashCode());
                break;
            }
        }
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
        for (Level level : this.getLevels().values()) {
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

    public void saveOfflinePlayerData(String name, CompoundTag tag, boolean async) {
        if (this.shouldSavePlayerData()) {
            try {
                if (async) {
                    this.getScheduler().scheduleAsyncTask(new FileWriteTask(dataPath + "players/" + name.toLowerCase() + ".dat", NBTIO.writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN)));
                } else {
                    Utils.writeFile(dataPath + "players/" + name.toLowerCase() + ".dat", new ByteArrayInputStream(NBTIO.writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN)));
                }
            } catch (Exception e) {
                log.error(this.getLanguage().translateString("nukkit.data.saveError", new String[]{name, e.getMessage()}));
                if (log.isDebugEnabled()) {
                    log.throwing(e);
                }
            }
        }
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
            path = dataPath + "worlds/" + name + "/";
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
            log.throwing(e);
            return false;
        }

        this.levels.put(level.getId(), level);

        level.initLevel();

        this.getEventManager().fire(new LevelLoadEvent(level));

        level.setTickRate(this.baseTickRate);

        return true;
    }

    public boolean isLevelGenerated(String name) {
        if (Objects.equals(name.trim(), "")) {
            return false;
        }

        String path = dataPath + "worlds/" + name + "/";
        if (this.getLevelByName(name) == null) {

            if (LevelProviderManager.getProvider(path) == null) {
                return false;
            }
        }

        return true;
    }

    public BaseLang getLanguage() {
        return baseLang;
    }

    public boolean isLanguageForced() {
        return forceLanguage;
    }

    // TODO: Implement with Jackson Object Mapper.
    public Config getConfig() {
        return config;
    }

    public Object getConfig(String variable) {
        return this.getConfig(variable, null);
    }

    public Object getConfig(String variable, Object defaultValue) {
        Object value = this.config.get(variable);
        return value == null ? defaultValue : value;
    }

    @Nullable
    public PluginCommand getPluginCommand(String name) {
        Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginCommand) command;
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

    public boolean isOp(String name) {
        return this.operators.exists(name, true);
    }

    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options, Class<? extends LevelProvider> provider) {
        if (Objects.equals(name.trim(), "") || this.isLevelGenerated(name)) {
            return false;
        }

        if (!options.containsKey("preset")) {
            options.put("preset", serverProperties.getGeneratorSettings());
        }

        if (generator == null) {
            generator = Generator.getGenerator(serverProperties.getLevelType());
        }

        if (provider == null) {
            if ((provider = LevelProviderManager.getProviderByName
                    ((String) this.getConfig("level-settings.default-format", "anvil"))) == null) {
                provider = LevelProviderManager.getProviderByName("anvil");
            }
        }

        String path;

        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = dataPath + "worlds/" + name + "/";
        }

        Level level;
        try {
            provider.getMethod("generate", String.class, String.class, long.class, Class.class, Map.class).invoke(null, path, name, seed, generator, options);

            level = new Level(this, name, path, provider);
            this.levels.put(level.getId(), level);

            level.initLevel();
            level.getGameRules().setGameRule("spawnRadius", "" + serverProperties.getSpawnProtection());

            level.setTickRate(this.baseTickRate);
        } catch (Exception e) {
            log.error(this.getLanguage().translateString("nukkit.level.generationError", new String[]{name, e.getMessage()}));
            log.throwing(e);
            return false;
        }

        this.getEventManager().fire(new LevelInitEvent(level));

        this.getEventManager().fire(new LevelLoadEvent(level));

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

    public boolean isWhitelisted(String name) {
        return !serverProperties.isWhitelistEnabled() || this.operators.exists(name, true) || this.whitelist.exists(name, true);
    }

    public Config getOps() {
        return operators;
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
        return (Boolean) this.getConfig("player.save-player-data", true);
    }

    /**
     * Checks the current thread against the expected primary thread for the server.
     * <p>
     * <b>Note:</b> this method should not be used to indicate the current synchronized state of the runtime. A current thread matching the main thread indicates that it is synchronized, but a mismatch does not preclude the same assumption.
     *
     * @return true if the current thread matches the expected primary thread, false otherwise
     */
    public boolean isPrimaryThread() {
        return (Thread.currentThread() == currentThread);
    }

    private void registerEntities() {
        Entity.registerEntity("Arrow", EntityArrow.class);
        Entity.registerEntity("ItemUse", EntityItem.class);
        Entity.registerEntity("FallingSand", EntityFallingBlock.class);
        Entity.registerEntity("PrimedTnt", EntityPrimedTNT.class);
        Entity.registerEntity("Snowball", EntitySnowball.class);
        Entity.registerEntity("EnderPearl", EntityEnderPearl.class);
        Entity.registerEntity("Painting", EntityPainting.class);
        //todo mobs
        Entity.registerEntity("Creeper", EntityCreeper.class);
        Entity.registerEntity("Zombie", EntityZombie.class);
        Entity.registerEntity("Blaze", EntityBlaze.class);
        Entity.registerEntity("CaveSpider", EntityCaveSpider.class);
        Entity.registerEntity("ElderGuardian", EntityElderGuardian.class);
        Entity.registerEntity("EnderDragon", EntityEnderDragon.class);
        Entity.registerEntity("Enderman", EntityEnderman.class);
        Entity.registerEntity("Endermite", EntityEndermite.class);
        Entity.registerEntity("Ghast", EntityGhast.class);
        Entity.registerEntity("Guardian", EntityGuardian.class);
        Entity.registerEntity("Husk", EntityHusk.class);
        Entity.registerEntity("MagmaCube", EntityMagmaCube.class);
        Entity.registerEntity("Shulker", EntityShulker.class);
        Entity.registerEntity("Silferfish", EntitySilverfish.class);
        Entity.registerEntity("Skeleton", EntitySkeleton.class);
        Entity.registerEntity("SkeletonHorse", EntitySkeletonHorse.class);
        Entity.registerEntity("Slime", EntitySlime.class);
        Entity.registerEntity("Spider", EntitySpider.class);
        Entity.registerEntity("Stray", EntityStray.class);
        Entity.registerEntity("Witch", EntityWitch.class);
        //TODO: more mobs
        Entity.registerEntity("Bat", EntityBat.class);
        Entity.registerEntity("Chicken", EntityChicken.class);
        Entity.registerEntity("Cow", EntityCow.class);
        Entity.registerEntity("Donkey", EntityDonkey.class);
        Entity.registerEntity("Horse", EntityHorse.class);
        Entity.registerEntity("Llama", EntityLlama.class);
        Entity.registerEntity("Mooshroom", EntityMooshroom.class);
        Entity.registerEntity("Mule", EntityMule.class);
        Entity.registerEntity("PolarBear", EntityPolarBear.class);
        Entity.registerEntity("Pig", EntityPig.class);
        Entity.registerEntity("Rabbit", EntityRabbit.class);
        Entity.registerEntity("Sheep", EntitySheep.class);
        Entity.registerEntity("Squid", EntitySquid.class);
        Entity.registerEntity("Wolf", EntityWolf.class);
        Entity.registerEntity("Ocelot", EntityOcelot.class);
        Entity.registerEntity("Villager", EntityVillager.class);

        Entity.registerEntity("ThrownExpBottle", EntityExpBottle.class);
        Entity.registerEntity("XpOrb", EntityXPOrb.class);
        Entity.registerEntity("ThrownPotion", EntityPotion.class);
        Entity.registerEntity("Egg", EntityEgg.class);

        Entity.registerEntity("Human", EntityHuman.class, true);

        Entity.registerEntity("MinecartRideable", EntityMinecartEmpty.class);
        // TODO: 2016/1/30 all finds of minecart
        Entity.registerEntity("Boat", EntityBoat.class);

        //Entity.registerEntity("Lightning", EntityLightning.class); lightning shouldn't be saved as entity
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
    }

    @Nonnull
    @Override
    public Whitelist getWhitelist() {
        return whitelist;
    }

    @Nonnull
    @Override
    public NukkitConfigBuilder getConfigBuilder() {
        return new NukkitConfigBuilder();
    }

    @Nonnull
    @Override
    public NukkitItemStackBuilder getItemStackBuilder() {
        return new NukkitItemStackBuilder();
    }

    @Nonnull
    @Override
    public NukkitPermissionManager getPermissionManager() {
        return permissionManager;
    }

    private void loadPlugins() throws Exception {
        log.info("L");
        try {
            Path pluginPath = Paths.get("plugins");
            if (Files.notExists(pluginPath)) {
                Files.createDirectory(pluginPath);
            } else {
                if (!Files.isDirectory(pluginPath)) {
                    log.info("Plugin location {} is not a directory, continuing without loading plugins.", pluginPath);
                    return;
                }
            }
            pluginManager.loadPlugins(pluginPath);
        } catch (Exception e) {
            log.error("Can't load plugins", e);
        }
        log.info("Loaded {} plugins.", pluginManager.getAllPlugins().size());
    }

    private void loadServerProperties() throws Exception {
        Path configFile = Paths.get(filePath + File.separator + "server.properties");
        try {
            serverProperties = NukkitServerProperties.load(configFile);
            serverProperties.addMissingValues();
            if (serverProperties.hasPropertiesChanged()) {
                NukkitServerProperties.save(configFile, serverProperties);
            }
        } catch (NoSuchFileException e) {
            serverProperties = NukkitServerProperties.defaultConfiguration();
            NukkitServerProperties.save(configFile, serverProperties);
        }
    }

    private void reloadServerProperties() throws Exception {
        if (serverProperties.isValueChanged()) {
            Path configFile = Paths.get(filePath + File.separator + "server.properties");
            NukkitServerProperties.save(configFile, serverProperties);
        }
        serverProperties = null;
        loadServerProperties();
    }

    private void loadWhitelist() throws Exception {
        Path whitelistFile = Paths.get(filePath + File.separator + "whitelist.json");
        try {
            whitelist = NukkitWhitelist.load(whitelistFile);
        } catch (NoSuchFileException e) {
            whitelist = NukkitWhitelist.defaultConfiguration();
            NukkitWhitelist.save(whitelistFile, whitelist);
        }
    }

    private void reloadWhitelist() throws Exception {
        Path whitelistFile = Paths.get(filePath + File.separator + "whitelist.json");
        NukkitWhitelist.save(whitelistFile, whitelist);
        whitelist = null;
        loadWhitelist();
    }

    private class ConsoleReader extends Thread implements InterruptibleThread {

        @Override
        public void run() {
            String line;
            try {
                while (running.get()) {
                    // Read line
                    reading.set(true);
                    line = lineReader.readLine("> ");
                    inputLines.offer(line);
                }
            } catch (UserInterruptException e) {
                shutdown();
            } finally {
                NukkitConsoleAppender.setLineReader(null);
            }
        }

        public String readLine() throws Exception{
            try {
                reading.set(true);
                return lineReader.readLine();
            } catch (UserInterruptException e) {
                shutdown();
            }
            return null;
        }
    }
}
