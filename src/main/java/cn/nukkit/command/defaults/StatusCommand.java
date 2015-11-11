package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;

import java.util.Objects;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class StatusCommand extends VanillaCommand {
    
    public StatusCommand(String name) {
        super(name, "%nukkit.command.status.description", "%nukkit.command.status.usage");
        this.setPermission("nukkit.command.status");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender)){
            return true;
        }

        Server server = sender.getServer();
        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----");

        long time = (System.currentTimeMillis() - Nukkit.START_TIME) / 1000;
        int seconds = NukkitMath.floorDouble(time % 60);
        int minutes = NukkitMath.floorDouble((time % 3600) / 60);
        int hours   = NukkitMath.floorDouble(time % (3600 * 24) / 3600);
        int days    = NukkitMath.floorDouble(time / (3600 * 24));
        String upTimeString = TextFormat.RED + days +TextFormat.GREEN +"days "+
                TextFormat.RED + hours +TextFormat.GREEN +"hours "+
                TextFormat.RED + minutes +TextFormat.GREEN +"minutes "+
                TextFormat.RED + seconds +TextFormat.GREEN +"seconds";
        sender.sendMessage(TextFormat.GOLD +"Uptime: "+ TextFormat.RED + upTimeString);//好乱...

        String tpsColor = TextFormat.GREEN;
        float tps = server.getTicksPerSecond();
        if(tps < 17) tpsColor = TextFormat.GOLD;
        else if(tps < 12) tpsColor = TextFormat.RED;
        sender.sendMessage(TextFormat.GOLD +"Current TPS: "+ tpsColor + NukkitMath.round(tps, 2));
        sender.sendMessage(TextFormat.GOLD +"Load: "+tpsColor+server.getTickUsage()+"%");
        
        sender.sendMessage(TextFormat.GOLD +"Network upload: "+  TextFormat.GREEN + NukkitMath.round((server.getNetwork().getUpload() / 1024), 2) + " kB/s");
        sender.sendMessage(TextFormat.GOLD +"Network download: "+  TextFormat.GREEN + NukkitMath.round((server.getNetwork().getDownload() / 1024), 2) + " kB/s");
        
        sender.sendMessage(TextFormat.GOLD +"Thread count: "+  TextFormat.GREEN + Thread.getAllStackTraces().size());

        Runtime runtime = Runtime.getRuntime();
        double totalMB = NukkitMath.round(((double) runtime.totalMemory()) / 1024 / 1024, 2);
        double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double maxMB = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        double usage = usedMB / maxMB * 100;
        String usageColor = TextFormat.GREEN;
        if(usage > 85) usageColor = TextFormat.GOLD;
        sender.sendMessage(TextFormat.GOLD +"Used memory: "+ usageColor + usedMB +" MB. (" + NukkitMath.round(usage, 2) + "%)");
        sender.sendMessage(TextFormat.GOLD +"Total memory: "+ TextFormat.RED + totalMB +" MB.");
        sender.sendMessage(TextFormat.GOLD +"Maximum VM memory: "+ TextFormat.RED + maxMB +" MB.");
        sender.sendMessage(TextFormat.GOLD +"Available processors: "+ TextFormat.GREEN + runtime.availableProcessors());


        String playerColor = TextFormat.GREEN;
        if(((float)server.getOnlinePlayers().size()/(float)server.getMaxPlayers()) > 0.85) playerColor = TextFormat.GOLD;
        sender.sendMessage(TextFormat.GOLD +"Players: "+playerColor+ server.getOnlinePlayers().size()+TextFormat.GREEN +" online, "+
                TextFormat.RED+ server.getMaxPlayers()+TextFormat.GREEN +" max. ");

        server.getLevels().forEach((i, level)->
            sender.sendMessage(
                    TextFormat.GOLD +"World \""+level.getFolderName()+"\""+(!Objects.equals(level.getFolderName(), level.getName()) ? " ("+level.getName()+")" : "")+": "+
                    //todo finish this after getChunks added
                    //TextFormat.RED + level.getChunks().size() + TextFormat.GREEN +" chunks, "+  //getChunks没有？
                    TextFormat.RED + level.getEntities().length + TextFormat.GREEN +" entities, "+
                    TextFormat.RED + level.getTiles().size() + TextFormat.GREEN +" tiles."+
                    " Time "+ ((level.getTickRate() > 1 || level.getTickRateTime() > 40) ? TextFormat.RED : TextFormat.YELLOW) +NukkitMath.round(level.getTickRateTime(), 2)+"ms"+
                    (level.getTickRate() > 1 ? " (tick rate "+ level.getTickRate() +")" : "")
            )
        );


        return true;
    }
}
