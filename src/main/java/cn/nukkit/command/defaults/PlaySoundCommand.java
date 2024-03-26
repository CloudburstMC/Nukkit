package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;

public class PlaySoundCommand extends VanillaCommand {

    public PlaySoundCommand(String name) {
        super(name, "%nukkit.command.playsound.description", "%commands.playsound.usage");
        this.setPermission("nukkit.command.playsound");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("sound", CommandParamType.STRING, false),
                new CommandParameter("player", CommandParamType.TARGET, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("%commands.playsound.usage", this.usageMessage));
            return false;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
                return true;
            }

            Player p = (Player) sender;

            p.getLevel().addSound(p, args[0], p);
            p.sendMessage(new TranslationContainer("commands.playsound.success", args[0], p.getName()));
            
            return true;
        }

        if (args[1].equalsIgnoreCase("@a")) {
            for (Player p : Server.getInstance().getOnlinePlayers().values()) {
                p.getLevel().addSound(p, args[0], p);
            }

            sender.sendMessage(new TranslationContainer("commands.playsound.success", args[0], "@a"));

            return true;
        }

        if (args[1].equalsIgnoreCase("@s") && sender instanceof Player) {
            Player p = (Player) sender;

            p.getLevel().addSound(p, args[0], p);
            sender.sendMessage(new TranslationContainer("commands.playsound.success", args[0], p.getName()));

            return true;
        }

        Player p = Server.getInstance().getPlayerExact(args[1]);

        if (p == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.player.notFound"));
            return true;
        }

        p.getLevel().addSound(p, args[0], p);
        sender.sendMessage(new TranslationContainer("commands.playsound.success", args[0], p.getName()));

        return true;
    }
}