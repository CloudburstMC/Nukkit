package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.permission.BanList;
import cn.nukkit.player.Player;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class BanIpCommand extends BaseCommand {
    public static final SimpleCommandExceptionType ALREADY_BANNED = new SimpleCommandExceptionType(new LiteralMessage("That player is already banned"));

    public BanIpCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("ban-ip", "commands.banip.description"); // TODO: aliases (banip)

        dispatcher.register(literal("ban-ip")
                .requires(requirePermission("nukkit.command.ban.ip"))
                .then(argument("player", player()).executes(this::run)
                        .then(argument("reason", greedyString()).executes(this::run)))
                .then(argument("address", string()).executes(this::address) // TODO: ip address argument
                        .then(argument("reason", greedyString()).executes(this::address))));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        String reason = "IP Banned";

        try {
            reason = getString(context, "reason");
        } catch (IllegalArgumentException ignored) {
            // ignore
        }

        if(!processBan(target.getAddress(), source, reason)) {
            source.sendMessage(new TranslationContainer("commands.banip.invalid"));
            return 1;
        }

        sendAdminMessage(source, new TranslationContainer("commands.banip.success.players", target.getAddress(), target.getName()));
        return 1;
    }

    public int address(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        String address = getString(context, "address");
        String reason = "IP Banned";

        try {
            reason = getString(context, "reason");
        } catch (IllegalArgumentException ignored) {
            // ignore
        }

        if(!processBan(address, source, reason)) {
            source.sendMessage(new TranslationContainer("commands.banip.invalid"));
            return 1;
        }

        sendAdminMessage(source, new TranslationContainer("commands.banip.success", address));
        return 1;
    }

    public boolean processBan(String ip, CommandSource source, String reason) {
        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", ip)) {
            return false;
        }

        source.getServer().getIPBans().addBan(ip, reason, null, source.getName());

        for (Player player : new ArrayList<>(source.getServer().getOnlinePlayers().values())) {
            if (player.getAddress().equals(ip)) {
                player.kick(PlayerKickEvent.Reason.IP_BANNED, !reason.isEmpty() ? reason : "IP banned");
            }
        }

        try {
            source.getServer().getNetwork().blockAddress(InetAddress.getByName(ip));
        } catch (UnknownHostException ignored) {

        }
        return true;
    }
}
