package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveCommand extends BaseCommand {

    public SaveCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("save-all", "%nukkit.command.save.description");

        dispatcher.register(literal("save-all")
                .requires(requirePermission("nukkit.command.save.perform"))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        sendAdminMessage(source, new TranslationContainer("commands.save.start"));

        for (Player player : source.getServer().getOnlinePlayers().values()) {
            player.save();
        }

        for (Level level : source.getServer().getLevels()) {
            level.save(true);
        }

        sendAdminMessage(source, new TranslationContainer("commands.save.success"));
        return 1;
    }
}
