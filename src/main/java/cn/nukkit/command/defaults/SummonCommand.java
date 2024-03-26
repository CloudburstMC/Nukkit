package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

public class SummonCommand extends Command {

    public SummonCommand(String name) {
        super(name, "%nukkit.command.summon.description", "%nukkit.command.summon.usage");
        this.setPermission("nukkit.command.summon");
        this.commandParameters.clear();
        List<String> entityNames = new ArrayList<>();
        for (String key : AddEntityPacket.LEGACY_IDS.values()) {
            entityNames.add(key.substring(10));
        }
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("entityType", false, entityNames.toArray(new String[0])),
                new CommandParameter("player", CommandParamType.TARGET, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0 || (args.length == 1 && !(sender instanceof Player))) {
            return false;
        }

        // Convert Minecraft format to the format what Nukkit uses
        String mob = Character.toUpperCase(args[0].charAt(0)) + args[0].substring(1);
        int max = mob.length() - 1;
        for (int x = 2; x < max; x++) {
            if (mob.charAt(x) == '_') {
                mob = mob.substring(0, x) + Character.toUpperCase(mob.charAt(x + 1)) + mob.substring(x + 2);
            }
        }

        Player playerThatSpawns;

        if (args.length == 2) {
            playerThatSpawns = Server.getInstance().getPlayerExact(args[1].replace("@s", sender.getName()));
        } else {
            playerThatSpawns = (Player) sender;
        }

        if (playerThatSpawns != null) {
            Position pos = playerThatSpawns.getPosition().floor().add(0.5, 0, 0.5);
            Entity ent;
            if ((ent = Entity.createEntity(mob, pos)) != null) {
                ent.spawnToAll();
                sender.sendMessage("\u00A76Spawned " + mob + " to " + playerThatSpawns.getName());
            } else {
                sender.sendMessage(TextFormat.RED + "Unable to spawn " + mob);
            }
        } else {
            sender.sendMessage(TextFormat.RED + "Unknown player " + (args.length == 2 ? args[1] : sender.getName()));
        }

        return true;
    }
}
