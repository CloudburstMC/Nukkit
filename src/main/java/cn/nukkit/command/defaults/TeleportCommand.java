package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Location;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.nukkitx.math.vector.Vector3f;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created on 2015/11/12 by Pub4Game and milkice.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TeleportCommand extends Command {
    public TeleportCommand() {
        super("tp", CommandData.builder("tp")
                .setDescription("commands.tp.description")
                .setUsageMessage("/tp [player] <position|target>")
                .setPermissions("nukkit.command.teleport")
                .addParameters(new CommandParameter[]{new CommandParameter("player", CommandParamType.TARGET, false)})
                .addParameters(new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false),
                        new CommandParameter("target", CommandParamType.TARGET, false),
                }).addParameters(new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false),
                        new CommandParameter("position", CommandParamType.POSITION, false),
                }).addParameters(new CommandParameter[]{
                        new CommandParameter("position", CommandParamType.POSITION, false),
                }).build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length < 1 || args.length > 6) {
            return false;
        }
        CommandSender target;
        CommandSender origin = sender;
        if (args.length == 1 || args.length == 3) {
            if (sender instanceof Player) {
                target = sender;
            } else {
                sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
                return true;
            }
            if (args.length == 1) {
                target = sender.getServer().getPlayer(args[0].replace("@s", sender.getName()));
                if (target == null) {
                    sender.sendMessage(TextFormat.RED + "Can't find player " + args[0]);
                    return true;
                }
            }
        } else {
            target = sender.getServer().getPlayer(args[0].replace("@s", sender.getName()));
            if (target == null) {
                sender.sendMessage(TextFormat.RED + "Can't find player " + args[0]);
                return true;
            }
            if (args.length == 2) {
                origin = target;
                target = sender.getServer().getPlayer(args[1].replace("@s", sender.getName()));
                if (target == null) {
                    sender.sendMessage(TextFormat.RED + "Can't find player " + args[1]);
                    return true;
                }
            }
        }
        if (args.length < 3) {
            ((Player) origin).teleport(((Player) target).getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.tp.success", origin.getName(), target.getName()));
            if (origin != sender) {
                origin.sendMessage(new TranslationContainer("commands.tp.successVictim", target.getName()));
            }
            return true;
        } else if (((Player) target).getLevel() != null) {
            int pos;
            if (args.length == 4 || args.length == 6) {
                pos = 1;
            } else {
                pos = 0;
            }
            Optional<Vector3f> optional = CommandUtils.parseVector3f(Arrays.copyOfRange(args, pos, pos += 3), ((Player) target).getPosition());
            if (!optional.isPresent()) {
                return false;
            }
            Vector3f position = optional.get();
            float yaw = ((Player) target).getYaw();
            float pitch = ((Player) target).getPitch();
            if (position.getY() < 0) position = Vector3f.from(position.getX(), 0, position.getZ());
            if (args.length == 6 || (args.length == 5 && pos == 3)) {
                yaw = Float.parseFloat(args[pos++]);
                pitch = Float.parseFloat(args[pos++]);
            }
            ((Player) target).teleport(Location.from(position, yaw, pitch, ((Player) target).getLevel()), PlayerTeleportEvent.TeleportCause.COMMAND);
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.tp.success.coordinates",
                    target.getName(), String.valueOf(NukkitMath.round(position.getX(), 2)),
                    String.valueOf(NukkitMath.round(position.getY(), 2)),
                    String.valueOf(NukkitMath.round(position.getZ(), 2))));
            if (target != sender) {
                target.sendMessage(new TranslationContainer("commands.tp.successVictim", position.toString()));
            }
            return true;
        }
        return false;
    }
}
