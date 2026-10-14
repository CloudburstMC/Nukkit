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
        this.commandParameters.clear();
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

        for (Level level : sender.getServer().getLevels().values()) {
            int chunksCount = level.getChunks().size();
            int entitiesCount = level.getEntities().length;
            int tilesCount = level.getBlockEntities().size();
            level.doChunkGarbageCollection();
            level.unloadChunks(true);
            chunksCollected += chunksCount - level.getChunks().size();
            entitiesCollected += entitiesCount - level.getEntities().length;
            tilesCollected += tilesCount - level.getBlockEntities().size();
        }

        System.gc();

        long freedMemory = Runtime.getRuntime().freeMemory() - memory;

        sender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + sender.getServer().getLanguage().translateString("nukkit.command.gc.title") + TextFormat.GREEN + " ----");
        sender.sendMessage(TextFormat.GOLD + sender.getServer().getLanguage().translateString("nukkit.command.gc.chunks", TextFormat.RED + String.valueOf(chunksCollected)));
        sender.sendMessage(TextFormat.GOLD + sender.getServer().getLanguage().translateString("nukkit.command.gc.entities", TextFormat.RED + String.valueOf(entitiesCollected)));
        sender.sendMessage(TextFormat.GOLD + sender.getServer().getLanguage().translateString("nukkit.command.gc.blockEntities", TextFormat.RED + String.valueOf(tilesCollected)));
        sender.sendMessage(TextFormat.GOLD + sender.getServer().getLanguage().translateString("nukkit.command.gc.memoryFreed", TextFormat.RED + String.valueOf(NukkitMath.round((freedMemory / 1024d / 1024d), 2)) + " MB"));
        return true;
    }
}
