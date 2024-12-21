package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

public class SummonCommand extends VanillaCommand {

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
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0 || (args.length != 2 && !(sender instanceof Player))) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String mob = args[0];

        // Convert Minecraft format to the format what Nukkit uses
        if (!Entity.isKnown(mob)) {
            mob = Character.toUpperCase(args[0].charAt(0)) + args[0].substring(1);
            StringBuilder sb = new StringBuilder(mob);
            for (int x = 2; x < sb.length() - 1; x++) {
                if (sb.charAt(x) == '_') {
                    sb.setCharAt(x + 1, Character.toUpperCase(sb.charAt(x + 1)));
                    sb.deleteCharAt(x);
                }
            }
            mob = sb.toString();
        }

        Player playerThatSpawns;

        if (args.length == 2) {
            playerThatSpawns = Server.getInstance().getPlayerExact(args[1].replace("@s", sender.getName()));
        } else {
            playerThatSpawns = (Player) sender;
        }

        if (playerThatSpawns != null) {
            Position pos = playerThatSpawns.floor().add(0.5, 0, 0.5);
            Entity ent;
            if ((ent = Entity.createEntity(mob, pos)) != null) {
                ent.spawnToAll();
                sender.sendMessage(new TranslationContainer("%commands.summon.success"));
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.summon.failed"));
            }
        } else {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
        }

        return true;
    }
}
