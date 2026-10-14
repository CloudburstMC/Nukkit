package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class StatusCommand extends VanillaCommand {

    private static final String UPTIME_FORMAT = TextFormat.RED + "%d" + TextFormat.GOLD + " days " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " hours " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " minutes " +
            TextFormat.RED + "%d" + TextFormat.GOLD + " seconds";

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
        cn.nukkit.lang.BaseLang lang = server.getLanguage();
        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + lang.translateString("nukkit.command.status.title") + TextFormat.GREEN + " ----");

        long time = System.currentTimeMillis() - Nukkit.START_TIME;

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.uptime", formatUptime(time)));

        TextFormat tpsColor = TextFormat.GREEN;
        float tps = server.getTicksPerSecond();
        if (tps < 12) {
            tpsColor = TextFormat.RED;
        } else if (tps < 17) {
            tpsColor = TextFormat.GOLD;
        }

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.tps", new String[]{tpsColor + String.valueOf(NukkitMath.round(tps, 2)), tpsColor + String.valueOf(NukkitMath.round(server.getTicksPerSecondAverage(), 2))}, null));

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.load", new String[]{tpsColor + String.valueOf(server.getTickUsage()) + "%", tpsColor + String.valueOf(server.getTickUsageAverage() * 100) + "%"}, null));

        //sender.sendMessage(TextFormat.GOLD + "Network upload: " + TextFormat.GREEN + NukkitMath.round((server.getNetwork().getUpload() / 1024 * 1000), 2) + " kB/s");

        //sender.sendMessage(TextFormat.GOLD + "Network download: " + TextFormat.GREEN + NukkitMath.round((server.getNetwork().getDownload() / 1024 * 1000), 2) + " kB/s");

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.threads", TextFormat.GREEN + String.valueOf(Thread.getAllStackTraces().size())));


        Runtime runtime = Runtime.getRuntime();
        double totalMB = NukkitMath.round(((double) runtime.totalMemory()) / 1024 / 1024, 2);
        double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double maxMB = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        double usage = usedMB / maxMB * 100;
        TextFormat usageColor = TextFormat.GREEN;

        if (usage > 85) {
            usageColor = TextFormat.GOLD;
        }

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.memory.used", usageColor + String.valueOf(usedMB) + " MB (" + NukkitMath.round(usage, 2) + "%)"));

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.memory.total", TextFormat.RED + String.valueOf(totalMB) + " MB"));

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.memory.max", TextFormat.RED + String.valueOf(maxMB) + " MB"));

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.processors", TextFormat.GREEN + String.valueOf(runtime.availableProcessors())));


        int players = server.getOnlinePlayersCount();

        TextFormat playerColor = TextFormat.GREEN;
        if (((float) players / (float) server.getMaxPlayers()) > 0.85) {
            playerColor = TextFormat.GOLD;
        }

        sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.players",
                new String[]{playerColor + String.valueOf(players) + TextFormat.GREEN,
                TextFormat.RED + String.valueOf(server.getMaxPlayers()) + TextFormat.GREEN}, null));

        for (Level level : server.getLevels().values()) {
            sender.sendMessage(TextFormat.GOLD + lang.translateString("nukkit.command.status.world",
                    level.getFolderName(),
                    !Objects.equals(level.getFolderName(), level.getName()) ? " (" + level.getName() + ')' : "",
                    TextFormat.RED + String.valueOf(level.getChunks().size()) + TextFormat.GREEN,
                    TextFormat.RED + String.valueOf(level.getEntities().length) + TextFormat.GREEN,
                    TextFormat.RED + String.valueOf(level.getBlockEntities().size()) + TextFormat.GREEN,
                    ((level.getTickRate() > 1 || level.getTickRateTime() > 40) ? TextFormat.RED : TextFormat.YELLOW) + String.valueOf(NukkitMath.round(level.getTickRateTime(), 2)) + "ms",
                    level.getTickRate() > 1 ? lang.translateString("nukkit.command.status.world.tickRate", String.valueOf(level.getTickRate())) : ""));
        }

        return true;
    }

    public static String formatUptime(long uptime) {
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
        return Server.getInstance().getLanguage().translateString("nukkit.command.status.uptime.format",
                TextFormat.RED + String.valueOf(days) + TextFormat.GOLD,
                TextFormat.RED + String.valueOf(hours) + TextFormat.GOLD,
                TextFormat.RED + String.valueOf(minutes) + TextFormat.GOLD,
                TextFormat.RED + String.valueOf(seconds) + TextFormat.GOLD);
    }
}