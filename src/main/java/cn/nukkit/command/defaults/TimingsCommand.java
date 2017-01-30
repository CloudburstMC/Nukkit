package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import co.aikar.timings.Timings;
import co.aikar.timings.TimingsExport;

/**
 * @author fromgate
 * @author Pub4Game
 */
public class TimingsCommand extends VanillaCommand {

    public TimingsCommand(String name) {
        super(name, "%nukkit.command.timings.description", "%nukkit.command.timings.usage");
        this.setPermission("nukkit.command.timings");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("on|off|paste")
        });
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

        switch (mode) {
            case "verbon":
                sender.sendMessage(new TranslationContainer("nukkit.command.timings.verboseEnable"));
                Timings.setVerboseEnabled(true);
                break;
            case "verboff":
                sender.sendMessage(new TranslationContainer("nukkit.command.timings.verboseDisable"));
                Timings.setVerboseEnabled(true);
                break;
            case "reset":
                Timings.reset();
                sender.sendMessage(new TranslationContainer("nukkit.command.timings.reset"));
                break;
            case "report":
            case "paste":
                TimingsExport.reportTimings(sender);
                break;
        }
        return true;
    }
}

