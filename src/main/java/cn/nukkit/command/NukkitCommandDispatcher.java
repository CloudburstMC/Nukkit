package cn.nukkit.command;

import cn.nukkit.command.args.registry.ArgumentRegistry;
import cn.nukkit.command.defaults.*;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class NukkitCommandDispatcher {
    @Getter
    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final Map<String, BaseCommand> knownCommands = new HashMap<>();

    @Getter
    private ArgumentRegistry argumentRegistry = new ArgumentRegistry(); // TODO: move to server?

    private final Map<String, ParseResults<CommandSource>> commandResultCache = new HashMap<>();

    public NukkitCommandDispatcher() {
        registerAll("nukkit", new BaseCommand[]{
                new BanCommand(dispatcher),
                new BanIpCommand(dispatcher),
                new BanListCommand(dispatcher),
                new VersionCommand(dispatcher),
                new EffectCommand(dispatcher),
                new KickCommand(dispatcher),
                new ListCommand(dispatcher),
                new DeopCommand(dispatcher),
                new OpCommand(dispatcher),
                new SaveCommand(dispatcher),
                new SayCommand(dispatcher),
                new SeedCommand(dispatcher),
                new SaveOffCommand(dispatcher),
                new SaveOnCommand(dispatcher),
                new StopCommand(dispatcher),
                new PluginsCommand(dispatcher),
                new MeCommand(dispatcher),
                new HelpCommand(dispatcher),
                new WeatherCommand(dispatcher),
                new GiveCommand(dispatcher),
                new GameruleCommand(dispatcher),
                new DifficultyCommand(dispatcher),
                new KillCommand(dispatcher),
                new TellCommand(dispatcher),
                new GamemodeCommand(dispatcher),
                new PardonCommand(dispatcher),
                new PardonIpCommand(dispatcher),
                new DefaultGamemodeCommand(dispatcher),
                new TitleCommand(dispatcher),
                new DaylockCommand(dispatcher),

                // Debug
                new StatusCommand(dispatcher),
                new DebugPasteCommand(dispatcher),
                new GarbageCollectorCommand(dispatcher)
        });

        // Temporary test command to show all commands and usages
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("bhelp").executes(ctx -> {
            String[] usages = dispatcher.getAllUsage(dispatcher.getRoot(), ctx.getSource(), false);

            for(String usage : usages) {
                ctx.getSource().sendMessage(TextFormat.LIGHT_PURPLE + "/" + TextFormat.AQUA + usage);
            }

            return 1;
        }));


        // TODO: /enchant
        // TODO: /particle
        // TODO: /setworldspawn
        // TODO: /spawnpoint
        // TODO: /tp
        // TODO: /time
        // TODO: /timings
        // TODO: /whitelist
        // TODO: /xp

        // TODO: /clear
        // TODO: /setblock
    }

    public boolean quickDispatch(CommandSource source, String command) throws CommandSyntaxException {
        if(!knownCommands.containsKey(command)) {
            return false;
        }
        if(commandResultCache.containsKey(command)) {
            log.info("Retrieved command from cache: " + command);
            dispatcher.execute(commandResultCache.get(command));
            return true;
        }

        BaseCommand knownCommand = knownCommands.get(command);
        if(knownCommand.isCanResultsBeCached()) {
            ParseResults<CommandSource> results = dispatcher.parse(command, source);
            if(results.getExceptions().isEmpty()) {
                commandResultCache.put(command, results);

                dispatcher.execute(results);
                return true;
            }
            log.warn("Failed to parse command " + command, results.getExceptions());
        }

        dispatcher.execute(command, source);
        return true;
    }

    public boolean dispatch(CommandSource source, String command) throws CommandSyntaxException {
        dispatcher.execute(command, source);
        return true;
    }

    private void registerAll(String fallbackPrefix, BaseCommand[] commands) {
        for (BaseCommand command : commands) {
            register(fallbackPrefix, command);
        }
    }

    private void register(String fallbackPrefix, BaseCommand command) {
        // TODO
        knownCommands.put(command.getName(), command);
    }

    public BaseCommand getCommand(String name) {
        return knownCommands.get(name.toLowerCase());
    }

    public Map<String, BaseCommand> getCommands() {
        return knownCommands;
    }
}
