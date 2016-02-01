package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.potion.Effect;

import java.util.Objects;

/**
 * Created by Snake1999 on 2016/1/23.
 * Package cn.nukkit.command.defaults in project nukkit.
 */
public class EffectCommand extends Command {
    public EffectCommand(String name) {
        super(name, "%nukkit.command.effect.description", "%commands.effect.usage");
        this.setPermission("nukkit.command.effect");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        /*  /effect <player> clear (removes all effects)
            /effect <player> <effect> [seconds] [amplifier] [hideParticles] (gives an effect)
        */
        if (args.length < 2 || args.length > 5) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        Player target;
        target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        if (Objects.equals(args[1], "clear")) {
            target.removeAllEffects();
            sender.sendMessage(new TranslationContainer("commands.effect.success.removed.all", args[0]));
            return true;
        } else {
            int seconds = 30;
            int amplifier = 0;
            boolean hideParticles = false;
            try {
                if (args.length >= 3) seconds = Integer.parseInt(args[2]);
                if (args.length >= 4) amplifier = Integer.parseInt(args[3]);
                if (args.length >= 5) hideParticles = Boolean.getBoolean(args[4]);
            } catch (NumberFormatException e0) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
            if (seconds < 0 || amplifier < 0) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
            Effect effect;
            try {
                effect = Effect.getEffect(Integer.parseInt(args[1]));
            } catch (NumberFormatException e1) {
                try {
                    effect = Effect.getEffectByName(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.effect.notFound", args[1]));
                    return true;
                }
            }
            effect = effect.setDuration(seconds * 20).setAmplifier(amplifier).setVisible(!hideParticles);
            target.addEffect(effect);

            sender.sendMessage(new TranslationContainer("commands.effect.success", new String[]{
                    effect.getName(),
                    String.valueOf(effect.getId()),
                    String.valueOf(effect.getAmplifier()),
                    target.getName(),
                    String.valueOf(seconds)}));
            return true;
        }
    }

}
