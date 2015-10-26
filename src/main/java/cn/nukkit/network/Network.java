package cn.nukkit.network;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Zlib;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Network {
    public static int BATCH_THRESHOLD = 512;

    public static final byte CHANNEL_NONE = 0;
    public static final byte CHANNEL_PRIORITY = 1; //Priority channel, only to be used when it matters
    public static final byte CHANNEL_WORLD_CHUNKS = 2; //Chunk sending
    public static final byte CHANNEL_MOVEMENT = 3; //Movement sending
    public static final byte CHANNEL_BLOCKS = 4; //Block updates or explosions
    public static final byte CHANNEL_WORLD_EVENTS = 5; //Entity, level or tile entity events
    public static final byte CHANNEL_ENTITY_SPAWNING = 6; //Entity spawn/despawn channel
    public static final byte CHANNEL_TEXT = 7; //Chat and other text stuff
    public static final byte CHANNEL_END = 31;

    private Class<? extends DataPacket>[] packetPool = new Class[256];

    private Server server;

    private Map<Integer, SourceInterface> interfaces = new HashMap<>();

    private Map<Integer, AdvancedSourceInterface> advancedInterfaces = new HashMap<>();

    private double upload = 0;
    private double download = 0;

    private String name;

    public Network(Server server) {
        this.registerPackets();
        this.server = server;
    }

    public void addStatistics(double upload, double download) {
        this.upload += upload;
        this.download += download;
    }

    public double getUpload() {
        return upload;
    }

    public double getDownload() {
        return download;
    }

    public void resetStatistics() {
        this.upload = 0;
        this.download = 0;
    }

    public Map<Integer, SourceInterface> getInterfaces() {
        return interfaces;
    }

    public void processInterfaces() {
        for (SourceInterface interfaz : this.interfaces.values()) {
            try {
                interfaz.process();
            } catch (Exception e) {
                if (Nukkit.DEBUG > 1) {
                    this.server.getLogger().logException(e);
                }

                interfaz.emergencyShutdown();
                this.unregisterInterface(interfaz);
                this.server.getLogger().critical(this.server.getLanguage().translateString("nukkit.server.networkError", new String[]{interfaz.getClass().getName(), e.getMessage()}));
            }
        }
    }

    public void registerInterface(SourceInterface interfaz) {
        this.interfaces.put(interfaz.hashCode(), interfaz);
        if (interfaz instanceof AdvancedSourceInterface) {
            this.advancedInterfaces.put(interfaz.hashCode(), (AdvancedSourceInterface) interfaz);
            ((AdvancedSourceInterface) interfaz).setNetwork(this);
        }
        interfaz.setName(this.name);
    }

    public void unregisterInterface(SourceInterface interfaz) {
        this.interfaces.remove(interfaz.hashCode());
        this.advancedInterfaces.remove(interfaz.hashCode());
    }

    public void setName(String name) {
        this.name = name;
        this.updateName();
    }

    public String getName() {
        return name;
    }

    public void updateName() {
        for (SourceInterface interfaz : this.interfaces.values()) {
            interfaz.setName(this.name);
        }
    }

    public void registerPacket(byte id, Class<? extends DataPacket> clazz) {
        this.packetPool[id & 0xff] = clazz;
    }

    public Server getServer() {
        return server;
    }

    public void processBatch(BatchPacket packet, Player p) {
        byte[] data;
        try {
            data = Zlib.inflate(packet.payload, 64 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int len = data.length;
        int offset = 0;
        try {
            while (offset < len) {
                DataPacket pk;
                if ((pk = this.getPacket(data[offset++])) != null) {
                    if (pk.pid() == Info.BATCH_PACKET) {
                        throw new IllegalStateException("Invalid BatchPacket inside BatchPacket");
                    }
                    pk.setBuffer(data);
                    pk.setOffset(offset);
                    pk.decode();
                    p.handleDataPacket(pk);
                    offset += pk.getOffset();
                    if (pk.getOffset() <= 0) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            if (Nukkit.DEBUG > 0) {
                this.server.getLogger().debug("BatchPacket 0x" + Binary.bytesToHexString(packet.payload));
                this.server.getLogger().logException(e);
            }
        }
    }

    public DataPacket getPacket(byte id) {
        Class<? extends DataPacket> clazz = this.packetPool[id];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sendPacket(String address, int port, byte[] payload) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces.values()) {
            interfaz.sendRawPacket(address, port, payload);
        }
    }

    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    public void blockAddress(String address, int timeout) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces.values()) {
            interfaz.blockAddress(address, timeout);
        }
    }

    private void registerPackets() {
        this.packetPool = new Class[256];

        this.registerPacket(Info.LOGIN_PACKET, LoginPacket.class);
        this.registerPacket(Info.PLAY_STATUS_PACKET, PlayStatusPacket.class);
        this.registerPacket(Info.DISCONNECT_PACKET, DisconnectPacket.class);
        this.registerPacket(Info.BATCH_PACKET, BatchPacket.class);
        this.registerPacket(Info.TEXT_PACKET, TextPacket.class);
        this.registerPacket(Info.SET_TIME_PACKET, SetTimePacket.class);
        this.registerPacket(Info.START_GAME_PACKET, StartGamePacket.class);
        this.registerPacket(Info.ADD_PLAYER_PACKET, AddPlayerPacket.class);
        this.registerPacket(Info.REMOVE_PLAYER_PACKET, RemovePlayerPacket.class);
        this.registerPacket(Info.ADD_ENTITY_PACKET, AddEntityPacket.class);
        this.registerPacket(Info.REMOVE_ENTITY_PACKET, RemoveEntityPacket.class);
        this.registerPacket(Info.ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket.class);
        this.registerPacket(Info.TAKE_ITEM_ENTITY_PACKET, TakeItemEntityPacket.class);
        this.registerPacket(Info.MOVE_ENTITY_PACKET, MoveEntityPacket.class);
        this.registerPacket(Info.MOVE_PLAYER_PACKET, MovePlayerPacket.class);
        this.registerPacket(Info.REMOVE_BLOCK_PACKET, RemoveBlockPacket.class);
        this.registerPacket(Info.UPDATE_BLOCK_PACKET, UpdateBlockPacket.class);
        this.registerPacket(Info.ADD_PAINTING_PACKET, AddPaintingPacket.class);
        this.registerPacket(Info.EXPLODE_PACKET, ExplodePacket.class);
        this.registerPacket(Info.LEVEL_EVENT_PACKET, LevelEventPacket.class);
        this.registerPacket(Info.TILE_EVENT_PACKET, TileEventPacket.class);
        this.registerPacket(Info.ENTITY_EVENT_PACKET, EntityEventPacket.class);
        this.registerPacket(Info.MOB_EQUIPMENT_PACKET, MobEquipmentPacket.class);
        this.registerPacket(Info.MOB_ARMOR_EQUIPMENT_PACKET, MobArmorEquipmentPacket.class);
        this.registerPacket(Info.INTERACT_PACKET, InteractPacket.class);
        this.registerPacket(Info.USE_ITEM_PACKET, UseItemPacket.class);
        this.registerPacket(Info.PLAYER_ACTION_PACKET, PlayerActionPacket.class);
        this.registerPacket(Info.HURT_ARMOR_PACKET, HurtArmorPacket.class);
        this.registerPacket(Info.SET_ENTITY_DATA_PACKET, SetEntityDataPacket.class);
        this.registerPacket(Info.SET_ENTITY_MOTION_PACKET, SetEntityMotionPacket.class);
        this.registerPacket(Info.SET_ENTITY_LINK_PACKET, SetEntityLinkPacket.class);
        //this.registerPacket(Info.SET_HEALTH_PACKET, SetHealthPacket.class);
        this.registerPacket(Info.SET_SPAWN_POSITION_PACKET, SetSpawnPositionPacket.class);
        this.registerPacket(Info.ANIMATE_PACKET, AnimatePacket.class);
        this.registerPacket(Info.RESPAWN_PACKET, RespawnPacket.class);
        this.registerPacket(Info.DROP_ITEM_PACKET, DropItemPacket.class);
        this.registerPacket(Info.CONTAINER_OPEN_PACKET, ContainerOpenPacket.class);
        this.registerPacket(Info.CONTAINER_CLOSE_PACKET, ContainerClosePacket.class);
        this.registerPacket(Info.CONTAINER_SET_SLOT_PACKET, ContainerSetSlotPacket.class);
        this.registerPacket(Info.CONTAINER_SET_DATA_PACKET, ContainerSetDataPacket.class);
        this.registerPacket(Info.CONTAINER_SET_CONTENT_PACKET, ContainerSetContentPacket.class);
        this.registerPacket(Info.CRAFTING_DATA_PACKET, CraftingDataPacket.class);
        this.registerPacket(Info.CRAFTING_EVENT_PACKET, CraftingEventPacket.class);
        this.registerPacket(Info.ADVENTURE_SETTINGS_PACKET, AdventureSettingsPacket.class);
        this.registerPacket(Info.TILE_ENTITY_DATA_PACKET, TileEntityDataPacket.class);
        this.registerPacket(Info.FULL_CHUNK_DATA_PACKET, FullChunkDataPacket.class);
        this.registerPacket(Info.SET_DIFFICULTY_PACKET, SetDifficultyPacket.class);
        this.registerPacket(Info.PLAYER_LIST_PACKET, PlayerListPacket.class);
    }
}
