package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.lang.TranslationContainer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class SaveOnCommand extends BaseCommand {

    public SaveOnCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("save-n", "%nukkit.command.saveon.description");
        setPermission("nukkit.command.saveon");

        dispatcher.register(literal("save-on").executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

        source.getServer().setAutoSave(true);
        sendAdminMessage(source, new TranslationContainer("commands.save.enabled"));
        return 1;
    }
}
