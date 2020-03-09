package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class TitleCommand extends BaseCommand {

    public TitleCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("title", "commands.title.description");

        dispatcher.register(literal("title")
                .requires(requirePermission("nukkit.command.title"))
                .then(argument("player", player())
                    .then(literal("clear").executes(this::clear))
                    .then(literal("reset").executes(this::reset))
                        // TODO: have this all in one enum literal argument in the future to prevent duplication
                    .then(literal("title").then(argument("titleText", greedyString()).executes(this::setTitle)))
                    .then(literal("subtitle").then(argument("titleText", greedyString()).executes(this::setSubtitle)))
                    .then(literal("actionbar").then(argument("titleText", greedyString()).executes(this::setActionBar)))
                    .then(literal("times")
                        .then(argument("fadeIn", integer())
                            .then(argument("stay", integer())
                                .then(argument("fadeOut", integer())
                                    .executes(this::times)))))));
    }

    public int clear(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");

        target.clearTitle();
        source.sendMessage(new TranslationContainer("commands.title.success"));
        return 1;
    }

    public int reset(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");

        target.resetTitleSettings();
        source.sendMessage(new TranslationContainer("commands.title.success"));
        return 1;
    }

    public int setTitle(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        String text = getString(context, "titleText");

        target.setTitle(text);
        source.sendMessage(new TranslationContainer("commands.title.success"));
        return 1;
    }

    public int setSubtitle(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        String text = getString(context, "titleText");

        target.setSubtitle(text);
        source.sendMessage(new TranslationContainer("commands.title.success"));
        return 1;
    }

    public int setActionBar(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        String text = getString(context, "titleText");

        target.setActionBar(text);
        source.sendMessage(new TranslationContainer("commands.title.success"));
        return 1;
    }

    public int times(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        int fadeIn = getInteger(context, "fadeIn");
        int stay = getInteger(context, "stay");
        int fadeOut = getInteger(context, "fadeOut");

        target.setTitleAnimationTimes(fadeIn, stay, fadeOut);

        source.sendMessage(new TranslationContainer("commands.title.success"));
        return 1;
    }
}
