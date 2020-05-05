package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.*;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.*;
import cn.nukkit.command.simple.*;
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
 * <code>CommandRegistry</code> is used to register custom commands. Use the {@link #register(Plugin, Command) register()}
 * method to pass a <code>{@link PluginCommand}</code> object with a reference to your {@link PluginBase Plugin}.
 * If the name used in the Command constructor is not unique, the registry will try to prefix the command with the
 * lower cased version of your plugin name (ex: <code>nukkitx:commnad</code>).
 * <p/>You may also use the {@link cn.nukkit.command.simple SimpleCommand} annotations to create your Command,
 * in which case you would use {@link #registerSimpleCommand(Plugin, Object) registerSimpleCommand()}
 * method to register the command.
 *
 * @author Sleepybear
 * @see PluginCommand
 * @since API 2.0.0
 */
@Log4j2
public class CommandRegistry implements Registry {
    private final Matcher NAME_MATCHER = Pattern.compile("^[a-z0-9_?\\-/\\.]+$").matcher("");
    private static final CommandRegistry INSTANCE = new CommandRegistry();
    private Map<String, Command> registeredCommands = new HashMap<>();
    private Map<String, String> knownAliases = new HashMap<>();

    private volatile boolean closed;

    private CommandRegistry() {

    }

    public void registerVanilla() {
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
     * Method used to register a custom command. Any aliases that are defined in the
     * {@link cn.nukkit.command.data.CommandData CommandData} created during the Command construction are also
     * registered. Your Command should extend from {@link PluginCommand}.<p/>If you are using the {@link SimpleCommand}
     * annotations to create your command, you should register it using
     * {@link #registerSimpleCommand(Plugin, Object) registerSimpleCommand()}.
     *
     * @param plugin  A reference to your {@link PluginBase Plugin}
     * @param command The {@link PluginCommand Command} object of your Command.
     * @throws RegistryException if command is unable to be registered
     */
    public synchronized void register(Plugin plugin, Command command) throws RegistryException {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(plugin, "plugin");
        checkClosed();
        String name = command.getName();
        NAME_MATCHER.reset(name);
        Preconditions.checkArgument(NAME_MATCHER.matches(), "Invalid command name: %s", name);

        if (knownAliases.containsKey(name)) {
            log.warn("Command with name {} already exists, attempting to add prefix {}", name, plugin.getName());
            name = plugin.getName().toLowerCase() + ":" + name;
            command.setRegisteredName(name);
        }
        registerInternal(name, command);
    }

    private synchronized void registerInternal(String cmdName, Command command) {
        if (this.registeredCommands.containsKey(cmdName)) {
            throw new RegistryException("Command " + cmdName + " already registered.");
        }
        this.registeredCommands.put(cmdName, command);
        this.knownAliases.put(cmdName, cmdName);
        if (command.getAliases().length > 0) {
            for (String alias : command.getAliases()) {
                registerAlias(cmdName, alias);
            }
        }
    }

    /**
     * Used to register {@link SimpleCommand}s created using the annotations found in <code>cn.nukkit.command.simple</code>
     * package.
     *
     * @param plugin        Reference to your {@link PluginBase Plugin}
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
                NAME_MATCHER.reset(cmd);
                if (!NAME_MATCHER.matches()) {
                    log.error("Invalid Command name in SimpleCommand: {}, skipping", cmd);
                    continue;
                }

                String perms = "";
                CommandPermission perm = method.getAnnotation(CommandPermission.class);
                if (perm != null) {
                    perms = perm.value();
                }

                List<CommandParameter[]> params = new ArrayList<>();
                CommandParameters parameters = method.getAnnotation(CommandParameters.class);
                if (parameters != null) {
                    for (Parameters p : parameters.parameters()) {
                        List<CommandParameter> p2 = new ArrayList<>();
                        for (Parameter parameter : p.parameters()) {
                            p2.add(new CommandParameter(parameter.name(), parameter.type(), parameter.optional()));
                        }
                        params.add(p2.toArray(new CommandParameter[0]));
                    }
                }

                SimpleCommand sc = new SimpleCommand(simpleCommand, method, cmd, def.description(), def.usageMessage(), def.aliases(), perms, params);
                Arguments args = method.getAnnotation(Arguments.class);
                if (args != null) {
                    sc.setMaxArgs(args.max());
                    sc.setMinArgs(args.min());
                }

                if (method.isAnnotationPresent(ForbidConsole.class)) {
                    sc.setForbidConsole(true);
                }

                register(plugin, sc);
            }
        }
    }

    private synchronized void registerAlias(String cmdName, String alias) throws RegistryException {
        Objects.requireNonNull(cmdName, "command");
        Objects.requireNonNull(alias, "alias");
        checkClosed();

        if (!this.registeredCommands.containsKey(cmdName)) {
            throw new RegistryException("Unable to register alias " + alias + " as command " + cmdName + " is not yet registered.");
        }

        NAME_MATCHER.reset(alias);
        Preconditions.checkArgument(NAME_MATCHER.matches(), "Invalid alias name: %s", alias);

        if (this.knownAliases.containsKey(alias)) {
            Command cmd = this.registeredCommands.get(cmdName);
            if (cmd instanceof PluginCommand) {
                log.warn("Alias {} already registered, trying with plugin prefix", alias);
                alias = ((PluginCommand) cmd).getPlugin().getName().toLowerCase() + ":" + alias;
            }
        }

        if (this.knownAliases.putIfAbsent(alias, cmdName) != null) {
            throw new RegistryException("Unable to register alias " + alias + ", already registered");
        }
    }

    /**
     * Method used to unregister a command. Please note that a Plugin may only unregister it's own
     * commands, or a built in command.
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

        Command cmd = registeredCommands.get(knownAliases.get(name));

        if (cmd instanceof PluginCommand) {
            if (((PluginCommand) cmd).getPlugin() != plugin) {
                throw new RegistryException("Unable to unregister another plugin's command");
            }
        }
        unregisterInternal(cmd);
    }

    private void unregisterInternal(Command cmd) {
        String name = cmd.getRegisteredName();
        registeredCommands.remove(name);
        List<String> aliasesToRemove = new ArrayList<>();
        for (Map.Entry<String, String> entry : knownAliases.entrySet()) {
            if (entry.getValue().equals(name)) {
                aliasesToRemove.add(entry.getKey());
            }
        }
        for (String alias : aliasesToRemove)
            knownAliases.remove(alias);
    }

    /**
     * Unregisters an alias for one of your Plugin's {@link PluginCommand Commands}, or for a
     * built-in command.
     *
     * @param plugin A reference to your {@link PluginBase Plugin}
     * @param alias  The alias to unregister
     * @throws RegistryException on attempt to unregister another Plugin's alias
     */
    public void unregisterAlias(Plugin plugin, String alias) throws RegistryException {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(alias, "alias");
        checkClosed();

        if (!knownAliases.containsKey(alias)) {
            log.warn("Attempted to unregister unknown alias: {}", alias);
            return;
        }

        if (registeredCommands.containsKey(alias)) {
            log.warn("Attempted to unregister alias {}, but it is the base command name.", alias);
            return;
        }
        Command cmd = registeredCommands.get(knownAliases.get(alias));
        if (cmd instanceof PluginCommand && ((PluginCommand) cmd).getPlugin() != plugin) {
            throw new RegistryException("Plugins may not unregister another Plugin's command aliases");
        }
        knownAliases.remove(alias);
        cmd.removeAlias(alias);
    }

    private void checkClosed() {
        if (this.closed) {
            throw new RegistryException("Registration is closed");
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() throws RegistryException {
        checkClosed();

        // Want to do this after all plugins have registered thier commands,
        // so the aliases defined in nukkit.yml can use the plugin commands
        this.registerServerAliases(Server.getInstance());
        this.closed = true;
        this.registeredCommands = ImmutableMap.copyOf(this.registeredCommands);
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
            boolean success = target.execute(sender, sentCmd, args);
            if (!success && target.getUsage().length() > 0) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", target.getUsage()));
            }
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
     * @param player The player reciving the packet
     * @return The Packet
     */
    public AvailableCommandsPacket createPacketFor(Player player) {
        AvailableCommandsPacket pk = new AvailableCommandsPacket();
        List<CommandData> data = pk.getCommands();
        for (Command command : this.registeredCommands.values()) {
            if (!command.testPermissionSilent(player)) {
                continue;
            }
            data.add(command.toNetwork(player));
        }
        return pk;
    }

    private void registerDefaults() {
        // TODO - Move this to a plugin to provide vanilla commands
        this.registerInternal("ban", new BanCommand());
        this.registerInternal("ban-ip", new BanIpCommand());
        this.registerInternal("banlist", new BanListCommand());
        this.registerInternal("defaultgamemode", new DefaultGamemodeCommand());
        this.registerInternal("deop", new DeopCommand());
        this.registerInternal("difficulty", new DifficultyCommand());
        this.registerInternal("effect", new EffectCommand());
        this.registerInternal("enchant", new EnchantCommand());
        this.registerInternal("gamemode", new GamemodeCommand());
        this.registerInternal("gamerule", new GameruleCommand());
        this.registerInternal("give", new GiveCommand());
        this.registerInternal("kick", new KickCommand());
        this.registerInternal("kill", new KillCommand());
        this.registerInternal("list", new ListCommand());
        this.registerInternal("me", new MeCommand());
        this.registerInternal("op", new OpCommand());
        this.registerInternal("pardon", new PardonCommand());
        this.registerInternal("pardon-ip", new PardonIpCommand());
        this.registerInternal("particle", new ParticleCommand());
        this.registerInternal("say", new SayCommand());
        this.registerInternal("seed", new SeedCommand());
        this.registerInternal("setworldspawn", new SetWorldSpawnCommand());
        this.registerInternal("spawnpoint", new SpawnpointCommand());
        this.registerInternal("tp", new TeleportCommand());
        this.registerInternal("tell", new TellCommand());
        this.registerInternal("time", new TimeCommand());
        this.registerInternal("title", new TitleCommand());
        this.registerInternal("weather", new WeatherCommand());
        this.registerInternal("whitelist", new WhitelistCommand());
        this.registerInternal("xp", new XpCommand());
    }

    private void registerBuiltIn() {
        this.registerInternal("debugpaste", new DebugPasteCommand());
        this.registerInternal("gc", new GarbageCollectorCommand());
        this.registerInternal("help", new HelpCommand());
        this.registerInternal("plugins", new PluginsCommand());
        this.registerInternal("save-all", new SaveCommand());
        this.registerInternal("save-off", new SaveOffCommand());
        this.registerInternal("save-on", new SaveOnCommand());
        this.registerInternal("status", new StatusCommand());
        this.registerInternal("stop", new StopCommand());
        this.registerInternal("timings", new TimingsCommand());
        this.registerInternal("version", new VersionCommand());
    }

    private void registerServerAliases(Server server) {
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
                registerInternal(alias, new FormattedCommandAlias(alias, targets));
            }
        }
    }
}
