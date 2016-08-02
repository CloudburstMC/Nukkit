package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.timings.Timings;
import cn.nukkit.timings.TimingsExport;

/**
 * @author fromgate
 * @author Pub4Game
 */
public class TimingsCommand extends VanillaCommand {

    public TimingsCommand(String name) {
        super(name, "%nukkit.command.timings.description", "%nukkit.command.timings.usage");
        this.setPermission("nukkit.command.timings");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
            return true;
        }

        String mode = args[0].toLowerCase();

        if (mode.equals("on")) {
            Timings.setTimingsEnabled(true);
            Timings.reset();
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.enable"));
            return true;
        } else if (mode.equals("off")) {
            Timings.setTimingsEnabled(false);
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.disable"));
            return true;
        }

        if (!Timings.isTimingsEnabled()) {
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.timingsDisabled"));
            return true;
        }

        if (mode.equals("verbon")) {
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.verboseEnable"));
            Timings.setVerboseEnabled(true);
        } else if (mode.equals("verboff")) {
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.verboseDisable"));
            Timings.setVerboseEnabled(true);
        } else if (mode.equals("reset")) {
            Timings.reset();
            sender.sendMessage(new TranslationContainer("nukkit.command.timings.reset"));
        } else if (mode.equals("report") || mode.equals("paste")) {
            TimingsExport.reportTimings(sender);
        }
        return true;
    }
}

