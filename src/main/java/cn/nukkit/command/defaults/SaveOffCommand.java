package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class SaveOffCommand extends BaseCommand {

    public SaveOffCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("save-off", "%nukkit.command.saveoff.description");
        setPermission("nukkit.command.saveoff");

        dispatcher.register(literal("save-off").executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

        source.getServer().setAutoSave(false);
        sendAdminMessage(source, new TranslationContainer("commands.save.disabled"));
        return 1;
    }
}
