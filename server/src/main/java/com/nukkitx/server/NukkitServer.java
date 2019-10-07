package com.nukkitx.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.flowpowered.math.GenericMath;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nukkitx.api.Player;
import com.nukkitx.api.Server;
import com.nukkitx.api.command.CommandException;
import com.nukkitx.api.command.CommandNotFoundException;
import com.nukkitx.api.command.MessageRecipient;
import com.nukkitx.api.command.PluginCommand;
import com.nukkitx.api.command.sender.ConsoleCommandSender;
import com.nukkitx.api.event.server.ServerInitializationEvent;
import com.nukkitx.api.event.server.ServerShutdownEvent;
import com.nukkitx.api.event.server.ServerStartEvent;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.LevelCreator;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.chunk.generator.ChunkGenerator;
import com.nukkitx.api.message.Message;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.api.permission.Permissible;
import com.nukkitx.api.util.ConfigBuilder;
import com.nukkitx.api.util.SemVer;
import com.nukkitx.event.SimpleEventManager;
import com.nukkitx.network.NetworkListener;
import com.nukkitx.plugin.SimplePluginManager;
import com.nukkitx.plugin.loader.JavaPluginLoader;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockServer;
import com.nukkitx.protocol.bedrock.v361.Bedrock_v361;
import com.nukkitx.server.block.NukkitBlockStateBuilder;
import com.nukkitx.server.command.NukkitCommandManager;
import com.nukkitx.server.console.*;
import com.nukkitx.server.entity.EntitySpawner;
import com.nukkitx.server.item.NukkitItemStackBuilder;
import com.nukkitx.server.level.LevelManager;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.level.NukkitLevelStorage;
import com.nukkitx.server.level.generator.NukkitChunkGeneratorRegistry;
import com.nukkitx.server.level.provider.ChunkProvider;
import com.nukkitx.server.level.provider.DefaultsLevelDataProvider;
import com.nukkitx.server.level.provider.LevelDataProvider;
import com.nukkitx.server.locale.NukkitLocaleManager;
import com.nukkitx.server.network.NukkitBedrockEventHandler;
import com.nukkitx.server.network.SessionManager;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import com.nukkitx.server.permission.NukkitAbilities;
import com.nukkitx.server.permission.NukkitPermissionManager;
import com.nukkitx.server.resourcepack.ResourcePackManager;
import com.nukkitx.server.scheduler.ServerScheduler;
import com.nukkitx.server.util.ServerKiller;
import com.nukkitx.service.SimpleServiceManager;
import com.spotify.futures.CompletableFutures;
import io.netty.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@Getter
public class NukkitServer implements Server {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final YAMLMapper YAML_MAPPER = (YAMLMapper) new YAMLMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final JavaPropsMapper PROPERTIES_MAPPER = (JavaPropsMapper) new JavaPropsMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static final BedrockPacketCodec MINECRAFT_CODEC = Bedrock_v361.V361_CODEC;
    public static final String NAME;
    public static final SemVer API_VERSION;
    public static final String NUKKIT_VERSION;
    public static final SemVer MINECRAFT_VERSION;
    @Getter(AccessLevel.NONE)
    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    @Getter(AccessLevel.NONE)
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";
    private static NukkitServer instance = null;
    @Getter(AccessLevel.NONE)
    private final boolean ansiEnabled;
    @Getter(AccessLevel.NONE)
    private final ScheduledExecutorService timerService = Executors.unconfigurableScheduledExecutorService(
            Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Nukkit Ticker").setDaemon(true).build()));
    private final NukkitConsoleCommandSender consoleCommandSender = new NukkitConsoleCommandSender(this);
    private final NukkitChunkGeneratorRegistry generatorRegistry = new NukkitChunkGeneratorRegistry(this);
    private final ResourcePackManager resourcePackManager = new ResourcePackManager(this);
    private final NukkitCommandManager commandManager = new NukkitCommandManager(this);
    private final NukkitPermissionManager permissionManager = new NukkitPermissionManager();
    private final NukkitLocaleManager localeManager = new NukkitLocaleManager();
    private final EntitySpawner entitySpawner = new EntitySpawner(this);
    private final SimpleEventManager eventManager = new SimpleEventManager();
    private final SimplePluginManager pluginManager = new SimplePluginManager(eventManager);
    private final SimpleServiceManager serviceManager = new SimpleServiceManager();
    private final SessionManager sessionManager = new SessionManager();
    private final ServerScheduler scheduler = new ServerScheduler(this);
    @Getter(AccessLevel.NONE)
    private final List<NetworkListener> listeners = new ArrayList<>();
    @Getter(AccessLevel.NONE)
    private final LevelManager levelManager = new LevelManager();
    private final Path pluginPath;
    private final Path jarPath;
    private final Path dataPath;
    private final Path playersPath;
    private final Path levelsPath;
    private BedrockServer bedrockServer;
    private NukkitConfiguration configuration;
    private NukkitWhitelist whitelist;
    private NukkitOperators operators;
    private NukkitLevel defaultLevel;
    private NukkitBanlist banlist;
    @Getter(AccessLevel.NONE)
    private LineReader lineReader;
    @Getter(AccessLevel.NONE)
    private AtomicBoolean reading = new AtomicBoolean(false);
    @Getter(AccessLevel.NONE)
    private BlockingQueue<String> inputLines;
    @Getter(AccessLevel.NONE)
    private ConsoleReader consoleReader;
    @Getter(AccessLevel.NONE)
    private AtomicBoolean running = new AtomicBoolean(true);

    static {
        String name;
        SemVer apiVersion;
        SemVer minecraftVersion;
        String nukkitVersion;
        Package mainPackage = NukkitServer.class.getPackage();

        try {
            name = Preconditions.checkNotNull(mainPackage.getImplementationTitle());
            apiVersion = SemVer.fromString(mainPackage.getSpecificationVersion().split("-")[0]);
            minecraftVersion = SemVer.fromString(mainPackage.getImplementationVersion().split("-")[0]);
            nukkitVersion = Preconditions.checkNotNull(mainPackage.getImplementationVendor());
        } catch (NullPointerException e) {
            name = "Nukkit";
            apiVersion = new SemVer(0, 0, 0);
            minecraftVersion = new SemVer(0, 0, 0);
            nukkitVersion = "git-Nukkit-UNKNOWN";
        }

        NAME = name;
        API_VERSION = apiVersion;
        MINECRAFT_VERSION = minecraftVersion;
        NUKKIT_VERSION = nukkitVersion;
    }

    public NukkitServer(final Path filePath, final Path dataPath, final Path pluginPath, final boolean ansiEnabled) throws IOException {
        this.jarPath = filePath;
        this.dataPath = dataPath;
        this.pluginPath = pluginPath;
        this.ansiEnabled = ansiEnabled;
        this.playersPath = dataPath.resolve("players");
        this.levelsPath = dataPath.resolve("levels");
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

        Thread.currentThread().setName("Main Thread");

        TranslatableMessage.setLanguageManager(localeManager);

        // Get terminal
        Terminal terminal = NukkitConsoleAppender.getTerminal();

        // Build line reader
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
        } else {
            log.error("Console reader could not be initialized! You will not be able to input into the console.");
        }

        inputLines = new LinkedBlockingQueue<>();

        // Setup console reader
        if (reader != null) {
            lineReader = reader;
            consoleReader = new ConsoleReader();

            consoleReader.setName("Console Reader");
            consoleReader.start();
        }

        // Load configuration
        loadConfiguration();

        // Load other files
        loadBanlist();
        loadOperators();
        loadWhitelist();

        // Set logger level as soon as we have the configuration
        LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        Configuration log4jConfig = ctx.getConfiguration();
        LoggerConfig loggerConfig = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(org.apache.logging.log4j.Level.toLevel(configuration.getAdvanced().getLogLevel(), org.apache.logging.log4j.Level.INFO));
        ctx.updateLoggers();

        // Set the default locale.
        Locale locale = localeManager.getLocaleByString(configuration.getGeneral().getLocale());
        if (!localeManager.isLocaleAvailable(locale)) {
            if (localeManager.isFallbackAvailable()) {
                log.info("Locale not available. Reverting to US");
                locale = Locale.US;
            } else {
                throw new IllegalStateException("Selected and fallback Locale could not be loaded");
            }
        }
        Locale.setDefault(locale);

        if (!localeManager.loadDefaultLocale(locale)) {
            log.fatal("Unable to load locale. Shutting down...");
            return;
        }

        /*if (NativeCodeFactory.cipher.load()) {
            log.debug("Loaded native cipher");
        }

        if (NativeCodeFactory.zlib.load()) {
            log.debug("Loaded native compression");
        }

        if (NativeCodeFactory.hash.load()) {
            log.debug("Loaded native hash");
        }*/

        // Register plugin loaders
        pluginManager.registerLoader(JavaPluginLoader.class, JavaPluginLoader.builder()
                .registerDependency(NukkitServer.class, this)
                .registerDependency(Server.class, this)
                .build()
        );

        // Load plugins
        loadPlugins();

        eventManager.fire(ServerInitializationEvent.INSTANCE);

        // Setup default LevelData values
        DefaultsLevelDataProvider defaultsLevelData = new DefaultsLevelDataProvider(this);
        defaultsLevelData.saveAsDefault();

        // Check folders
        if (Files.notExists(levelsPath)) {
            Files.createDirectories(levelsPath);
        }
        if (Files.notExists(playersPath)) {
            Files.createDirectories(playersPath);
        }
        if (!Files.isDirectory(levelsPath)) {
            throw new RuntimeException("levels/ is not a directory");
        }
        if (!Files.isDirectory(playersPath)) {
            throw new RuntimeException("players/ is not a directory");
        }

        // Load default level
        LinkedHashMap<String, NukkitConfiguration.NukkitLevelConfiguration> levelConfigs = configuration.getLevels();
        if (levelConfigs.isEmpty()) {
            throw new RuntimeException("No levels to load");
        }

        // Load other levels
        List<CompletableFuture<Level>> loadingLevels = new ArrayList<>();

        String defaultId = null;
        for (Map.Entry<String, NukkitConfiguration.NukkitLevelConfiguration> entry : configuration.getLevels().entrySet()) {
            NukkitConfiguration.NukkitLevelConfiguration levelConfig = entry.getValue();
            LevelCreator levelCreator = LevelCreator.builder()
                    .name(entry.getKey())
                    .id(levelConfig.getId())
                    .levelPath(levelsPath.resolve(levelConfig.getId()))
                    .generator(levelConfig.getGenerator())
                    .readOnly(levelConfig.isReadOnly())
                    .generatorSettings(levelConfig.getGeneratorSettings())
                    .loadSpawnChunks(levelConfig.isLoadingSpawnChunks())
                    .storage(LevelCreator.LevelStorage.valueOf(levelConfig.getFormat()))
                    .build();

            if (defaultId == null) {
                defaultId = levelCreator.getId();
            }

            loadingLevels.add(loadLevel(levelCreator));
        }

        for (CompletableFuture<Level> loadingLevel : loadingLevels) {
            Level loadedLevel = null;
            try {
                loadedLevel = loadingLevel.join();
            } catch (Throwable e) {
                log.fatal("Unable to load level", e);
                System.exit(1);
            }
            if (loadedLevel.getId().equals(defaultId)) {
                defaultLevel = (NukkitLevel) loadedLevel;
            }
        }

        if (defaultLevel == null) {
            log.fatal("No default level specified.");
            System.exit(1);
        }

        log.info(TranslatableMessage.of("nukkit.server.networkStart", configuration.getNetwork().getAddress(), Integer.toString(configuration.getNetwork().getPort())));
        if (configuration.getNetwork().isQueryEnabled()) {
            log.info(TranslatableMessage.of("nukkit.server.query.start"));
        }
        int configNetThreads = configuration.getAdvanced().getNetworkThreads();
        int maxThreads = configNetThreads < 1 ? Runtime.getRuntime().availableProcessors() : configNetThreads;

        InetSocketAddress bindAddress = new InetSocketAddress(configuration.getNetwork().getAddress(), configuration.getNetwork().getPort());
        this.bedrockServer = new BedrockServer(bindAddress, maxThreads);
        bedrockServer.setHandler(new NukkitBedrockEventHandler(this));
        bedrockServer.bind().join();

        /*if (configuration.getRcon().isEnabled()) {
            RconNetworkListener rconListener = new RconNetworkListener(
                    new NukkitRconEventListener(),
                    configuration.getRcon().getPassword().getBytes(StandardCharsets.UTF_8),
                    configuration.getRcon().getAddress(),
                    configuration.getRcon().getPort()
            );
            configuration.getRcon().clearPassword();
            rconListener.bind();
            listeners.add(rconListener);
        }*/

        if (configuration.getGeneral().isAutoSaving()) {
            int autoSaveInterval = configuration.getGeneral().getAutoSaveInterval();
            timerService.scheduleAtFixedRate(this::doAutoSave, autoSaveInterval, autoSaveInterval, TimeUnit.MINUTES);
            // TODO: Keep ScheduledFuture so auto save can be disabled whilst the server is loaded.
        }

        if (ansiEnabled) {
            timerService.scheduleAtFixedRate(this::titleTick, 500, 500, TimeUnit.MILLISECONDS);
        }

        eventManager.fire(ServerStartEvent.INSTANCE);

        loop();
    }

    private void loop() {

        while (running.get()) {
            try {
                String command = inputLines.take();
                try {
                    commandManager.executeCommand(consoleCommandSender, command);
                } catch (CommandNotFoundException e) {
                    consoleCommandSender.sendMessage(new TranslationMessage("commands.generic.unknown", command));
                } catch (CommandException e) {
                    consoleCommandSender.sendMessage(new TranslationMessage("commands.generic.exception"));
                    log.error("An error occurred whilst running command " + command + " for " + getName(), e);
                }
            } catch (Exception e) {
                log.error("Exception occurred whilst ticking main thread", e);
            }
        }

        shutdown();
    }

    @Override
    public void broadcastMessage(@Nonnull String message) {
        broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    @Nonnull
    @Override
    public String getName() {
        return NAME;
    }

    @Nonnull
    @Override
    public String getNukkitVersion() {
        return NUKKIT_VERSION;
    }

    @Nonnull
    @Override
    public SemVer getMinecraftVersion() {
        return MINECRAFT_VERSION;
    }

    @Nonnull
    @Override
    public SemVer getApiVersion() {
        return API_VERSION;
    }

    @Nonnull
    @Override
    public NukkitAbilities getDefaultAbilities() {
        return new NukkitAbilities(); //TODO: implement defaults
    }


    public void broadcastMessage(Message message) {
        broadcastMessage(message, getOnlinePlayers());
    }

    public void broadcastMessage(String message, Collection<Player> recipients) {
        for (Player recipient : recipients) {
            recipient.sendMessage(message);
        }
    }

    public void broadcastMessage(Message message, Collection<Player> recipients) {
        for (Player recipient : recipients) {
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

    public ConsoleCommandSender getConsoleSender() {
        return consoleCommandSender;
    }

    public void shutdown() {
        shutdown(configuration.getGeneral().getShutdownMessage());
    }

    public void shutdown(@Nonnull String reason) {
        Preconditions.checkNotNull(reason, "reason");
        try {
            // Stop reading the console
            reading.compareAndSet(true, false);
            running.compareAndSet(true, false);

            // Disconnect all players
            getSessionManager().allPlayers().forEach(p -> p.disconnect(reason));

            saveBanlist();

            saveOperators();

            saveWhitelist();

            // Safely disable plugins.

            eventManager.fire(ServerShutdownEvent.INSTANCE);

            this.pluginManager.deregisterLoader(JavaPluginLoader.class);

            // Unload all levels.
            levelManager.getLevels().forEach(this::unloadLevel);

            // Shutdown logger
            LogManager.shutdown();

            // If still running then it's time to kill
            if (running.get()) {
                ServerKiller killer = new ServerKiller(90);
                killer.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void doAutoSave() {
        // Save player data
        for (Player player : getSessionManager().allPlayers()) {
            if (player instanceof PlayerSession) {
                ((PlayerSession) player).save();
            }
        }

        levelManager.getLevels().forEach(Level::save);
    }

    private void titleTick() {
        Runtime runtime = Runtime.getRuntime();
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        double load = GenericMath.round(osMXBean.getSystemLoadAverage() / osMXBean.getAvailableProcessors(), 2);
        double used = ((runtime.totalMemory() - runtime.freeMemory()) / 1048576D);
        double max = (runtime.maxMemory() / 1048576D);
        String usage = Math.round((used / max) * 100) + "%";
        String title = (char) 0x1b + "]0;" + NAME + " " + MINECRAFT_VERSION + " | Online " +
                sessionManager.playerSessionCount() + "/" + configuration.getGeneral().getMaximumPlayers() +
                " | Memory " + usage + " | Load " + load + "%" + (char) 0x07;
        System.out.print(title);
    }

    @Nonnull
    @Override
    public Collection<Player> getOnlinePlayers() {
        return sessionManager.allPlayers();
    }

    @Nonnull
    public Optional<Player> getPlayer(@Nonnull UUID uuid) {
        return Optional.ofNullable(sessionManager.getPlayer(uuid));
    }

    @Nonnull
    @Override
    public Optional<Player> getPlayer(@Nonnull String name) {
        return Optional.ofNullable(sessionManager.getPlayer(name));
    }

    public Optional<Player> getPlayerExact(@Nonnull String name) {
        name = name.toLowerCase();
        for (Player player : sessionManager.allPlayers()) {
            if (player.getName().toLowerCase().equals(name)) {
                return Optional.of(player);
            }
        }

        return Optional.empty();
    }

    public Player[] matchPlayer(String partialName) {
        partialName = partialName.toLowerCase();
        List<Player> matchedPlayer = new ArrayList<>();
        for (Player player : sessionManager.allPlayers()) {
            if (player.getName().toLowerCase().equals(partialName)) {
                return new Player[]{player};
            } else if (player.getName().toLowerCase().contains(partialName)) {
                matchedPlayer.add(player);
            }
        }

        return matchedPlayer.toArray(new Player[0]);
    }

    public Optional<Level> getLevel(String id) {
        for (Level level : levelManager.getLevels()) {
            if (level.getId().equals(id)) {
                return Optional.of(level);
            }
        }
        return Optional.empty();
    }

    public Locale getLanguage() {
        return Locale.getDefault();
    }

    @Nullable
    public PluginCommand getPluginCommand(String name) {
        /*Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginCommand) command;
        } else {*/
            return null;
        //}
    }

    @Nonnull
    public NukkitOperators getOperators() {
        return operators;
    }

    public boolean isWhitelisted(@Nonnull String name) {
        return !configuration.getGeneral().isWhitelisted() || whitelist.isWhitelisted(name) || operators.isOperator(name);
    }

    public boolean isWhitelisted(@Nonnull Player player) {
        return !configuration.getGeneral().isWhitelisted() || whitelist.isWhitelisted(player) || operators.isOperator(player);
    }

    public boolean isWhitelisted(@Nonnull UUID uuid) {
        return !configuration.getGeneral().isWhitelisted() || whitelist.isWhitelisted(uuid) || operators.isOperator(uuid);
    }

    @Nonnull
    @Override
    public NukkitItemStackBuilder itemInstanceBuilder() {
        return new NukkitItemStackBuilder();
    }

    @Nonnull
    @Override
    public NukkitBlockStateBuilder blockStateBuilder() {
        return new NukkitBlockStateBuilder();
    }

    @Nonnull
    @Override
    public ConfigBuilder createConfigBuilder() {
        throw new UnsupportedOperationException();
    }

    private void loadPlugins() {
        log.info("Loading Plugins...");
        try {
            Path pluginPath = dataPath.resolve("plugins");
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

    private void loadConfiguration() throws Exception {
        log.info("Loading nukkit.yml...");
        Path configFile = dataPath.resolve("nukkit.yml");
        try {
            configuration = NukkitConfiguration.load(configFile);
            if (configuration.populateMissingFields()) {
                NukkitConfiguration.save(configFile, configuration);
            }
        } catch (NoSuchFileException e) {
            // If file is not a regular, we need to delete.
            Files.deleteIfExists(configFile);

            Locale locale = Locale.US;
            if (consoleReader != null) {
                reading.set(false); // Stop reading so we can use it
                StringBuilder builder = new StringBuilder();
                for (Locale availLocale : localeManager.availableLocales()) {
                    builder.append("\n").append(availLocale.toString()).append(": ").append(availLocale.getDisplayName(availLocale));
                }
                log.info(builder.toString());

                Locale newLocale = null;
                while (newLocale == null) {
                    Locale l = null;
                    try {
                        String input = inputLines.take();
                        l = localeManager.getLocaleByString(input);
                    } catch (Exception ex) {
                        // Ignore
                    }
                    if (l != null && localeManager.isLocaleAvailable(l)) {
                        newLocale = l;
                    } else {
                        log.info("Unknown language. Please try again");
                    }
                }
                locale = newLocale;
            }
            log.info("{} selected", locale.getDisplayName(locale));

            InputStream advancedConf = NukkitServer.class.getClassLoader().getResourceAsStream("lang/nukkit/" + locale.toString() + ".yml");
            if (advancedConf != null) {
                configuration = NukkitConfiguration.load(advancedConf);
            } else {
                configuration = NukkitConfiguration.defaultConfiguration();
                configuration.getGeneral().setLocale(locale.toString());
            }
            configuration.populateMissingFields();
            NukkitConfiguration.save(configFile, configuration);
        }
    }

    private void saveConfiguration() throws Exception {
        Path configFile = dataPath.resolve("nukkit.yml");
        NukkitConfiguration.save(configFile, configuration);
    }

    private void loadOperators() throws IOException {
        Path operatorsPath = dataPath.resolve("ops.json");
        try {
            operators = NukkitOperators.load(operatorsPath);
        } catch (NoSuchFileException e) {
            operators = NukkitOperators.defaultConfiguration();
            NukkitOperators.save(operatorsPath, operators);
        }
    }

    private void saveOperators() throws IOException {
        Preconditions.checkNotNull(operators, "operators");
        Path operatorsPath = dataPath.resolve("ops.json");
        NukkitOperators.save(operatorsPath, operators);
    }

    private void loadBanlist() throws IOException {
        Path bansPath = dataPath.resolve("bans.json");
        try {
            banlist = NukkitBanlist.load(bansPath);
        } catch (NoSuchFileException e) {
            banlist = NukkitBanlist.defaultConfiguration();
            NukkitBanlist.save(bansPath, banlist);
        }
    }

    private void saveBanlist() throws IOException {
        Preconditions.checkNotNull(banlist, "banlist");
        Path banPath = dataPath.resolve("bans.json");
        NukkitBanlist.save(banPath, banlist);
    }

    private void loadWhitelist() throws Exception {
        Path whitelistFile = dataPath.resolve("whitelist.json");
        try {
            whitelist = NukkitWhitelist.load(whitelistFile);
        } catch (NoSuchFileException e) {
            whitelist = NukkitWhitelist.defaultConfiguration();
            NukkitWhitelist.save(whitelistFile, whitelist);
        }
    }

    private void saveWhitelist() throws IOException {
        Preconditions.checkNotNull(whitelist, "whitelist");
        Path whitelistPath = dataPath.resolve("whitelist.json");
        NukkitWhitelist.save(whitelistPath, whitelist);
    }

    private void loadResourcePacks() throws IOException {
        Path resourcePackPath = dataPath.resolve("resourcePacks");
        try {
            resourcePackManager.loadResourcePacks(resourcePackPath, null);
        } catch (IOException e) {
            Files.deleteIfExists(resourcePackPath);
            Files.createDirectories(resourcePackPath);
        }
    }

    @Nonnull
    @Override
    public CompletableFuture<Level> loadLevel(@Nonnull LevelCreator creator) {
        Preconditions.checkNotNull(creator, "creator");
        log.info(TranslatableMessage.of("nukkit.level.preparing", creator.getName()));

        CompletableFuture<LevelDataProvider> stage1 = new CompletableFuture<>();

        NukkitLevelStorage storage = NukkitLevelStorage.fromApi(creator.getStorage());

        ChunkProvider provider = storage.createChunkProvider(creator.getLevelPath());

        ForkJoinPool.commonPool().execute(() -> {
            try {
                LevelDataProvider levelDataProvider = storage.createLevelDataProvider(creator.getLevelPath().resolve("level.dat"));
                stage1.complete(levelDataProvider);
            } catch (IOException e) {
                stage1.completeExceptionally(e);
            }
        });

        CompletableFuture<Level> stage2 = stage1.thenApplyAsync(levelDataProvider -> {
            Optional<ChunkGenerator> chunkGeneratorFactory = getGeneratorRegistry().getChunkGenerator(creator.getGenerator());
            Preconditions.checkState(chunkGeneratorFactory.isPresent(), "Unknown chunk generator");
            NukkitLevel level = new NukkitLevel(NukkitServer.this, creator.getId(), provider, levelDataProvider, chunkGeneratorFactory.get());
            levelManager.register(level);
            levelManager.start(level);
            return level;
        });

        if (creator.isLoadSpawnChunks()) {
            CompletableFuture<Level> stage3 = new CompletableFuture<>();
            stage2.whenComplete(((level, throwable) -> {
                if (throwable != null) {
                    stage3.completeExceptionally(throwable);
                    return;
                }

                log.info(TranslatableMessage.of("nukkit.level.preparing", level.getId()));

                int spawnChunkX = level.getData().getDefaultSpawn().getFloorX();
                int spawnChunkZ = level.getData().getDefaultSpawn().getFloorZ();
                List<CompletableFuture<Chunk>> chunkFutures = new ArrayList<>();
                final int spawnRadius = configuration.getAdvanced().getSpawnChunkRadius();
                for (int x = -spawnRadius; x <= spawnRadius; x++) {
                    for (int z = -spawnRadius; z <= spawnRadius; z++) {
                        chunkFutures.add(level.getChunk(spawnChunkX + x, spawnChunkZ + z));
                    }
                }
                CompletableFuture<List<Chunk>> chunksFuture = CompletableFutures.allAsList(chunkFutures);
                chunksFuture.whenComplete((chunks, chunkThrow) -> {
                    if (chunkThrow != null) {
                        log.error("Unable to load spawn chunks for {}", level.getId());
                        stage3.completeExceptionally(chunkThrow);
                    } else {
                        log.info("Successfully loaded spawn chunks for {}", level.getId());
                        stage3.complete(level);
                    }
                });
            }));
            return stage3;
        }
        return stage2;
    }

    public boolean unloadLevel(Level level) {
        /* TODO: Implement unloading
        Preconditions.checkNotNull(level, "level")
        Preconditions.checkArgument(level instanceof NukkitLevel, "level is not a Nukkit Level");
        levelManager.stop(level);
        levelManager.deregister(level);
        ((NukkitLevel) level).getChunkManager()
        */
        return false;
    }

    private class ConsoleReader extends Thread implements InterruptibleThread {

        @Override
        public void run() {
            String line;
            try {
                reading.set(true);
                while (running.get()) {
                    try {
                        // Read line
                        line = lineReader.readLine("> ");
                        inputLines.offer(line);
                    } catch (UserInterruptException | NullPointerException e) {
                        shutdown();
                    } catch (EndOfFileException e) {
                        // Ignore
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Console unable to read", e);
            }
        }
    }
}
