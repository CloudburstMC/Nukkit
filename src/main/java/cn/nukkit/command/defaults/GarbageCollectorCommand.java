package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GarbageCollectorCommand extends VanillaCommand {

    public GarbageCollectorCommand(String name) {
        super(name, "%nukkit.command.gc.description", "%nukkit.command.gc.usage");
        this.setPermission("nukkit.command.gc");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender)){
            return true;
        }
        //todo finish chunk collector after getChunks added
        //final int[] chunksCollected = {0};
        final int[] entitiesCollected = {0};
        final int[] tilesCollected = {0};
        long memory = Runtime.getRuntime().freeMemory();

        sender.getServer().getLevels().forEach((i, level)->{
            //int chunksCount = level.getChunks(); //wtf no getChunks()
            int entitiesCount = level.getEntities().length;
            int tilesCount = level.getTiles().size();
            level.doChunkGarbageCollection();
            level.unloadChunks(true);
            //chunksCollected[0] += chunksCount - level.getChunks();
            entitiesCollected[0] += entitiesCount - level.getEntities().length;
            tilesCollected[0] += tilesCount - level.getTiles().size();
            level.clearCache(true);
        });

        System.gc();

        long freedMemory = Runtime.getRuntime().freeMemory() - memory;

        sender.sendMessage(TextFormat.GREEN +"---- "+ TextFormat.WHITE +"Garbage collection result"+ TextFormat.GREEN +" ----");
        //sender.sendMessage(TextFormat.GOLD +"Chunks: "+ TextFormat.RED + chunksCollected);
        sender.sendMessage(TextFormat.GOLD +"Entities: "+ TextFormat.RED + entitiesCollected[0]);
        sender.sendMessage(TextFormat.GOLD +"Tiles: "+ TextFormat.RED + tilesCollected[0]);
        sender.sendMessage(TextFormat.GOLD +"Memory freed: "+ TextFormat.RED + NukkitMath.round((freedMemory / 1024d / 1024d), 2)+" MB");
        return true;
    }
}
