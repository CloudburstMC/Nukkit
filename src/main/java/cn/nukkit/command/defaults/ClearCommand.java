package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2024/26/2 by Sherko231.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class ClearCommand extends VanillaCommand {
    public ClearCommand(String name) {
        super(name, "%nukkit.command.clear.description", "%nukkit.command.clear.usage");
        this.setPermission("nukkit.command.clear");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{});
        this.commandParameters.put("target", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        if (args.length > 1 || (!(sender instanceof Player) && args.length < 1)) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        if (args.length == 0) {
            Player player = (Player) sender;
            player.getInventory().clearAll();
            return false;
        }

        List<Player> targets = new ArrayList<>();
        if (args[0].equals("@a")) {
            targets.addAll(Server.getInstance().getOnlinePlayers().values());
        }
        else {
            Player target = sender.getServer().getPlayer(args[0].replace("@s", sender.getName()));
            if (target != null) {
                targets.add(target);
            }
            else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
        }

        for (Player player : targets) {
            player.getInventory().clearAll();
            Command.broadcastCommandMessage(sender, new TranslationContainer("%commands.clear.success", player.getName()));
        }

        return false;
    }
}
