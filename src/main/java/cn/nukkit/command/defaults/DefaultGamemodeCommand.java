package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.GameMode;
import com.nukkitx.protocol.bedrock.data.command.CommandParamType;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DefaultGamemodeCommand extends Command {

    public DefaultGamemodeCommand() {
        super("defaultgamemode", CommandData.builder("defaultgamemode")
                .setDescription("commands.defaultgamemode.description")
                .setUsageMessage("/defaultgamemode <mode>")
                .setPermissions("nukkit.command.defaultgamemode")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("mode", CommandParamType.INT, false)
                }, new CommandParameter[]{
                        new CommandParameter("mode", new String[]{"survival", "creative", "s", "c",
                                "adventure", "a", "spectator", "view", "v"})
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            return false;
        }
        try {
            GameMode gameMode = GameMode.from(args[0].toLowerCase());

            sender.getServer().setPropertyInt("gamemode", gameMode.getVanillaId());
            sender.sendMessage(new TranslationContainer("commands.defaultgamemode.success", gameMode.getTranslation()));
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Unknown game mode"); //TODO: translate?
        }
        return true;
    }
}
