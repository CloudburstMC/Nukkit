package cn.nukkit.event;

import cn.nukkit.level.Level;

/**
 * Created by Pub4Game on 01.07.2016.
 */
public class LevelTimings {

    public final TimingsHandler mobSpawn;
    public final TimingsHandler doChunkUnload;
    public final TimingsHandler doPortalForcer;
    public final TimingsHandler doTickPending;
    public final TimingsHandler doTickTiles;
    public final TimingsHandler doVillages;
    public final TimingsHandler doChunkMap;
    public final TimingsHandler doChunkGC;
    public final TimingsHandler doSounds;
    public final TimingsHandler entityTick;
    public final TimingsHandler blockEntityTick;
    public final TimingsHandler blockEntityPending;
    public final TimingsHandler tracker;
    public final TimingsHandler doTick;
    public final TimingsHandler tickEntities;
    public final TimingsHandler syncChunkSendTimer;
    public final TimingsHandler syncChunkSendPrepareTimer;
    public final TimingsHandler syncChunkLoadTimer;
    public final TimingsHandler syncChunkLoadDataTimer;
    public final TimingsHandler syncChunkLoadStructuresTimer;
    public final TimingsHandler syncChunkLoadEntitiesTimer;
    public final TimingsHandler syncChunkLoadBlockEntitiesTimer;
    public final TimingsHandler syncChunkLoadTileTicksTimer;
    public final TimingsHandler syncChunkLoadPostTimer;

    public LevelTimings(Level level) {
        String name = level.getFolderName() + " - ";

        this.mobSpawn = new TimingsHandler("** " + name + "mobSpawn");
        this.doChunkUnload = new TimingsHandler("** " + name + "doChunkUnload");
        this.doTickPending = new TimingsHandler("** " + name + "doTickPending");
        this.doTickTiles = new TimingsHandler("** " + name + "doTickTiles");
        this.doVillages = new TimingsHandler("** " + name + "doVillages");
        this.doChunkMap = new TimingsHandler("** " + name + "doChunkMap");
        this.doSounds = new TimingsHandler("** " + name + "doSounds");
        this.doChunkGC = new TimingsHandler("** " + name + "doChunkGC");
        this.doPortalForcer = new TimingsHandler("** " + name + "doPortalForcer");
        this.entityTick = new TimingsHandler("** " + name + "entityTick");
        this.blockEntityTick = new TimingsHandler("** " + name + "blockEntityTick");
        this.blockEntityPending = new TimingsHandler("** " + name + "blockEntityPending");

        this.syncChunkSendTimer = new TimingsHandler("** " + name + "syncChunkSend");
        this.syncChunkSendPrepareTimer = new TimingsHandler("** " + name + "syncChunkSendPrepare");

        this.syncChunkLoadTimer = new TimingsHandler("** " + name + "syncChunkLoad");
        this.syncChunkLoadDataTimer = new TimingsHandler("** " + name + "syncChunkLoad - Data");
        this.syncChunkLoadStructuresTimer = new TimingsHandler("** " + name + "syncChunkLoad - Structures");
        this.syncChunkLoadEntitiesTimer = new TimingsHandler("** " + name + "syncChunkLoad - Entities");
        this.syncChunkLoadBlockEntitiesTimer = new TimingsHandler("** " + name + "syncChunkLoad - BlockEntities");
        this.syncChunkLoadTileTicksTimer = new TimingsHandler("** " + name + "syncChunkLoad - TileTicks");
        this.syncChunkLoadPostTimer = new TimingsHandler("** " + name + "syncChunkLoad - Post");

        this.tracker = new TimingsHandler(name + "tracker");
        this.doTick = new TimingsHandler(name + "doTick");
        this.tickEntities = new TimingsHandler(name + "tickEntities");
    }
}
