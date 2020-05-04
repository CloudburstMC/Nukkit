package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.command.*;
import cn.nukkit.command.defaults.*;
import cn.nukkit.command.simple.SimpleCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.data.CommandData;
import com.nukkitx.protocol.bedrock.packet.AvailableCommandsPacket;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>CommandRegistry</code> is used to register custom commands. Use the {@link #register(Plugin, String, CommandFactory) register()}
 * method to pass a <code>{@link CommandFactory}</code> object with your command name.
 * If the name is not unique, the registry will try to prefix the command with the plugin name
 * (ex: <code>nukkitx:commnad</code>)
 *
 * @author Sleepybear
 * @since API 2.0.0
 */
@Log4j2
public class CommandRegistry implements Registry {
    private final Matcher NAME_MATCHER = Pattern.compile("^[a-z0-9_\\-/\\.]+$").matcher("");
    private static final CommandRegistry INSTANCE = new CommandRegistry();
    private final Map<String, CommandFactory> factoryMap = new HashMap<>();
    private Map<String, Command> registeredCommands;
    private Map<String, String> knownAliases = new HashMap<>();

    private volatile boolean closed;

    private CommandRegistry() {
        registerDefaults();
        registerBuiltIn();
    }

    /**
     * Gets the instance of the CommandRegistry.
     *
     * @return the CommandRegistry singleton
     */
    public static CommandRegistry get() {
        return INSTANCE;
    }

    /**
     * Method used to register a custom command. Any aliases that are defined during the command constructor
     * will also be automatically registered. Aliases may also be registered outside of the command constructor
     * with the {@link #registerAlias(Plugin, String, String) registerAlias()} method, as long as the registry has not closed.
     *
     * @param name           The name of your command, which is how it will be run. Should be all lowercase.
     * @param commandFactory The {@link CommandFactory} that will produce your command. (ex. MyPluginCommand::new)
     * @throws RegistryException if command is unable to be registered
     */
    public synchronized void register(Plugin plugin, String name, CommandFactory commandFactory) throws RegistryException {
        Objects.requireNonNull(name, "command name");
        Objects.requireNonNull(commandFactory, "commandFactory");
        Objects.requireNonNull(plugin, "plugin");
        checkClosed();
        NAME_MATCHER.reset(name);
        Preconditions.checkArgument(NAME_MATCHER.matches(), "Invalid command name: %s", name);

        if (knownAliases.containsKey(name)) {
            log.warn("Command with name {} already exists, attempting to add prefix {}", name, plugin.getName());
            name = plugin.getName().toLowerCase() + ":" + name;
        }
        registerInternal(name, commandFactory);
    }

    private synchronized void registerInternal(String command, CommandFactory factory) {
        if (this.factoryMap.containsKey(command)) {
            throw new RegistryException("Command " + command + " already registered.");
        }
        this.factoryMap.put(command, factory);
        this.knownAliases.put(command, command);

        log.debug("Registered command: {}", command);

    }

    /**
     * Used to register {@link SimpleCommand}s created using the annotations found in <code>cn.nukkit.command.simple</code>
     * package.
     *
     * @param plugin        Reference to your {@link PluginBase}
     * @param simpleCommand Object reference to the class containing the SimpleCommand annotations.
     */
    public synchronized void registerSimpleCommand(Plugin plugin, Object simpleCommand) {
        Objects.requireNonNull(simpleCommand, "simpleCommand");
        Objects.requireNonNull(plugin, "plugin");
        checkClosed();

        for (Method method : simpleCommand.getClass().getDeclaredMethods()) {
            cn.nukkit.command.simple.Command def = method.getAnnotation(cn.nukkit.command.simple.Command.class);
            if (def != null) {
                String cmd = def.name();
                register(plugin, cmd, SimpleCommand.factory(simpleCommand, method, def.description(), def.usageMessage(), def.aliases()));
            }
        }
    }

    /**
     * This method registers an alias for your {@link PluginCommand Command}. Please note that any aliases assigned
     * to the command via the {@link Command#setAliases(String[]) setAliases()} method during the constructor
     * will be automatically registered. If the alias is already registered and also assigned during the
     * constructor, it will simply be skipped with no error.
     *
     * @param plugin A reference to your {@link cn.nukkit.plugin.PluginBase} class.
     * @param name   The name of your command. Should match the name used with {@link #register(Plugin, String, CommandFactory) register()}
     * @param alias  The alias you wish to register. If not unique, the Plugin name will attempted to be prefixed. (ex: nukkitx:command)
     * @throws RegistryException
     */
    public synchronized void registerAlias(Plugin plugin, String name, String alias) throws RegistryException {
        Objects.requireNonNull(name, "command name");
        Objects.requireNonNull(alias, "alias");
        checkClosed();
        if (!this.factoryMap.containsKey(name)) {
            throw new RegistryException("Unable to register alias " + alias + " as command " + name + " is not yet registered.");
        }

        if (this.knownAliases.containsKey(alias) && plugin != null) {
            log.warn("Alias {} already registered, trying with plugin prefix", alias);
            alias = plugin.getName().toLowerCase() + ":" + alias;
        }

        if (this.knownAliases.putIfAbsent(alias.toLowerCase(), name) != null) {
            throw new RegistryException("Unable to register alias " + alias + ", already registered");
        }
        log.debug("Registered alias: {} => {}", alias, name);
    }

    /**
     * Registers all the aliases in the set <code>aliases</code>, using the {@link #registerAlias(Plugin, String, String) registerAlias()}
     * method.
     *
     * @param plugin  A reference to your {@link PluginBase} plugin object
     * @param name    The name used when registering the {@link PluginCommand Command}
     * @param aliases The {@link Set} of String aliases to link to the above command
     */
    public void registerAliases(Plugin plugin, String name, String... aliases) {
        for (String alias : aliases) {
            registerAlias(plugin, name, alias);
        }
    }

    /**
     * Method used to unregister a command. Please note that a Plugin may only unregister it's own
     * commands, or a built in vanilla command.
     *
     * @param plugin A reference to your {@link PluginBase} instance
     * @param name   The command name, or alias for the command
     * @throws RegistryException
     */
    public void unregister(Plugin plugin, String name) throws RegistryException {
        Objects.requireNonNull(name, "commandName");
        Objects.requireNonNull(plugin, "plugin");
        checkClosed();
        if (!knownAliases.containsKey(name)) {
            log.warn("Attempted to unregister unknown command: {}", name);
            return;
        }

        String cmdName = knownAliases.get(name);
        Command cmd = factoryMap.get(cmdName).create(cmdName);

        if (cmd instanceof PluginCommand) {
            if (((PluginCommand) cmd).getPlugin() != plugin) {
                throw new RegistryException("Unable to unregister another plugin's command");
            }
        }
        unregisterInternal(cmdName);
    }

    private void unregisterInternal(String name) {
        factoryMap.remove(name);
        List<String> aliasesToRemove = new ArrayList<>();
        for (Map.Entry<String, String> entry : knownAliases.entrySet()) {
            if (entry.getValue().equals(name)) {
                aliasesToRemove.add(entry.getKey());
            }
        }
        for (String alias : aliasesToRemove)
            knownAliases.remove(alias);
    }

    private void checkClosed() {
        if (this.closed) {
            throw new RegistryException("Registration is closed");
        }
    }

    @Override
    public void close() throws RegistryException {
        checkClosed();
        ImmutableMap.Builder<String, Command> builder = new ImmutableMap.Builder<>();
        for (Map.Entry<String, CommandFactory> entry : factoryMap.entrySet()) {
            String cmdName = entry.getKey();
            Command cmd = entry.getValue().create(cmdName);
            String[] aliases = cmd.getAliases();
            builder.put(cmdName, cmd);
            for (String alias : aliases) {
                if (knownAliases.containsKey(alias) && knownAliases.get(alias).equalsIgnoreCase(cmdName)) {
                    continue;
                }
                registerAlias(cmd instanceof PluginCommand ? ((PluginCommand) cmd).getPlugin() : null, cmdName, alias);
            }
        }
        // Want to do this after all plugins have registered thier commands, so the aliases defined in nukkit.yml can use the plugin commands
        this.registerServerAliases(Server.getInstance(), builder);
        this.closed = true;
        this.registeredCommands = builder.build();
        this.knownAliases = ImmutableMap.copyOf(this.knownAliases);
    }

    /**
     * Returns a {@link Command} object by looking it up via name or alias
     *
     * @param name The name or alias of the command
     * @return The {@link Command} object, if known, otherwise <code>null</code>.
     */
    public Command getCommand(String name) {
        if (!this.knownAliases.containsKey(name)) {
            return null;
        }
        return this.registeredCommands.get(this.knownAliases.get(name));
    }

    /**
     * Returns all registered command and alias Strings
     *
     * @return A {@link Set} of String objects, of all known command names and aliases.
     */
    public Set<String> getCommandList() {
        return this.knownAliases.keySet();
    }

    /**
     * Used to obtain a Mapping of String name to Command objects. Note that this map
     * does not include aliases.
     *
     * @return An {@link ImmutableMap} of String command names to {@link Command} objects.
     */
    public Map<String, Command> getRegisteredCommands() {
        return this.registeredCommands;
    }

    /**
     * Used to check if a command is registered with the CommandRegistry
     *
     * @param cmd A reference to the Command object
     * @return <code>true</code> if registered, <code>false</code> otherwise
     */
    public boolean isRegistered(Command cmd) {
        return this.registeredCommands.containsValue(cmd);
    }

    /**
     * Used to check if a command is registered with the CommandRegistry
     *
     * @param cmd The command or alias name
     * @return <code>true</code> if registered, <code>false</code> otherwise
     */
    public boolean isRegistered(String cmd) {
        return this.knownAliases.containsKey(cmd);
    }

    /**
     * Used to obtain a Plugin Command from the registry.
     *
     * @param name The command name or alias
     * @return The {@link PluginCommand} associated with the command name, or <code>null</code>
     */
    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command cmd = this.getCommand(name);
        if (cmd instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) cmd;
        }
        return null;
    }

    /**
     * Used to dispatch a command. This method should be used over obtaining an object
     * reference to a Command and calling execute yourself.
     *
     * @param sender      The {@link CommandSender} source of the command.
     * @param commandLine The full command line to execute, including the command name.
     * @return <code>true</code> if the command was located and executed, <code>false</code> otherwise.
     */
    public boolean dispatch(CommandSender sender, String commandLine) {
        ArrayList<String> parsed = parseArguments(commandLine);
        if (parsed.size() == 0) {
            return false;
        }

        String sentCmd = parsed.remove(0).toLowerCase();
        if (!this.knownAliases.containsKey(sentCmd)) {
            if (sender instanceof ConsoleCommandSender && sentCmd.startsWith("/")) {
                sentCmd = sentCmd.substring(1);
                if (!this.knownAliases.containsKey(sentCmd))
                    return false;
            } else {
                return false;
            }
        }

        String[] args = parsed.toArray(new String[0]);
        Command target = this.registeredCommands.get(this.knownAliases.get(sentCmd));

        target.timing.startTiming();
        try {
            target.execute(sender, sentCmd, args);
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.exception"));
            log.error(Server.getInstance().getLanguage().translate("nukkit.command.exception", commandLine,
                    target.toString(), Utils.getExceptionMessage(e)));
        }
        target.timing.stopTiming();
        return true;
    }

    private ArrayList<String> parseArguments(String cmdLine) {
        StringBuilder sb = new StringBuilder(cmdLine);
        ArrayList<String> args = new ArrayList<>();
        boolean notQuoted = true;
        int start = 0;

        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\\') {
                sb.deleteCharAt(i);
                continue;
            }

            if (sb.charAt(i) == ' ' && notQuoted) {
                String arg = sb.substring(start, i);
                if (!arg.isEmpty()) {
                    args.add(arg);
                }
                start = i + 1;
            } else if (sb.charAt(i) == '"') {
                sb.deleteCharAt(i);
                --i;
                notQuoted = !notQuoted;
            }
        }

        String arg = sb.substring(start);
        if (!arg.isEmpty()) {
            args.add(arg);
        }
        return args;
    }

    /**
     * Used internally to obtain the AvailableCommandsPacket to send to a client.
     *
     * @param player
     * @return The Packet
     */
    public AvailableCommandsPacket createPacketFor(Player player) {
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        List<CommandData> data = pk.getCommands();
        for (Command command : this.registeredCommands.values()) {
            if (!command.testPermissionSilent(player)) {
                continue;
            }
            data.add(command.generateCustomCommandData(player));
        }
        return pk;
    }

    private void registerDefaults() {
        // TODO - Move this to a plugin to provide vanilla commands
        this.registerInternal("plugins", PluginsCommand::new);
        this.registerInternal("seed", SeedCommand::new);
        this.registerInternal("tell", TellCommand::new);
        this.registerInternal("defaultgamemode", DefaultGamemodeCommand::new);
        this.registerInternal("ban", BanCommand::new);
        this.registerInternal("ban-ip", BanIpCommand::new);
        this.registerInternal("banlist", BanListCommand::new);
        this.registerInternal("pardon", PardonCommand::new);
        this.registerInternal("pardon-ip", PardonIpCommand::new);
        this.registerInternal("say", SayCommand::new);
        this.registerInternal("me", MeCommand::new);
        this.registerInternal("list", ListCommand::new);
        this.registerInternal("difficulty", DifficultyCommand::new);
        this.registerInternal("kick", KickCommand::new);
        this.registerInternal("op", OpCommand::new);
        this.registerInternal("deop", DeopCommand::new);
        this.registerInternal("whitelist", WhitelistCommand::new);
        this.registerInternal("give", GiveCommand::new);
        this.registerInternal("effect", EffectCommand::new);
        this.registerInternal("enchant", EnchantCommand::new);
        this.registerInternal("particle", ParticleCommand::new);
        this.registerInternal("gamemode", GamemodeCommand::new);
        this.registerInternal("gamerule", GameruleCommand::new);
        this.registerInternal("kill", KillCommand::new);
        this.registerInternal("spawnpoint", SpawnpointCommand::new);
        this.registerInternal("setworldspawn", SetWorldSpawnCommand::new);
        this.registerInternal("tp", TeleportCommand::new);
        this.registerInternal("time", TimeCommand::new);
        this.registerInternal("title", TitleCommand::new);
        this.registerInternal("weather", WeatherCommand::new);
        this.registerInternal("xp", XpCommand::new);
    }

    private void registerBuiltIn() {
        this.registerInternal("help", HelpCommand::new);
        this.registerInternal("version", VersionCommand::new);
        this.registerInternal("status", StatusCommand::new);
        this.registerInternal("gc", GarbageCollectorCommand::new);
        this.registerInternal("timings", TimingsCommand::new);
        this.registerInternal("debugpaste", DebugPasteCommand::new);
        this.registerInternal("save-on", SaveOnCommand::new);
        this.registerInternal("save-off", SaveOffCommand::new);
        this.registerInternal("save-all", SaveCommand::new);
        this.registerInternal("stop", StopCommand::new);
    }

    private void registerServerAliases(Server server, ImmutableMap.Builder<String, Command> builder) {
        Map<String, List<String>> values = server.getCommandAliases();
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            String alias = entry.getKey();
            List<String> commands = entry.getValue();

            if (alias.contains(" ") || alias.contains(":")) {
                log.warn(server.getLanguage().translate("nukkit.command.alias.illegal", alias));
                continue;
            }

            List<String> targets = new ArrayList<>();
            StringBuilder bad = new StringBuilder();

            for (String command : commands) {
                String[] args = command.split(" ");
                Command cmd = this.getCommand(args[0]);

                if (cmd == null) {
                    if (bad.length() > 0) {
                        bad.append(", ");
                    }
                    bad.append(command);
                } else {
                    targets.add(command);
                }
            }

            if (bad.length() > 0) {
                log.warn(server.getLanguage().translate("nukkit.command.alias.notFound", alias, bad.toString()));
                continue;
            }
            alias = alias.toLowerCase();
            if (!targets.isEmpty()) {
                builder.put(alias, new FormattedCommandAlias(alias, targets));
                this.knownAliases.putIfAbsent(alias, alias);
            }
        }
    }
}
