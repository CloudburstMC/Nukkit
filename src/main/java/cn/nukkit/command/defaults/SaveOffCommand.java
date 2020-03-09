package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class SaveOffCommand extends BaseCommand {

    public SaveOffCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("save-off", "%nukkit.command.saveoff.description");

        dispatcher.register(literal("save-off")
                .requires(requirePermission("nukkit.command.saveoff"))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        source.getServer().setAutoSave(false);
        sendAdminMessage(source, new TranslationContainer("commands.save.disabled"));
        return 1;
    }
}
