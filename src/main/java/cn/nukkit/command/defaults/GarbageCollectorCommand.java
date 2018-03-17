package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.ThreadCache;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GarbageCollectorCommand extends VanillaCommand {

    public GarbageCollectorCommand(String name) {
        super(name, "%nukkit.command.gc.description", "%nukkit.command.gc.usage");
        this.setPermission("nukkit.command.gc");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        sender.getServer().scheduler.scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {
                int chunksCount = 0;
                int entitiesCount = 0;
                int tilesCount = 0;
                long memory = Runtime.getRuntime().freeMemory();
                for (Level level : sender.getServer().levelArray)   {
                    chunksCount += level.getChunks().size();
                    entitiesCount += level.countEntities();
                    tilesCount += level.getBlockEntities().size();
                }

                sender.getServer().forceGC();

                for (Level level : sender.getServer().getLevels().values()) {
                    chunksCount -= level.getChunks().size();
                    entitiesCount -= level.countEntities();
                    tilesCount -= level.getBlockEntities().size();
                }

                ThreadCache.clean();
                System.gc();

                long freedMemory = Runtime.getRuntime().freeMemory() - memory;

                sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Garbage collection result" + TextFormat.GREEN + " ----");
                sender.sendMessage(TextFormat.GOLD + "Chunks: " + TextFormat.RED + chunksCount);
                sender.sendMessage(TextFormat.GOLD + "Entities: " + TextFormat.RED + entitiesCount);
                sender.sendMessage(TextFormat.GOLD + "Block Entities: " + TextFormat.RED + tilesCount);
                sender.sendMessage(TextFormat.GOLD + "Memory freed: " + TextFormat.RED + NukkitMath.round((freedMemory / 1024d / 1024d), 2) + " MB");
            }
        });
        return true;
    }
}
