package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.InstantEffect;
import cn.nukkit.utils.ServerException;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static cn.nukkit.command.args.EffectArgument.effect;
import static cn.nukkit.command.args.EffectArgument.getEffect;
import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class EffectCommand extends BaseCommand {

    public EffectCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("effect", "%nukkit.command.effect.description");

        dispatcher.register(literal("effect")
                .requires(requirePermission("nukkit.command.effect"))
                    .then(argument("player", player())
                        .then(literal("clear").executes(ctx -> clear(ctx, getPlayer(ctx, "player")))))
                    .then(argument("effect", effect()))
                    .then(argument("seconds", integer()))
                        .executes(ctx -> run(ctx, getPlayer(ctx, "player"), getEffect(ctx, "effect"), getInteger(ctx, "seconds"), 0, false)));
//                    .then(argument("amplifier", string()) // TODO
//                    .then(argument("hideParticles", bool()))))
    }

    public int run(CommandContext<CommandSource> context, Player target, Effect effect, int seconds, int amplifier,
                   boolean hideParticles) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        return 1;
    }

    public int clear(CommandContext<CommandSource> context, Player target) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

        if(target == null && source instanceof Player) {
            target = (Player) source;
        }

        for (Effect effect : target.getEffects().values()) {
            target.removeEffect(effect.getId());
        }

        source.sendMessage(new TranslationContainer("commands.effect.success.removed.all", target.getDisplayName()));
        return 1;
    }

//    public EffectCommand(String name) {
//        super(name, "%nukkit.command.effect.description", "%commands.effect.usage");
//        this.setPermission("nukkit.command.effect");
//        this.commandParameters.clear();
//        this.commandParameters.put("default", new CommandParameter[]{
//                new CommandParameter("player", CommandParamType.TARGET, false),
//                new CommandParameter("effect", CommandParamType.STRING, false), //Do not use Enum here because of buggy behavior
//                new CommandParameter("seconds", CommandParamType.INT, true),
//                new CommandParameter("amplifier", true),
//                new CommandParameter("hideParticle", true, new String[]{"true", "false"})
//        });
//        this.commandParameters.put("clear", new CommandParameter[]{
//                new CommandParameter("player", CommandParamType.TARGET, false),
//                new CommandParameter("clear", new String[]{"clear"})
//        });
//    }
//
//    @Override
//    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
//        if (!this.testPermission(sender)) {
//            return true;
//        }
//        if (args.length < 2) {
//            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
//            return true;
//        }
//        Player player = sender.getServer().getPlayer(args[0]);
//        if (player == null) {
//            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
//            return true;
//        }
//        if (args[1].equalsIgnoreCase("clear")) {
//            for (Effect effect : player.getEffects().values()) {
//                player.removeEffect(effect.getId());
//            }
//            sender.sendMessage(new TranslationContainer("commands.effect.success.removed.all", player.getDisplayName()));
//            return true;
//        }
//        Effect effect;
//        try {
//            effect = Effect.getEffect(Integer.parseInt(args[1]));
//        } catch (NumberFormatException | ServerException a) {
//            try {
//                effect = Effect.getEffectByName(args[1]);
//            } catch (Exception e) {
//                sender.sendMessage(new TranslationContainer("commands.effect.notFound", args[1]));
//                return true;
//            }
//        }
//        int duration = 300;
//        int amplification = 0;
//        if (args.length >= 3) {
//            try {
//                duration = Integer.valueOf(args[2]);
//            } catch (NumberFormatException a) {
//                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
//                return true;
//            }
//            if (!(effect instanceof InstantEffect)) {
//                duration *= 20;
//            }
//        } else if (effect instanceof InstantEffect) {
//            duration = 1;
//        }
//        if (args.length >= 4) {
//            try {
//                amplification = Integer.valueOf(args[3]);
//            } catch (NumberFormatException a) {
//                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
//                return true;
//            }
//        }
//        if (args.length >= 5) {
//            String v = args[4].toLowerCase();
//            if (v.matches("(?i)|on|true|t|1")) {
//                effect.setVisible(false);
//            }
//        }
//        if (duration == 0) {
//            if (!player.hasEffect(effect.getId())) {
//                if (player.getEffects().size() == 0) {
//                    sender.sendMessage(new TranslationContainer("commands.effect.failure.notActive.all", player.getDisplayName()));
//                } else {
//                    sender.sendMessage(new TranslationContainer("commands.effect.failure.notActive", effect.getName(), player.getDisplayName()));
//                }
//                return true;
//            }
//            player.removeEffect(effect.getId());
//            sender.sendMessage(new TranslationContainer("commands.effect.success.removed", effect.getName(), player.getDisplayName()));
//        } else {
//            effect.setDuration(duration).setAmplifier(amplification);
//            player.addEffect(effect);
//            Command.broadcastCommandMessage(sender, new TranslationContainer("%commands.effect.success", effect.getName(), String.valueOf(effect.getAmplifier()), player.getDisplayName(), String.valueOf(effect.getDuration() / 20)));
//        }
//        return true;
//    }
}
