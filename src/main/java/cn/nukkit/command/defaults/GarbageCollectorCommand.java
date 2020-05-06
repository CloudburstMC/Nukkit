package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.ThreadCache;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GarbageCollectorCommand extends Command {

    public GarbageCollectorCommand() {
        super("gc", CommandData.builder("gc")
                .setDescription("commands.gc.description")
                .setUsageMessage("/gc")
                .setPermissions("nukkit.command.gc")
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        int chunksCollected = 0;
        int entitiesCollected = 0;
        int tilesCollected = 0;
        long memory = Runtime.getRuntime().freeMemory();

        for (Level level : sender.getServer().getLevels()) {
            int chunksCount = level.getChunkCount();
            int entitiesCount = level.getEntities().length;
            int tilesCount = level.getBlockEntities().size();
            level.doChunkGarbageCollection();
            //level.unloadChunks(true);
            chunksCollected += chunksCount - level.getChunkCount();
            entitiesCollected += entitiesCount - level.getEntities().length;
            tilesCollected += tilesCount - level.getBlockEntities().size();
        }

        ThreadCache.clean();
        System.gc();

        long freedMemory = Runtime.getRuntime().freeMemory() - memory;

        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Garbage collection result" + TextFormat.GREEN + " ----");
        sender.sendMessage(TextFormat.GOLD + "Chunks: " + TextFormat.RED + chunksCollected);
        sender.sendMessage(TextFormat.GOLD + "Entities: " + TextFormat.RED + entitiesCollected);
        sender.sendMessage(TextFormat.GOLD + "Block Entities: " + TextFormat.RED + tilesCollected);
        sender.sendMessage(TextFormat.GOLD + "Memory freed: " + TextFormat.RED + NukkitMath.round((freedMemory / 1024d / 1024d), 2) + " MB");
        return true;
    }
}
