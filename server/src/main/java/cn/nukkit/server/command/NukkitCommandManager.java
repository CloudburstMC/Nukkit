package cn.nukkit.server.command;

import cn.nukkit.api.Player;
import cn.nukkit.api.command.*;
import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.command.vanilla.VersionCommand;
import cn.nukkit.server.network.minecraft.packet.AvailableCommandsPacket;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NukkitCommandManager implements CommandManager {
    private final Map<String, Command> commandMap = new ConcurrentHashMap<>();
    private final NukkitServer server;
    private CommandData defaultCommandData;

    public NukkitCommandManager(NukkitServer server) {
        this.server = server;
        setDefaultCommands();
    }

    private void setDefaultCommands() {
        try {
            register("minecraft", new VersionCommand(), true);
        } catch (CommandException e) {
            throw new RuntimeException("Unable to register default commands", e);
        }
        /*try {
            register("nukkit", new VersionCommand(server));
            register("nukkit", new PluginsCommand());
            register("nukkit", new SeedCommand("seed"));
            register("nukkit", new HelpCommand("help"));
            register("nukkit", new StopCommand("stop"));
            register("nukkit", new TellCommand("tell"));
            register("nukkit", new DefaultGamemodeCommand("defaultgamemode"));
            register("nukkit", new BanCommand("ban"));
            register("nukkit", new BanIpCommand("ban-ip"));
            register("nukkit", new BanListCommand("banlist"));
            register("nukkit", new PardonCommand("pardon"));
            register("nukkit", new PardonIpCommand("pardon-ip"));
            register("nukkit", new SayCommand("say"));
            register("nukkit", new MeCommand("me"));
            register("nukkit", new ListCommand("list"));
            register("nukkit", new DifficultyCommand("difficulty"));
            register("nukkit", new KickCommand("kick"));
            register("nukkit", new OpCommand("op"));
            register("nukkit", new DeopCommand("deop"));
            register("nukkit", new WhitelistCommand("whitelist"));
            register("nukkit", new SaveOnCommand("save-on"));
            register("nukkit", new SaveOffCommand("save-off"));
            register("nukkit", new SaveCommand("save-all"));
            register("nukkit", new GiveCommand("give"));
            register("nukkit", new EffectCommand("effect"));
            register("nukkit", new EnchantCommand("enchant"));
            register("nukkit", new ParticleCommand("particle"));
            register("nukkit", new GamemodeCommand("gamemode"));
            register("nukkit", new KillCommand("kill"));
            register("nukkit", new SpawnpointCommand("spawnpoint"));
            register("nukkit", new SetWorldSpawnCommand("setworldspawn"));
            register("nukkit", new TeleportCommand("tp"));
            register("nukkit", new TimeCommand("time"));
            register("nukkit", new TimingsCommand("timings"));
            register("nukkit", new TitleCommand("title"));
            register("nukkit", new ReloadCommand("reload"));
            register("nukkit", new WeatherCommand("weather"));
            register("nukkit", new XpCommand("xp"));
        } catch (CommandException e) {
            throw new AssertionError("Could not register default commands", e);
        }*/
    }

    private void register(@Nonnull String fallbackPrefix, @Nonnull Command command, boolean override) throws CommandException {
        Preconditions.checkNotNull(fallbackPrefix, "fallbackPrefix");
        Preconditions.checkNotNull(command, "command");
        String fallbackCommand = fallbackPrefix + ":" + command.getName();
        Command fallback = commandMap.get(fallbackCommand);
        Command previousCommand = commandMap.get(command.getName());

        if ((fallback != null || previousCommand != null) && !override) {
            throw new CommandRegisterException("Command already registered");
        }

        commandMap.put(fallbackCommand, command);
        commandMap.put(command.getName(), command);

        for (String alias : command.getData().getAliases()) {
            if (commandMap.putIfAbsent(alias, command) != null && override) {
                commandMap.put(alias, command);
            }
        }
    }

    @Override
    public void register(@Nonnull Plugin plugin, @Nonnull Command command) throws CommandException {
        Preconditions.checkNotNull(plugin, "plugin");
        register(plugin.getName(), command, false);
    }

    @Override
    public CommandData getDefaultData() {
        return defaultCommandData;
    }

    @Override
    public void setDefaultData(@Nonnull CommandData data) {
        this.defaultCommandData = Preconditions.checkNotNull(data, "data");
    }

    public void executeCommand(CommandSender sender, String command) throws CommandException {
        Preconditions.checkNotNull(sender, "sender");
        Preconditions.checkNotNull(command, "command");

        //TODO: Better regex
        String[] args = command.trim().split(" ");
        Command executor = commandMap.get(args[0]);
        if (executor == null) {
            throw new CommandNotFoundException(args[0]);
        }

        try {
            executor.onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    public void unregister(@Nonnull String commandName, @Nonnull Command command) {
        Preconditions.checkNotNull(commandName, "commandName");
        Preconditions.checkNotNull(command, "command");
        Command foundCommand = commandMap.get(commandName);
        if (foundCommand == command) {
            commandMap.remove(commandName);
        }

        for (String alias : command.getData().getAliases()) {
            Command cmd = commandMap.get(alias);
            if (cmd == command) {
                commandMap.remove(alias);
            }
        }
    }

    public AvailableCommandsPacket createAvailableCommandsPacket(Player player) {
        //TODO: command parameters
        return null;
    }

    public void clear() {

    }

    public Collection<Command> getCommands() {
        return ImmutableList.copyOf(commandMap.values());
    }

    public Collection<String> getCommandNames() {
        return ImmutableList.copyOf(commandMap.keySet());
    }
}
