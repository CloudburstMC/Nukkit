package cn.nukkit.network;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.VarInt;
import io.netty.buffer.ByteBuf;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@Log4j2
@SuppressWarnings("unchecked")
public class Network {

    private Class<? extends DataPacket>[] packetPool = new Class[256];

    private final Server server;

    private final Set<SourceInterface> interfaces = new HashSet<>();

    private final Set<AdvancedSourceInterface> advancedInterfaces = new HashSet<>();

    private double upload;
    private double download;

    private String name;
    private String subName;

    public Network(Server server) {
        this.registerPackets();
        this.server = server;
    }

    @Deprecated
    public void addStatistics(double upload, double download) {
        this.upload += upload;
        this.download += download;
    }

    @Deprecated
    public double getUpload() {
        return upload;
    }

    @Deprecated
    public double getDownload() {
        return download;
    }

    @Deprecated
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
                interfaz.emergencyShutdown();
                this.unregisterInterface(interfaz);
                log.fatal(this.server.getLanguage().translateString("nukkit.server.networkError", new String[]{interfaz.getClass().getName(), Utils.getExceptionMessage(e)}), e);
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

    public void unregisterInterface(SourceInterface sourceInterface) {
        this.interfaces.remove(sourceInterface);
        if (sourceInterface instanceof AdvancedSourceInterface) {
            this.advancedInterfaces.remove(sourceInterface);
        }
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

    public void registerPacket(byte id, Class<? extends DataPacket> clazz) {
        this.packetPool[id & 0xff] = clazz;
    }

    public Server getServer() {
        return server;
    }

    public void processBatch(byte[] payload, Collection<DataPacket> packets, CompressionProvider compression) throws Exception {
        byte[] data = compression.decompress(payload, 6291456);

        BinaryStream stream = new BinaryStream(data);
        int count = 0;
        while (!stream.feof()) {
            count++;
            if (count > 1300) {
                throw new ProtocolException("Too big batch packet (count > 1300)");
            }

            byte[] buf = stream.getByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);

            int packetId = ((int) VarInt.readUnsignedVarInt(bais) & 0x3ff);

            // Use internal backwards compatible IDs until pid() is rewritten
            DataPacket pk = this.getPacket(packetId >= 300 ? packetId - 100 : packetId);
            if (pk == null) {
                if (Nukkit.DEBUG > 1) {
                    log.debug("Received unknown packet with ID: 0x{}", Integer.toHexString(packetId));
                }
                continue;
            }

            pk.setBuffer(buf, buf.length - bais.available());

            try {
                pk.decode();

                if (Nukkit.DEBUG > 1 && pk.offset < pk.getRawBuffer().length) {
                    log.debug(pk.getClass().getSimpleName() + " still has " + (pk.getRawBuffer().length - pk.offset) + " bytes to read!");
                }
            } catch (Exception e) {
                throw new IllegalStateException("Unable to decode " + pk.getClass().getSimpleName(), e);
            }

            packets.add(pk);
        }
    }

    public DataPacket getPacket(int id) {
        if (id < 0 || id >= this.packetPool.length) {
            return null;
        }
        Class<? extends DataPacket> clazz = this.packetPool[id];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        return null;
    }

    public void sendPacket(InetSocketAddress socketAddress, ByteBuf payload) {
        for (AdvancedSourceInterface sourceInterface : this.advancedInterfaces) {
            sourceInterface.sendRawPacket(socketAddress, payload);
        }
    }

    public void blockAddress(InetAddress address) {
        for (AdvancedSourceInterface sourceInterface : this.advancedInterfaces) {
            sourceInterface.blockAddress(address);
        }
    }

    public void blockAddress(InetAddress address, int timeout) {
        for (AdvancedSourceInterface sourceInterface : this.advancedInterfaces) {
            sourceInterface.blockAddress(address, timeout);
        }
    }

    public void unblockAddress(InetAddress address) {
        for (AdvancedSourceInterface sourceInterface : this.advancedInterfaces) {
            sourceInterface.unblockAddress(address);
        }
    }

    private void registerPackets() {
        this.packetPool = new Class[256];

        this.registerPacket(ProtocolInfo.BATCH_PACKET, BatchPacket.class);

        this.registerPacket(ProtocolInfo.ANIMATE_PACKET, AnimatePacket.class);
        this.registerPacket(ProtocolInfo.BLOCK_ENTITY_DATA_PACKET, BlockEntityDataPacket.class);
        this.registerPacket(ProtocolInfo.BLOCK_PICK_REQUEST_PACKET, BlockPickRequestPacket.class);
        this.registerPacket(ProtocolInfo.BOOK_EDIT_PACKET, BookEditPacket.class);
        this.registerPacket(ProtocolInfo.COMMAND_REQUEST_PACKET, CommandRequestPacket.class);
        this.registerPacket(ProtocolInfo.CONTAINER_CLOSE_PACKET, ContainerClosePacket.class);
        this.registerPacket(ProtocolInfo.ENTITY_EVENT_PACKET, EntityEventPacket.class);
        this.registerPacket(ProtocolInfo.INTERACT_PACKET, InteractPacket.class);
        this.registerPacket(ProtocolInfo.INVENTORY_TRANSACTION_PACKET, InventoryTransactionPacket.class);
        this.registerPacket(ProtocolInfo.LOGIN_PACKET, LoginPacket.class);
        this.registerPacket(ProtocolInfo.MAP_INFO_REQUEST_PACKET, MapInfoRequestPacket.class);
        this.registerPacket(ProtocolInfo.MOB_EQUIPMENT_PACKET, MobEquipmentPacket.class);
        this.registerPacket(ProtocolInfo.MODAL_FORM_RESPONSE_PACKET, ModalFormResponsePacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_ACTION_PACKET, PlayerActionPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_HOTBAR_PACKET, PlayerHotbarPacket.class);
        this.registerPacket(ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET, RequestChunkRadiusPacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET, ResourcePackClientResponsePacket.class);
        this.registerPacket(ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET, ResourcePackChunkRequestPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_SKIN_PACKET, PlayerSkinPacket.class);
        this.registerPacket(ProtocolInfo.RESPAWN_PACKET, RespawnPacket.class);
        this.registerPacket(ProtocolInfo.SET_DIFFICULTY_PACKET, SetDifficultyPacket.class);
        this.registerPacket(ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET, SetPlayerGameTypePacket.class);
        this.registerPacket(ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET, ServerSettingsRequestPacket.class);
        this.registerPacket(ProtocolInfo.TEXT_PACKET, TextPacket.class);
        this.registerPacket(ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET, SetLocalPlayerAsInitializedPacket.class);
        this.registerPacket(ProtocolInfo.LECTERN_UPDATE_PACKET, LecternUpdatePacket.class);
        this.registerPacket(ProtocolInfo.NETWORK_SETTINGS_PACKET, NetworkSettingsPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_AUTH_INPUT_PACKET, PlayerAuthInputPacket.class);
        this.registerPacket(ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET, PacketViolationWarningPacket.class);
        this.registerPacket(ProtocolInfo.EMOTE_PACKET, EmotePacket.class);
        this.registerPacket(ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET, RequestNetworkSettingsPacket.class);
        this.registerPacket(ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET, ClientToServerHandshakePacket.class);
        this.registerPacket(ProtocolInfo.REQUEST_PERMISSIONS_PACKET, RequestPermissionsPacket.class);
        this.registerPacket(ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET, SetDefaultGameTypePacket.class);
        this.registerPacket(ProtocolInfo.SETTINGS_COMMAND_PACKET, SettingsCommandPacket.class);

        // Unused but sent by the client
        this.registerPacket(ProtocolInfo.SET_ENTITY_LINK_PACKET, SetEntityLinkPacket.class);
        this.registerPacket(ProtocolInfo.SET_ENTITY_MOTION_PACKET, SetEntityMotionPacket.class);
        this.registerPacket(ProtocolInfo.LEVEL_SOUND_EVENT_PACKET, LevelSoundEventPacket.class);
        this.registerPacket(ProtocolInfo.REQUEST_ABILITY_PACKET, RequestAbilityPacket.class);
        this.registerPacket(ProtocolInfo.NETWORK_STACK_LATENCY_PACKET, NetworkStackLatencyPacket.class);
        this.registerPacket(ProtocolInfo.NPC_REQUEST_PACKET, NPCRequestPacket.class);
        this.registerPacket(ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET, MoveEntityAbsolutePacket.class);
        this.registerPacket(ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET, MobArmorEquipmentPacket.class);
        this.registerPacket(ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET, MapCreateLockedCopyPacket.class);
        this.registerPacket(ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET, GUIDataPickItemPacket.class);
        this.registerPacket(ProtocolInfo.EMOTE_LIST_PACKET, EmoteListPacket.class);
        this.registerPacket(ProtocolInfo.DISCONNECT_PACKET, DisconnectPacket.class);
        this.registerPacket(ProtocolInfo.BOSS_EVENT_PACKET, BossEventPacket.class);
        this.registerPacket(ProtocolInfo.ANVIL_DAMAGE_PACKET, AnvilDamagePacket.class);
    }
}
