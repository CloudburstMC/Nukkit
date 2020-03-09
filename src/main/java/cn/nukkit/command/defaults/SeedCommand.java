package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class SeedCommand extends BaseCommand {

    public SeedCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("seed", "%commands.seed.description");

        dispatcher.register(literal("seed")
                .requires(requirePermission("nukkit.command.seed"))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        long seed;
        if (source instanceof Player) {
            seed = ((Player) source).getLevel().getSeed();
        } else {
            seed = source.getServer().getDefaultLevel().getSeed();
        }

        source.sendMessage(new TranslationContainer("commands.seed.success", seed));
        return 1;
    }
}
