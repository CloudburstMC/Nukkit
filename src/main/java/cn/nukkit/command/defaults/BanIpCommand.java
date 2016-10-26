package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BanIpCommand extends VanillaCommand {

    public BanIpCommand(String name) {
        super(name, "%nukkit.command.ban.ip.description", "%commands.banip.usage");
        this.setPermission("nukkit.command.ban.ip");
        this.setAliases(new String[]{"banip"});
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        String value = args[0];
        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason += args[i] + " ";
        }

        if (reason.length() > 0) {
            reason = reason.substring(0, reason.length() - 1);
        }

        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", value)) {
            this.processIPBan(value, sender, reason);

            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.banip.success", value));
        } else {
            Player player = sender.getServer().getPlayer(value);
            if (player != null) {
                this.processIPBan(player.getAddress(), sender, reason);

                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.banip.success.players", new String[]{player.getAddress(), player.getName()}));
            } else {
                String name = value.toLowerCase();
                String path = sender.getServer().getDataPath() + "players/";
                File file = new File(path + name + ".dat");
                CompoundTag nbt = null;
                if (file.exists()) {
                    try {
                        nbt = NBTIO.readCompressed(new FileInputStream(file));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (nbt != null && nbt.contains("lastIP") && Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", (value = nbt.getString("lastIP")))) {
                    this.processIPBan(value, sender, reason);

                    Command.broadcastCommandMessage(sender, new TranslationContainer("commands.banip.success", value));
                } else {
                    sender.sendMessage(new TranslationContainer("commands.banip.invalid"));
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

        sender.getServer().getNetwork().blockAddress(ip, -1);
    }
}
