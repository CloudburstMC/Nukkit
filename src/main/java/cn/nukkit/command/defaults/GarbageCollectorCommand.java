package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.ThreadCache;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class GarbageCollectorCommand extends BaseCommand {

    public GarbageCollectorCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("gc", "%nukkit.command.gc.description");
        setPermission("nukkit.gc.stop");

        dispatcher.register(literal("gc").executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

        int chunksCollected = 0;
        int entitiesCollected = 0;
        int tilesCollected = 0;
        long memory = Runtime.getRuntime().freeMemory();

        for (Level level : source.getServer().getLevels()) {
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

        source.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Garbage collection result" + TextFormat.GREEN + " ----");
        source.sendMessage(TextFormat.GOLD + "Chunks: " + TextFormat.RED + chunksCollected);
        source.sendMessage(TextFormat.GOLD + "Entities: " + TextFormat.RED + entitiesCollected);
        source.sendMessage(TextFormat.GOLD + "Block Entities: " + TextFormat.RED + tilesCollected);
        source.sendMessage(TextFormat.GOLD + "Memory freed: " + TextFormat.RED + NukkitMath.round((freedMemory / 1024d / 1024d), 2) + " MB");
        return 1;
    }
}
