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
import com.mojang.brigadier.tree.CommandNode;

import java.util.Iterator;
import java.util.Map;

import static cn.nukkit.command.args.EffectArgument.effect;
import static cn.nukkit.command.args.EffectArgument.getEffect;
import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class EffectCommand extends BaseCommand {

    public EffectCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("effect", "%nukkit.command.effect.description");

        // TODO: try and get around this, its not pretty
        dispatcher.register(literal("effect")
                .requires(requirePermission("nukkit.command.effect"))
                .then(argument("player", player()) // TODO: Support for entities too
                        .then(literal("clear").executes(this::clear))
                        .then(argument("effect", effect()).executes(ctx -> run(ctx, 30, 0, false))
                            .then(argument("seconds", integer()).executes(ctx -> run(ctx, getInteger(ctx, "seconds"), 0, false))
                                    .then(argument("amplifier", integer()).executes(ctx -> run(ctx, getInteger(ctx, "seconds"), getInteger(ctx, "amplifier"), false))
                                            .then(argument("hideParticles", bool()).executes(ctx -> run(ctx, getInteger(ctx, "seconds"), getInteger(ctx, "amplifier"), getBool(ctx, "hideParticles")))))))));
    }

    public int run(CommandContext<CommandSource> context, int duration, int amplifier, boolean hideParticles) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        Effect effect = getEffect(context, "effect");

        if (!(effect instanceof InstantEffect)) {
            duration *= 20;
        }
        else {
            duration = 1;
        }

        if(hideParticles) {
            effect.setVisible(true);
        }

        if (duration == 0) {
            if (!target.hasEffect(effect.getId())) {
                if (target.getEffects().size() == 0) {
                    target.sendMessage(new TranslationContainer("commands.effect.failure.notActive.all", target.getDisplayName()));
                } else {
                    target.sendMessage(new TranslationContainer("commands.effect.failure.notActive", effect.getName(), target.getDisplayName()));
                }
                return 1;
            }
            target.removeEffect(effect.getId());
            target.sendMessage(new TranslationContainer("commands.effect.success.removed", effect.getName(), target.getDisplayName()));
        } else {
            effect.setDuration(duration).setAmplifier(amplifier);
            target.addEffect(effect);
            sendAdminMessage(source, new TranslationContainer("%commands.effect.success", effect.getName(), String.valueOf(effect.getAmplifier()), target.getDisplayName(), String.valueOf(effect.getDuration() / 20)));
        }
        return 1;
    }

    public int clear(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");

        if(target == null && source instanceof Player) {
            target = (Player) source;
        }

        target.removeAllEffects();
        source.sendMessage(new TranslationContainer("commands.effect.success.removed.all", target.getDisplayName()));
        return 1;
    }
}
