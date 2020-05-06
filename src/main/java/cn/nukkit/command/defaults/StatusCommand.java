package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class StatusCommand extends Command {
    private static final String UPTIME_FORMAT = TextFormat.RED + "%d" + TextFormat.YELLOW + " days " +
            TextFormat.RED + "%d" + TextFormat.YELLOW + " hours " +
            TextFormat.RED + "%d" + TextFormat.YELLOW + " minutes " +
            TextFormat.RED + "%d" + TextFormat.YELLOW + " seconds";

    public StatusCommand() {
        super("status", CommandData.builder("status")
                .setDescription("nukkit.command.status.description")
                .setPermissions("nukkit.command.status")
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Server server = sender.getServer();
        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----");

        long time = System.currentTimeMillis() - Nukkit.START_TIME;

        sender.sendMessage(TextFormat.YELLOW + "Uptime: " + formatUptime(time));

        TextFormat tpsColor = TextFormat.GREEN;
        float tps = server.getTicksPerSecond();
        if (tps < 17) {
            tpsColor = TextFormat.YELLOW;
        } else if (tps < 12) {
            tpsColor = TextFormat.RED;
        }

        sender.sendMessage(TextFormat.YELLOW + "Current TPS: " + tpsColor + NukkitMath.round(tps, 2));

        sender.sendMessage(TextFormat.YELLOW + "Load: " + tpsColor + server.getTickUsage() + "%");

        sender.sendMessage(TextFormat.YELLOW + "Network upload: " + TextFormat.GREEN + NukkitMath.round((server.getNetwork().getUpload() / 1024 * 1000), 2) + " kB/s");

        sender.sendMessage(TextFormat.YELLOW + "Network download: " + TextFormat.GREEN + NukkitMath.round((server.getNetwork().getDownload() / 1024 * 1000), 2) + " kB/s");

        sender.sendMessage(TextFormat.YELLOW + "Thread count: " + TextFormat.GREEN + Thread.getAllStackTraces().size());


        Runtime runtime = Runtime.getRuntime();
        double totalMB = NukkitMath.round(((double) runtime.totalMemory()) / 1024 / 1024, 2);
        double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double maxMB = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        double usage = usedMB / maxMB * 100;
        TextFormat usageColor = TextFormat.GREEN;

        if (usage > 85) {
            usageColor = TextFormat.YELLOW;
        }

        sender.sendMessage(TextFormat.YELLOW + "Used memory: " + usageColor + usedMB + " MB. (" + NukkitMath.round(usage, 2) + "%)");

        sender.sendMessage(TextFormat.YELLOW + "Total memory: " + TextFormat.RED + totalMB + " MB.");

        sender.sendMessage(TextFormat.YELLOW + "Maximum VM memory: " + TextFormat.RED + maxMB + " MB.");

        sender.sendMessage(TextFormat.YELLOW + "Available processors: " + TextFormat.GREEN + runtime.availableProcessors());


        TextFormat playerColor = TextFormat.GREEN;
        if (((float) server.getOnlinePlayers().size() / (float) server.getMaxPlayers()) > 0.85) {
            playerColor = TextFormat.YELLOW;
        }

        sender.sendMessage(TextFormat.YELLOW + "Players: " + playerColor + server.getOnlinePlayers().size() + TextFormat.GREEN + " online, " +
                TextFormat.RED + server.getMaxPlayers() + TextFormat.GREEN + " max. ");

        for (Level level : server.getLevels()) {
            sender.sendMessage(
                    TextFormat.YELLOW + "World \"" + level.getId() + "\"" + (!Objects.equals(level.getId(), level.getName()) ? " (" + level.getName() + ")" : "") + ": " +
                            TextFormat.RED + level.getChunks().size() + TextFormat.GREEN + " chunks, " +
                            TextFormat.RED + level.getEntities().length + TextFormat.GREEN + " entities, " +
                            TextFormat.RED + level.getBlockEntities().size() + TextFormat.GREEN + " blockEntities." +
                            " Time " + ((level.getTickRate() > 1 || level.getTickRateTime() > 40) ? TextFormat.RED : TextFormat.YELLOW) + NukkitMath.round(level.getTickRateTime(), 2) + "ms" +
                            (level.getTickRate() > 1 ? " (tick rate " + level.getTickRate() + ")" : "")
            );
        }

        return true;
    }

    private static String formatUptime(long uptime) {
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
        return String.format(UPTIME_FORMAT, days, hours, minutes, seconds);
    }
}
