package cn.nukkit.event;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.scheduler.TaskHandler;

import java.util.HashMap;

/**
 * Created by Pub4Game on 30.06.2016.
 */
public abstract class Timings {

    public static TimingsHandler fullTickTimer;
    public static TimingsHandler serverTickTimer;
    public static TimingsHandler memoryManagerTimer;// to-do
    public static TimingsHandler garbageCollectorTimer;// to-do
    public static TimingsHandler playerListTimer;
    public static TimingsHandler playerNetworkTimer;
    public static TimingsHandler playerNetworkReceiveTimer;
    public static TimingsHandler playerChunkOrderTimer;
    public static TimingsHandler playerChunkSendTimer;
    public static TimingsHandler connectionTimer;
    public static TimingsHandler tickablesTimer;
    public static TimingsHandler schedulerTimer;
    public static TimingsHandler chunkIOTickTimer;
    public static TimingsHandler timeUpdateTimer;
    public static TimingsHandler serverCommandTimer;
    public static TimingsHandler worldSaveTimer;
    public static TimingsHandler generationTimer;
    public static TimingsHandler populationTimer;
    public static TimingsHandler generationCallbackTimer;
    public static TimingsHandler permissibleCalculationTimer;
    public static TimingsHandler permissionDefaultTimer;
    public static TimingsHandler entityMoveTimer;
    public static TimingsHandler tickEntityTimer;
    public static TimingsHandler activatedEntityTimer;
    public static TimingsHandler tickBlockEntityTimer;
    public static TimingsHandler timerEntityBaseTick;
    public static TimingsHandler timerLivingEntityBaseTick;
    public static TimingsHandler timerEntityAI;
    public static TimingsHandler timerEntityAICollision;
    public static TimingsHandler timerEntityAIMove;
    public static TimingsHandler timerEntityTickRest;
    public static TimingsHandler schedulerSyncTimer;
    public static TimingsHandler schedulerAsyncTimer;
    public static TimingsHandler playerCommandTimer;
    private static final HashMap<String, TimingsHandler> entityTypeTimingMap = new HashMap<>();
    private static final HashMap<String, TimingsHandler> blockEntityTypeTimingMap = new HashMap<>();
    private static final HashMap<Byte, TimingsHandler> packetReceiveTimingMap = new HashMap<>();
    private static final HashMap<Byte, TimingsHandler> packetSendTimingMap = new HashMap<>();
    private static final HashMap<String, TimingsHandler> pluginTaskTimingMap = new HashMap<>();

    public static void init() {
        if (serverTickTimer != null) {
            return;
        }
        fullTickTimer = new TimingsHandler("Full Server Tick");
        serverTickTimer = new TimingsHandler("** Full Server Tick", fullTickTimer);
        memoryManagerTimer = new TimingsHandler("Memory Manager");
        garbageCollectorTimer = new TimingsHandler("Garbage Collector", memoryManagerTimer);
        playerListTimer = new TimingsHandler("Player List");
        playerNetworkTimer = new TimingsHandler("Player Network Send");
        playerNetworkReceiveTimer = new TimingsHandler("Player Network Receive");
        playerChunkOrderTimer = new TimingsHandler("Player Order Chunks");
        playerChunkSendTimer = new TimingsHandler("Player Send Chunks");
        connectionTimer = new TimingsHandler("Connection Handler");
        tickablesTimer = new TimingsHandler("Tickables");
        schedulerTimer = new TimingsHandler("Scheduler");
        chunkIOTickTimer = new TimingsHandler("ChunkIOTick");
        timeUpdateTimer = new TimingsHandler("Time Update");
        serverCommandTimer = new TimingsHandler("Server Command");
        worldSaveTimer = new TimingsHandler("World Save");
        generationTimer = new TimingsHandler("World Generation");
        populationTimer = new TimingsHandler("World Population");
        generationCallbackTimer = new TimingsHandler("World Generation Callback");
        permissibleCalculationTimer = new TimingsHandler("Permissible Calculation");
        permissionDefaultTimer = new TimingsHandler("Default Permission Calculation");
        entityMoveTimer = new TimingsHandler("** entityMove");
        tickEntityTimer = new TimingsHandler("** tickEntity");
        activatedEntityTimer = new TimingsHandler("** activatedTickEntity");
        tickBlockEntityTimer = new TimingsHandler("** tickBlockEntity");
        timerEntityBaseTick = new TimingsHandler("** entityBaseTick");
        timerLivingEntityBaseTick = new TimingsHandler("** livingEntityBaseTick");
        timerEntityAI = new TimingsHandler("** livingEntityAI");
        timerEntityAICollision = new TimingsHandler("** livingEntityAICollision");
        timerEntityAIMove = new TimingsHandler("** livingEntityAIMove");
        timerEntityTickRest = new TimingsHandler("** livingEntityTickRest");
        schedulerSyncTimer = new TimingsHandler("** Scheduler - Sync Tasks", PluginManager.pluginParentTimer);
        schedulerAsyncTimer = new TimingsHandler("** Scheduler - Async Tasks");
        playerCommandTimer = new TimingsHandler("** playerCommand");
    }

    public static TimingsHandler getPluginTaskTimings(TaskHandler task, long period) {
        Runnable ftask = task.getTask();
        String plugin;
        if (ftask instanceof PluginTask && ((PluginTask) ftask).getOwner() != null) {
            plugin = ((PluginTask) ftask).getOwner().getDescription().getFullName();
        } else if (!task.timingName.isEmpty()) {
            plugin = "Scheduler";
        } else {
            plugin = "Unknown";
        }
        String taskname = task.getTaskName();
        String name = "Task: " + plugin + " Runnable: " + taskname;
        if (period > 0) {
            name += "(interval:" + period + ")";
        } else {
            name += "(Single)";
        }
        if (!pluginTaskTimingMap.containsKey(name)) {
            pluginTaskTimingMap.put(name, new TimingsHandler(name, schedulerSyncTimer));
        }
        return pluginTaskTimingMap.get(name);
    }

    public static TimingsHandler getEntityTimings(Entity entity) {
        String entityType = entity.getClass().getSimpleName();
        if (!entityTypeTimingMap.containsKey(entityType)) {
            if (entity instanceof Player) {
                entityTypeTimingMap.put(entityType, new TimingsHandler("** tickEntity - EntityPlayer", tickEntityTimer));
            } else {
                entityTypeTimingMap.put(entityType, new TimingsHandler("** tickEntity - " + entityType, tickEntityTimer));
            }
        }
        return entityTypeTimingMap.get(entityType);
    }

    public static TimingsHandler getBlockEntityTimings(BlockEntity blockEntity) {
        String blockEntityType = blockEntity.getClass().getSimpleName();
        if (!blockEntityTypeTimingMap.containsKey(blockEntityType)) {
            blockEntityTypeTimingMap.put(blockEntityType, new TimingsHandler("** tickBlockEntity - " + blockEntityType, tickBlockEntityTimer));
        }
        return blockEntityTypeTimingMap.get(blockEntityType);
    }

    public static TimingsHandler getReceiveDataPacketTimings(DataPacket pk) {
        if (!packetReceiveTimingMap.containsKey(pk.pid())) {
            String pkName = pk.getClass().getSimpleName();
            packetReceiveTimingMap.put(pk.pid(), new TimingsHandler("** receivePacket - " + pkName + " [0x" + Integer.toHexString(pk.pid()) + "]", playerNetworkReceiveTimer));
        }
        return packetReceiveTimingMap.get(pk.pid());
    }

    public static TimingsHandler getSendDataPacketTimings(DataPacket pk) {
        if (!packetSendTimingMap.containsKey(pk.pid())) {
            String pkName = pk.getClass().getSimpleName();
            packetSendTimingMap.put(pk.pid(), new TimingsHandler("** sendPacket - " + pkName + " [0x" + Integer.toHexString(pk.pid()) + "]", playerNetworkTimer));
        }
        return packetSendTimingMap.get(pk.pid());
    }
}
