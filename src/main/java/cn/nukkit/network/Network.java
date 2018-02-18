package cn.nukkit.network;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Network {

    public static final byte CHANNEL_NONE = 0;
    public static final byte CHANNEL_PRIORITY = 1; //Priority channel, only to be used when it matters
    public static final byte CHANNEL_WORLD_CHUNKS = 2; //Chunk sending
    public static final byte CHANNEL_MOVEMENT = 3; //Movement sending
    public static final byte CHANNEL_BLOCKS = 4; //Block updates or explosions
    public static final byte CHANNEL_WORLD_EVENTS = 5; //Entity, level or blockentity entity events
    public static final byte CHANNEL_ENTITY_SPAWNING = 6; //Entity spawn/despawn channel
    public static final byte CHANNEL_TEXT = 7; //Chat and other text stuff
    public static final byte CHANNEL_END = 31;

    private HashMap<PlayerProtocol, Class<? extends DataPacket>[]> packetPools = new HashMap<>();

    private final Server server;

    private final Set<SourceInterface> interfaces = new HashSet<>();

    private final Set<AdvancedSourceInterface> advancedInterfaces = new HashSet<>();

    private double upload = 0;
    private double download = 0;

    private String name;
    private String subName;

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

    public Set<SourceInterface> getInterfaces() {
        return interfaces;
    }

    public void processInterfaces() {
        for (SourceInterface interfaz : this.interfaces) {
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
        this.interfaces.add(interfaz);
        if (interfaz instanceof AdvancedSourceInterface) {
            this.advancedInterfaces.add((AdvancedSourceInterface) interfaz);
            ((AdvancedSourceInterface) interfaz).setNetwork(this);
        }
        interfaz.setName(this.name + "!@#" + this.subName);
    }

    public void unregisterInterface(SourceInterface interfaz) {
        this.interfaces.remove(interfaz);
        this.advancedInterfaces.remove(interfaz);
    }

    public void setName(String name) {
        this.name = name;
        this.updateName();
    }

    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public void updateName() {
        for (SourceInterface interfaz : this.interfaces) {
            interfaz.setName(this.name + "!@#" + this.subName);
        }
    }

    public void registerPacket(Class<? extends DataPacket> clazz) {
        try {
            for (PlayerProtocol protocol : PlayerProtocol.values()){
                int packetID = clazz.newInstance().pid(protocol);
                if (packetID != 0) {
                    if (!this.packetPools.containsKey(protocol)) this.packetPools.put(protocol, new Class[256]);
                    this.packetPools.get(protocol)[packetID & 0xff] = clazz;
                }
            }
        }
        catch (Exception exc) {}
    }

    public Server getServer() {
        return server;
    }

    public void processBatch(BatchPacket packet, Player player) {
        byte[] data;
        try {
            data = Zlib.inflate(packet.payload, 64 * 1024 * 1024);
        } catch (Exception e) {
            return;
        }

        int len = data.length;
        BinaryStream stream = new BinaryStream(data);
        try {
            List<DataPacket> packets = new ArrayList<>();
            while (stream.offset < len) {
                byte[] buf = stream.getByteArray();

                DataPacket pk;

                if ((pk = this.getPacket(buf[0], player.getProtocol())) != null) {
                    if (pk.getClass().getSimpleName().equals("LoginPacket")){
                        if (buf[4] != 0){ //Protocol 113
                            player.setProtocol(PlayerProtocol.PLAYER_PROTOCOL_113);
                        }
                        else player.setProtocol(PlayerProtocol.PLAYER_PROTOCOL_130);
                    }
                    pk.setBuffer(buf, player.getProtocol().equals(PlayerProtocol.PLAYER_PROTOCOL_113) ? 1 : 3); //skip
                    pk.decode(player.getProtocol());

                    packets.add(pk);
                }
            }

            processPackets(player, packets);

        } catch (Exception e) {
            if (Nukkit.DEBUG > 0) {
                this.server.getLogger().debug("BatchPacket 0x" + Binary.bytesToHexString(packet.payload));
                this.server.getLogger().logException(e);
            }
        }
    }

    /**
     * Process packets obtained from batch packets
     * Required to perform additional analyses and filter unnecessary packets
     *
     * @param packets
     */
    public void processPackets(Player player, List<DataPacket> packets) {
        if (packets.isEmpty()) return;
        packets.forEach(player::handleDataPacket);
    }


    public DataPacket getPacket(byte id, PlayerProtocol protocol) {
        Class<? extends DataPacket> clazz = this.packetPools.get(protocol)[id & 0xff];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        return null;
    }

    public void sendPacket(String address, int port, byte[] payload) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces) {
            interfaz.sendRawPacket(address, port, payload);
        }
    }

    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    public void blockAddress(String address, int timeout) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces) {
            interfaz.blockAddress(address, timeout);
        }
    }

    public void unblockAddress(String address) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces) {
            interfaz.unblockAddress(address);
        }
    }

    private void registerPackets() {
        //this.registerPacket(AddBehaviorTreePacket.class);
        this.registerPacket(AddEntityPacket.class);
        this.registerPacket(AddItemEntityPacket.class);
        this.registerPacket(AddPaintingPacket.class);
        this.registerPacket(AddPlayerPacket.class);
        this.registerPacket(AdventureSettingsPacket.class);
        this.registerPacket(AnimatePacket.class);
        this.registerPacket(AvailableCommandsPacket.class);
        this.registerPacket(BatchPacket.class);
        this.registerPacket(BlockEntityDataPacket.class);
        this.registerPacket(BlockEventPacket.class);
        this.registerPacket(BlockPickRequestPacket.class);
        //this.registerPacket(BookEditPacket.class);
        this.registerPacket(BossEventPacket.class);
        //this.registerPacket(CameraPacket.class);
        this.registerPacket(ChangeDimensionPacket.class);
        this.registerPacket(ChunkRadiusUpdatedPacket.class);
        this.registerPacket(ClientboundMapItemDataPacket.class);
        //this.registerPacket(ClientToServerHandshakePacket.class);
        //this.registerPacket(CommandBlockUpdatePacket.class);
        this.registerPacket(CommandRequestPacket.class);
        this.registerPacket(ContainerClosePacket.class);
        this.registerPacket(ContainerOpenPacket.class);
        this.registerPacket(ContainerSetDataPacket.class);
        this.registerPacket(CraftingDataPacket.class);
        this.registerPacket(CraftingEventPacket.class);
        this.registerPacket(DisconnectPacket.class);
        this.registerPacket(EntityEventPacket.class);
        this.registerPacket(EntityFallPacket.class);
        //this.registerPacket(EntityPickRequestPacket.class);
        this.registerPacket(ExplodePacket.class);
        this.registerPacket(FullChunkDataPacket.class);
        this.registerPacket(GameRulesChangedPacket.class);
        //this.registerPacket(GUIDataPickItemPacket.class);
        this.registerPacket(HurtArmorPacket.class);
        //this.registerPacket(InitiateWebSocketConnectionPacket.class);
        this.registerPacket(InteractPacket.class);
        this.registerPacket(InventoryContentPacket.class);
        this.registerPacket(InventorySlotPacket.class);
        this.registerPacket(InventoryTransactionPacket.class);
        this.registerPacket(ItemFrameDropItemPacket.class);
        this.registerPacket(LevelEventPacket.class);
        this.registerPacket(LevelSoundEventPacket.class);
        this.registerPacket(LoginPacket.class);
        this.registerPacket(MapInfoRequestPacket.class);
        this.registerPacket(MobArmorEquipmentPacket.class);
        //this.registerPacket(MobEffectPacket.class);
        this.registerPacket(MobEquipmentPacket.class);
        this.registerPacket(ModalFormRequestPacket.class);
        this.registerPacket(ModalFormResponsePacket.class);
        this.registerPacket(MoveEntityPacket.class);
        this.registerPacket(MovePlayerPacket.class);
        //this.registerPacket(NPCRequestPacket.class);
        this.registerPacket(PlayerActionPacket.class);
        //this.registerPacket(PLAYER_HOTBAR_PACKET PlayerHotbarPacket.class); ToDo fix NPE while encoding
        this.registerPacket(PlayerInputPacket.class);
        this.registerPacket(PlayerListPacket.class);
        this.registerPacket(PlayerSkinPacket.class);
        this.registerPacket(PlaySoundPacket.class);
        this.registerPacket(PlayStatusPacket.class);
        this.registerPacket(RemoveBlockPacket.class);
        this.registerPacket(RemoveEntityPacket.class);
        this.registerPacket(RequestChunkRadiusPacket.class);
        this.registerPacket(ResourcePackChunkDataPacket.class);
        this.registerPacket(ResourcePackChunkRequestPacket.class);
        this.registerPacket(ResourcePackClientResponsePacket.class);
        this.registerPacket(ResourcePackDataInfoPacket.class);
        this.registerPacket(ResourcePacksInfoPacket.class);
        this.registerPacket(ResourcePackStackPacket.class);
        this.registerPacket(RespawnPacket.class);
        this.registerPacket(RiderJumpPacket.class);
        this.registerPacket(ServerSettingsRequestPacket.class);
        this.registerPacket(ServerSettingsResponsePacket.class);
        this.registerPacket(SetCommandsEnabledPacket.class);
        this.registerPacket(SetDifficultyPacket.class);
        this.registerPacket(SetEntityDataPacket.class);
        this.registerPacket(SetEntityLinkPacket.class);
        this.registerPacket(SetEntityMotionPacket.class);
        this.registerPacket(SetHealthPacket.class);
        //this.register(SetLastHurtByPacket.class);
        this.registerPacket(SetPlayerGameTypePacket.class);
        this.registerPacket(SetSpawnPositionPacket.class);
        this.registerPacket(SetTimePacket.class);
        this.registerPacket(SetTitlePacket.class);
        this.registerPacket(ShowCreditsPacket.class);
        this.registerPacket(ShowProfilePacket.class);
        //this.registerPacket(SimpleEventPacket.class);
        this.registerPacket(SpawnExperienceOrbPacket.class);
        this.registerPacket(StartGamePacket.class);
        this.registerPacket(StopSoundPacket.class);
        //this.registerPacket(StructureBlockUpdatePacket.class);
        //this.registerPacket(SubClientLoginPacket.class);
        this.registerPacket(TakeItemEntityPacket.class);
        //this.registerPacket(TelemetryEventPacket.class);
        this.registerPacket(TextPacket.class);
        this.registerPacket(TransferPacket.class);
        this.registerPacket(UpdateAttributesPacket.class);
        this.registerPacket(UpdateBlockPacket.class);
        //this.registerPacket(UpdateEquipmentPacket.class);
        this.registerPacket(UpdateTradePacket.class);
        this.registerPacket(UseItemPacket.class);
    }
}
