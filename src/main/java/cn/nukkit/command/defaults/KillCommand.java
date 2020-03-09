package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import java.util.StringJoiner;

import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;

public class KillCommand extends BaseCommand {

    public KillCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("kill", "%nukkit.command.kill.description"); // TODO: aliases (suicide)

        dispatcher.register(literal("kill")
                .requires(requirePermission("nukkit.command.kill.self"))
                .then(argument("player", player())
                        .requires(requirePermission("nukkit.command.kill.other"))
                        .executes(this::killOther))
                .executes(this::killSelf));
    }

    public int killSelf(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        EntityDamageEvent ev = new EntityDamageEvent((Player) source, DamageCause.SUICIDE, 1000);
        source.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return 1;
        }

        ((Player) source).setLastDamageCause(ev);
        ((Player) source).setHealth(0);

        source.sendMessage(new TranslationContainer("commands.kill.successful", source.getName()));
        return 1;
    }

    public int killOther(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");

        EntityDamageEvent ev = new EntityDamageEvent(target, DamageCause.SUICIDE, 1000);
        source.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return 1;
        }

        target.setLastDamageCause(ev);
        target.setHealth(0);

        sendAdminMessage(source, new TranslationContainer("commands.kill.successful", target.getName()));
        return 1;
    }
}
