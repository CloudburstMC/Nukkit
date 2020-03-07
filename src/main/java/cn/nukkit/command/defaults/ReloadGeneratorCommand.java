package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

/**
 * @author DaPorkchop_
 */
public class ReloadGeneratorCommand extends VanillaCommand {
    public ReloadGeneratorCommand(String name) {
        super(name, "%nukkit.command.op.description", "%commands.op.description");
        this.setPermission("nukkit.command.op.give");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Server.getInstance().getLevelManager().getChunkExecutor().submit(() -> {
            Server.getInstance().getLevels().forEach(Level::reloadGenerator);
            sender.sendMessage("Generators reloaded!");
        });

        return true;
    }
}
