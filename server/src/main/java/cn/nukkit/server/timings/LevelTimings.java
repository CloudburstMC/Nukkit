package cn.nukkit.server.timings;

import cn.nukkit.server.level.NukkitLevel;
import co.aikar.timings.Timing;
import co.aikar.timings.TimingsManager;

/**
 * @author Pub4Game
 * @author Tee7even
 */
public class LevelTimings {
    public final Timing doChunkUnload;
    public final Timing doTickPending;
    public final Timing doChunkGC;
    public final Timing doTick;

    public final Timing tickChunks;
    public final Timing entityTick;
    public final Timing blockEntityTick;

    public final Timing syncChunkSendTimer;
    public final Timing syncChunkSendPrepareTimer;
    public final Timing syncChunkLoadTimer;
    public final Timing syncChunkLoadDataTimer;
    public final Timing syncChunkLoadEntitiesTimer;
    public final Timing syncChunkLoadBlockEntitiesTimer;

    public LevelTimings(NukkitLevel level) {
        String name = level.getFolderName() + " - ";

        this.doChunkUnload = TimingsManager.getTiming(name + "doChunkUnload");
        this.doTickPending = TimingsManager.getTiming(name + "doTickPending");
        this.doChunkGC = TimingsManager.getTiming(name + "doChunkGC");
        this.doTick = TimingsManager.getTiming(name + "doTick");

        this.tickChunks = TimingsManager.getTiming(name + "tickChunks");
        this.entityTick = TimingsManager.getTiming(name + "entityTick");
        this.blockEntityTick = TimingsManager.getTiming(name + "blockEntityTick");

        this.syncChunkSendTimer = TimingsManager.getTiming(name + "syncChunkSend");
        this.syncChunkSendPrepareTimer = TimingsManager.getTiming(name + "syncChunkSendPrepare");
        this.syncChunkLoadTimer = TimingsManager.getTiming(name + "syncChunkLoad");
        this.syncChunkLoadDataTimer = TimingsManager.getTiming(name + "syncChunkLoad - Data");
        this.syncChunkLoadEntitiesTimer = TimingsManager.getTiming(name + "syncChunkLoad - Entities");
        this.syncChunkLoadBlockEntitiesTimer = TimingsManager.getTiming(name + "syncChunkLoad - BlockEntities");
    }
}
