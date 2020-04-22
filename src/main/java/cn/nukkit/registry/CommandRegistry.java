package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.FormattedCommandAlias;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.defaults.*;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
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
    private final Server server = Server.getInstance();
    private final Map<Identifier, Command> registeredCommands = new IdentityHashMap<>();
    private final Map<String, Identifier> knownAliases = new HashMap<>();

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
     * Method used to register a custom command.
     *
     * @param id      The {@link Identifier} of your command, should be unique
     * @param command The {@link Command} object you are registering.
     * @throws RegistryException if command is already registered with this id
     */
    public synchronized void register(Identifier id, Command command) throws RegistryException {
        Objects.requireNonNull(id, "identifier");
        Objects.requireNonNull(command, "command");
        checkClosed();
        String label = command.getName();
        if (knownAliases.containsKey(label)) {
            label = id.getNamespace() + ":" + label;
        }
        if (this.registeredCommands.containsKey(id) || this.knownAliases.containsKey(label)) {
            throw new RegistryException("Command " + id + " is already registered.");
        }

        this.registeredCommands.put(id, command);
        this.knownAliases.put(label, id);
        log.debug("Registered command: {} => {}", id, label);

        if (command.getAliases() != null && command.getAliases().length != 0) {
            this.registerAliases(id, command.getAliases());
        }
    }

    public synchronized void registerAlias(Identifier id, String alias) throws RegistryException {
        Objects.requireNonNull(id, "identifier");
        Objects.requireNonNull(alias, "alias");
        checkClosed();
        if (this.knownAliases.containsKey(alias)) {
            alias = id.getNamespace() + ":" + alias;
            log.warn("Alias already registered, trying with alias: {}", alias);
        }
        if (this.knownAliases.putIfAbsent(alias.toLowerCase(), id) != null) {
            throw new RegistryException("Unable to register alias " + alias + ", already registered");
        }
        log.debug("Registered alias: {} => {}", alias, id);
    }

    public void registerAliases(Identifier id, String... aliases) {
        for (String alias : aliases) {
            registerAlias(id, alias);
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
        this.registerServerAliases(); // Want to do this after all plugins have registered thier commands
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

    public Map<Identifier, Command> getRegisteredCommands() {
        return this.registeredCommands;
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
            log.error(server.getLanguage().translate("nukkit.command.exception", commandLine,
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
        this.register(Identifier.from("nukkit", "plugins"), new PluginsCommand("plugins"));
        this.register(Identifier.from("nukkit", "seed"), new SeedCommand("seed"));
        this.register(Identifier.from("nukkit", "stop"), new StopCommand("stop"));
        this.register(Identifier.from("nukkit", "tell"), new TellCommand("tell"));
        this.register(Identifier.from("nukkit", "defaultgamemode"), new DefaultGamemodeCommand("defaultgamemode"));
        this.register(Identifier.from("nukkit", "ban"), new BanCommand("ban"));
        this.register(Identifier.from("nukkit", "banip"), new BanIpCommand("ban-ip"));
        this.register(Identifier.from("nukkit", "banlist"), new BanListCommand("banlist"));
        this.register(Identifier.from("nukkit", "pardon"), new PardonCommand("pardon"));
        this.register(Identifier.from("nukkit", "pardonip"), new PardonIpCommand("pardon-ip"));
        this.register(Identifier.from("nukkit", "say"), new SayCommand("say"));
        this.register(Identifier.from("nukkit", "me"), new MeCommand("me"));
        this.register(Identifier.from("nukkit", "list"), new ListCommand("list"));
        this.register(Identifier.from("nukkit", "difficulty"), new DifficultyCommand("difficulty"));
        this.register(Identifier.from("nukkit", "kick"), new KickCommand("kick"));
        this.register(Identifier.from("nukkit", "op"), new OpCommand("op"));
        this.register(Identifier.from("nukkit", "deop"), new DeopCommand("deop"));
        this.register(Identifier.from("nukkit", "whitelist"), new WhitelistCommand("whitelist"));
        this.register(Identifier.from("nukkit", "give"), new GiveCommand("give"));
        this.register(Identifier.from("nukkit", "effect"), new EffectCommand("effect"));
        this.register(Identifier.from("nukkit", "enchant"), new EnchantCommand("enchant"));
        this.register(Identifier.from("nukkit", "particle"), new ParticleCommand("particle"));
        this.register(Identifier.from("nukkit", "gamemode"), new GamemodeCommand("gamemode"));
        this.register(Identifier.from("nukkit", "gamerule"), new GameruleCommand("gamerule"));
        this.register(Identifier.from("nukkit", "kill"), new KillCommand("kill"));
        this.register(Identifier.from("nukkit", "spawnpoint"), new SpawnpointCommand("spawnpoint"));
        this.register(Identifier.from("nukkit", "setworldspawn"), new SetWorldSpawnCommand("setworldspawn"));
        this.register(Identifier.from("nukkit", "tp"), new TeleportCommand("tp"));
        this.register(Identifier.from("nukkit", "time"), new TimeCommand("time"));
        this.register(Identifier.from("nukkit", "title"), new TitleCommand("title"));
        this.register(Identifier.from("nukkit", "weather"), new WeatherCommand("weather"));
        this.register(Identifier.from("nukkit", "xp"), new XpCommand("xp"));

    }

    private void registerBuiltIn() {
        this.register(Identifier.from("nukkit", "help"), new HelpCommand("help"));
        this.register(Identifier.from("nukkitx", "version"), new VersionCommand("version"));
        this.register(Identifier.from("nukkit", "status"), new StatusCommand("status"));
        this.register(Identifier.from("nukkit", "gc"), new GarbageCollectorCommand("gc"));
        this.register(Identifier.from("nukkit", "timings"), new TimingsCommand("timings"));
        this.register(Identifier.from("nukkit", "debugpaste"), new DebugPasteCommand("debugpaste"));
        this.register(Identifier.from("nukkit", "reload"), new ReloadGeneratorCommand("reload"));
        this.register(Identifier.from("nukkit", "saveon"), new SaveOnCommand("save-on"));
        this.register(Identifier.from("nukkit", "saveoff"), new SaveOffCommand("save-off"));
        this.register(Identifier.from("nukkit", "saveall"), new SaveCommand("save-all"));
    }

    public void registerServerAliases() {
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
                this.register(Identifier.from("nukkitx", "server_alias_" + count++), new FormattedCommandAlias(alias.toLowerCase(), targets));
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
