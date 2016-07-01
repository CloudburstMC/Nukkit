package cn.nukkit.event;

import cn.nukkit.level.Level;

/**
 * Created by Pub4Game on 01.07.2016.
 */
public class LevelTimings {

    public TimingsHandler mobSpawn;
    public TimingsHandler doChunkUnload;
    public TimingsHandler doPortalForcer;
    public TimingsHandler doTickPending;
    public TimingsHandler doTickTiles;
    public TimingsHandler doVillages;
    public TimingsHandler doChunkMap;
    public TimingsHandler doChunkGC;
    public TimingsHandler doSounds;
    public TimingsHandler entityTick;
    public TimingsHandler blockEntityTick;
    public TimingsHandler blockEntityPending;
    public TimingsHandler tracker;
    public TimingsHandler doTick;
    public TimingsHandler tickEntities;
    public TimingsHandler syncChunkSendTimer;
    public TimingsHandler syncChunkSendPrepareTimer;
    public TimingsHandler syncChunkLoadTimer;
    public TimingsHandler syncChunkLoadDataTimer;
    public TimingsHandler syncChunkLoadStructuresTimer;
    public TimingsHandler syncChunkLoadEntitiesTimer;
    public TimingsHandler syncChunkLoadBlockEntitiesTimer;
    public TimingsHandler syncChunkLoadTileTicksTimer;
    public TimingsHandler syncChunkLoadPostTimer;

    public LevelTimings(Level level){
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
