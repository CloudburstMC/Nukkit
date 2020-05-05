package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BanIpCommand extends Command {

    public BanIpCommand() {
        super("ban-ip", CommandData.builder("ban-ip")
                .setDescription("commands.banip.description")
                .setUsageMessage("/ban-ip <player> [reason]")
                .setPermissions("nukkit.command.ban.ip")
                .setAliases("banip")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false),
                        new CommandParameter("reason", CommandParamType.STRING, true)
                }).build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        String value = args[0];
        StringJoiner reason = new StringJoiner(" ");
        for (int i = 1; i < args.length; i++) {
            reason.add(args[i]);
        }

        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", value)) {
            this.processIPBan(value, sender, reason.toString());
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.banip.success", value));
        } else {
            Player player = sender.getServer().getPlayer(value);
            if (player != null) {
                this.processIPBan(player.getAddress(), sender, reason.toString());

                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.banip.success.players", player.getAddress(), player.getName()));
            } else {
                String name = value.toLowerCase();
                String path = sender.getServer().getDataPath() + "players/";
                File file = new File(path + name + ".dat");
                CompoundTag nbt = CompoundTag.EMPTY;
                if (file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file);
                         NBTInputStream inputStream = NbtUtils.createGZIPReader(fis)) {
                        nbt = (CompoundTag) inputStream.readTag();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (nbt != null && nbt.contains("lastIP") && Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", (value = nbt.getString("lastIP")))) {
                    this.processIPBan(value, sender, reason.toString());

                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.banip.success", value));
                } else {
                    sender.sendMessage(new TranslationContainer("%commands.banip.invalid"));
                    return false;
                }
            }
        }

        return true;
    }

    private void processIPBan(String ip, CommandSender sender, String reason) {
        sender.getServer().getIPBans().addBan(ip, reason, null, sender.getName());

        for (Player player : new ArrayList<>(sender.getServer().getOnlinePlayers().values())) {
            if (player.getAddress().equals(ip)) {
                player.kick(PlayerKickEvent.Reason.IP_BANNED, !reason.isEmpty() ? reason : "IP banned");
            }
        }

        try {
            sender.getServer().getNetwork().blockAddress(InetAddress.getByName(ip));
        } catch (UnknownHostException e) {
            // ignore
        }
    }
}
