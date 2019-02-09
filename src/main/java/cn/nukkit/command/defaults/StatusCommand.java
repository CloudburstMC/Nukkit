package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class StatusCommand extends VanillaCommand {
    private static final DateFormat format = new SimpleDateFormat("'" + TextFormat.RED + "'d'" + TextFormat.GOLD + " days " +
            TextFormat.RED + "'H'" + TextFormat.GOLD + " hours " +
            TextFormat.RED + "'m'" + TextFormat.GOLD + " minutes " +
            TextFormat.RED + "'s'" + TextFormat.GOLD + " seconds'");

    public StatusCommand(String name) {
        super(name, "%nukkit.command.status.description", "%nukkit.command.status.usage");
        this.setPermission("nukkit.command.status");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Server server = sender.getServer();
        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----");

        long time = System.currentTimeMillis() - Nukkit.START_TIME;

        sender.sendMessage(TextFormat.GOLD + "Uptime: " + format.format(new Date(time)));

        TextFormat tpsColor = TextFormat.GREEN;
        float tps = server.getTicksPerSecond();
        if (tps < 17) {
            tpsColor = TextFormat.GOLD;
        } else if (tps < 12) {
            tpsColor = TextFormat.RED;
        }

        sender.sendMessage(TextFormat.GOLD + "Current TPS: " + tpsColor + NukkitMath.round(tps, 2));

        sender.sendMessage(TextFormat.GOLD + "Load: " + tpsColor + server.getTickUsage() + "%");

        sender.sendMessage(TextFormat.GOLD + "Network upload: " + TextFormat.GREEN + NukkitMath.round((server.getNetwork().getUpload() / 1024 * 1000), 2) + " kB/s");

        sender.sendMessage(TextFormat.GOLD + "Network download: " + TextFormat.GREEN + NukkitMath.round((server.getNetwork().getDownload() / 1024 * 1000), 2) + " kB/s");

        sender.sendMessage(TextFormat.GOLD + "Thread count: " + TextFormat.GREEN + Thread.getAllStackTraces().size());


        Runtime runtime = Runtime.getRuntime();
        double totalMB = NukkitMath.round(((double) runtime.totalMemory()) / 1024 / 1024, 2);
        double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double maxMB = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        double usage = usedMB / maxMB * 100;
        TextFormat usageColor = TextFormat.GREEN;

        if (usage > 85) {
            usageColor = TextFormat.GOLD;
        }

        sender.sendMessage(TextFormat.GOLD + "Used memory: " + usageColor + usedMB + " MB. (" + NukkitMath.round(usage, 2) + "%)");

        sender.sendMessage(TextFormat.GOLD + "Total memory: " + TextFormat.RED + totalMB + " MB.");

        sender.sendMessage(TextFormat.GOLD + "Maximum VM memory: " + TextFormat.RED + maxMB + " MB.");

        sender.sendMessage(TextFormat.GOLD + "Available processors: " + TextFormat.GREEN + runtime.availableProcessors());


        TextFormat playerColor = TextFormat.GREEN;
        if (((float) server.getOnlinePlayers().size() / (float) server.getMaxPlayers()) > 0.85) {
            playerColor = TextFormat.GOLD;
        }

        sender.sendMessage(TextFormat.GOLD + "Players: " + playerColor + server.getOnlinePlayers().size() + TextFormat.GREEN + " online, " +
                TextFormat.RED + server.getMaxPlayers() + TextFormat.GREEN + " max. ");

        for (Level level : server.getLevels().values()) {
            sender.sendMessage(
                    TextFormat.GOLD + "World \"" + level.getFolderName() + "\"" + (!Objects.equals(level.getFolderName(), level.getName()) ? " (" + level.getName() + ")" : "") + ": " +
                            TextFormat.RED + level.getChunks().size() + TextFormat.GREEN + " chunks, " +
                            TextFormat.RED + level.getEntities().length + TextFormat.GREEN + " entities, " +
                            TextFormat.RED + level.getBlockEntities().size() + TextFormat.GREEN + " blockEntities." +
                            " Time " + ((level.getTickRate() > 1 || level.getTickRateTime() > 40) ? TextFormat.RED : TextFormat.YELLOW) + NukkitMath.round(level.getTickRateTime(), 2) + "ms" +
                            (level.getTickRate() > 1 ? " (tick rate " + level.getTickRate() + ")" : "")
            );
        }

        return true;
    }
}
