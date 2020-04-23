package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.command.*;
import cn.nukkit.command.defaults.*;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import com.nukkitx.protocol.bedrock.data.CommandData;
import com.nukkitx.protocol.bedrock.packet.AvailableCommandsPacket;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * CommandRegistry is used to register custom commands. Use the <code>register</code>
 * method to pass a <code>{@link Command}</code> object with your unique <code>{@link Identifier}</code>
 */
@Log4j2
public class CommandRegistry implements Registry {
    private static final CommandRegistry INSTANCE = new CommandRegistry();
    private final Map<String, CommandFactory> factoryMap = new HashMap<>();
    private final Map<String, Command> registeredCommands = new HashMap<>();
    private final Map<String, String> knownAliases = new HashMap<>();

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
     * Method used to register a custom command. Any aliases
     *
     * @param name           The name of your command, which is how it will be run. Should be all lowercase.
     * @param commandFactory The {@link CommandFactory} that will produce your command. (ex. CommandClass::new)
     * @throws RegistryException if command is unable to be registered
     */
    public synchronized void register(Plugin plugin, String name, CommandFactory commandFactory) throws RegistryException {
        Objects.requireNonNull(name, "command name");
        Objects.requireNonNull(commandFactory, "commandFactory");
        checkClosed();
        // Pattern match to make sure name is lowercase? or just lowercase it?

        if (knownAliases.containsKey(name) && plugin != null) {
            log.warn("Command with name {} already exists, attempting to add prefix {}", name, plugin.getName());
            name = plugin.getName().toLowerCase() + ":" + name;
        }

        if (this.factoryMap.containsKey(name) || this.knownAliases.containsKey(name)) {
            throw new RegistryException("Command " + name + " is already registered.");
        }

        this.factoryMap.put(name, commandFactory);
        this.knownAliases.put(name, name);

        log.debug("Registered command: {} ", name);
    }

    private synchronized void registerInternal(String command, CommandFactory factory) {
        if (this.factoryMap.containsKey(command)) {
            throw new RegistryException("Command " + command + " already registered.");
        }
        this.register(null, command, factory);
    }

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

    public void registerAliases(Plugin plugin, String name, String... aliases) {
        for (String alias : aliases) {
            registerAlias(plugin, name, alias);
        }
    }

    private void checkClosed() {
        if (this.closed) {
            throw new RegistryException("Registration is closed");
        }
    }

    @Override
    public void close() throws RegistryException {
        checkClosed();

        for (Map.Entry<String, CommandFactory> entry : factoryMap.entrySet()) {
            String cmdName = entry.getKey();
            Command cmd = entry.getValue().create(cmdName);
            String[] aliases = cmd.getAliases();
            this.registeredCommands.putIfAbsent(cmdName, cmd);
            for (String alias : aliases) {
                if (knownAliases.containsKey(alias) && knownAliases.get(alias).equalsIgnoreCase(cmdName)) {
                    continue;
                }
                registerAlias(cmd instanceof PluginCommand ? ((PluginCommand) cmd).getPlugin() : null, cmdName, alias);
            }
        }
        this.registerServerAliases(Server.getInstance()); // Want to do this after all plugins have registered thier commands
        this.closed = true;
    }

    public Command getCommand(String name) {
        if (!this.knownAliases.containsKey(name)) {
            return null;
        }
        return this.registeredCommands.get(this.knownAliases.get(name));
    }

    public Set<String> getCommandList() {
        return this.knownAliases.keySet();
    }

    public Map<String, Command> getRegisteredCommands() {
        return this.registeredCommands;
    }

    public boolean isRegistered(Command cmd) {
        return this.registeredCommands.containsValue(cmd);
    }

    public boolean isRegistered(Identifier id) {
        return this.registeredCommands.containsKey(id);
    }

    public boolean dispatch(CommandSender sender, String commandLine) {
        ArrayList<String> parsed = parseArguments(commandLine);
        if (parsed.size() == 0) {
            return false;
        }

        String sentCmd = parsed.remove(0).toLowerCase();
        if (!this.knownAliases.containsKey(sentCmd)) {
            return false;
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
        this.registerInternal("kill", KickCommand::new);
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
        this.registerInternal("reload", ReloadGeneratorCommand::new);
        this.registerInternal("save-on", SaveOnCommand::new);
        this.registerInternal("save-off", SaveOffCommand::new);
        this.registerInternal("save-all", SaveCommand::new);
        this.registerInternal("stop", StopCommand::new);
    }

    public void registerServerAliases(Server server) {
        Map<String, List<String>> values = server.getCommandAliases();
        int count = 0;
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

            if (!targets.isEmpty()) {
                this.registerInternal(alias.toLowerCase(), FormattedCommandAlias.factory(targets));
            }
        }
    }

    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command cmd = this.getCommand(name);
        if (cmd instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) cmd;
        }
        return null;
    }
}
