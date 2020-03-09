package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class PardonIpCommand extends BaseCommand {

    public PardonIpCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("pardon-ip", "commands.unban.ip.description"); // TODO: aliases (unban-ip)

        dispatcher.register(literal("pardon-ip")
                .requires(requirePermission("nukkit.command.unban.ip"))
                .then(argument("ip", string()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        String ip = getString(context, "ip");

        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", ip)) {
            source.getServer().getIPBans().remove(ip);

            try {
                source.getServer().getNetwork().unblockAddress(InetAddress.getByName(ip));
            } catch (UnknownHostException e) {
                source.sendMessage(new TranslationContainer("commands.unbanip.invalid"));
                return 1;
            }

            sendAdminMessage(source, new TranslationContainer("commands.unbanip.success", ip));
        } else {
            source.sendMessage(new TranslationContainer("commands.unbanip.invalid"));
        }
        return 1;
    }
}
