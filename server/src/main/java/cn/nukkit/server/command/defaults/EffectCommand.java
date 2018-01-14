package cn.nukkit.server.command.defaults;

import cn.nukkit.api.MessageRecipient;
import cn.nukkit.api.command.Command;
import cn.nukkit.api.command.CommandExecutorSource;
import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.NukkitCommand;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.potion.Effect;
import cn.nukkit.server.potion.InstantEffect;
import cn.nukkit.server.util.ServerException;
import cn.nukkit.server.util.TextFormat;

/**
 * Created by Snake1999 and Pub4Game on 2016/1/23.
 * Package cn.nukkit.server.command.defaults in project nukkit.
 */
public class EffectCommand extends NukkitCommand {
    public EffectCommand(String name) {
        super(name, "%nukkit.command.effect.description", "%commands.effect.usage");
        this.setPermission("nukkit.command.effect");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                new CommandParameter("effect", CommandParameter.ARG_TYPE_STRING, false), //Do not use Enum here because of buggy behavior
                new CommandParameter("seconds", CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter("amplifier", true),
                new CommandParameter("hideParticle", CommandParameter.ARG_TYPE_BOOL, true)
        });
        this.commandParameters.put("clear", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                new CommandParameter("clear", new String[]{"clear"})
        });
    }

    @Override
    public boolean execute(CommandExecutorSource sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        boolean canReceiveMessage = (sender instanceof MessageRecipient);
        if (args.length < 2 && canReceiveMessage) {
            ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
            return true;
        }
        Player player = sender.getServer().getPlayer(args[0]);
        if (player == null && canReceiveMessage) {
            ((MessageRecipient) sender).sendMessage(new TranslatedMessage(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }
        if (args[1].equalsIgnoreCase("clear") && canReceiveMessage) {
            for (Effect effect : player.getEffects().values()) {
                player.removeEffect(effect.getId());
            }
            ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.effect.success.removed.all", player.getDisplayName()));
            return true;
        }
        Effect effect;
        try {
            effect = Effect.getEffect(Integer.parseInt(args[1]));
        } catch (NumberFormatException | ServerException a) {
            try {
                effect = Effect.getEffectByName(args[1]);
            } catch (Exception e) {
                if (canReceiveMessage) {
                    ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.effect.notFound", args[1]));
                }
                return true;
            }
        }
        int duration = 300;
        int amplification = 0;
        if (args.length >= 3) {
            try {
                duration = Integer.valueOf(args[2]);
            } catch (NumberFormatException a) {
                if (canReceiveMessage) {
                    ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
                }
                return true;
            }
            if (!(effect instanceof InstantEffect)) {
                duration *= 20;
            }
        } else if (effect instanceof InstantEffect) {
            duration = 1;
        }
        if (args.length >= 4) {
            try {
                amplification = Integer.valueOf(args[3]);
            } catch (NumberFormatException a) {
                ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
                return true;
            }
        }
        if (args.length >= 5) {
            String v = args[4].toLowerCase();
            if (v.matches("(?i)|on|true|t|1")) {
                effect.setVisible(false);
            }
        }
        if (duration == 0) {
            if (!player.hasEffect(effect.getId())) {
                if (canReceiveMessage) {
                    if (player.getEffects().size() == 0) {
                        ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.effect.failure.notActive.all", player.getDisplayName()));
                    } else {
                        ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.effect.failure.notActive", effect.getName(), player.getDisplayName()));
                    }
                }
                return true;
            }
            player.removeEffect(effect.getId());
            if (canReceiveMessage) {
                ((MessageRecipient) sender).sendMessage(new TranslatedMessage("commands.effect.success.removed", effect.getName(), player.getDisplayName()));
            }
        } else {
            effect.setDuration(duration).setAmplifier(amplification);
            player.addEffect(effect);
            Command.broadcastCommandMessage(sender, new TranslatedMessage("%commands.effect.success", new String[]{effect.getName(), String.valueOf(effect.getId()), String.valueOf(effect.getAmplifier()), player.getDisplayName(), String.valueOf(effect.getDuration() / 20)}));
        }
        return true;
    }
}
