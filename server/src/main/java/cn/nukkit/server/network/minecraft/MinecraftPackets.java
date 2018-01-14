package cn.nukkit.server.network.minecraft;

import cn.nukkit.server.network.PacketType;
import cn.nukkit.server.network.Packets;
import cn.nukkit.server.network.minecraft.packet.*;
import cn.nukkit.server.network.raknet.NetworkPacket;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;

@UtilityClass
@SuppressWarnings({"unchecked"})
public final class MinecraftPackets {

    public static final PacketType TYPE = PacketType.forName("MINECRAFT");
    public static final int BROADCAST_PROTOCOL_VERSION = 160;
    private static final TIntSet SUPPORTED_PROTOCOL_VERSIONS = new TIntHashSet();
    private static final ImmutableBiMap<Integer, String> VERSION_STRINGS;
    private static final Class<? extends MinecraftPacket>[] MINECRAFT_PACKETS = new Class[256];


    static {
        MINECRAFT_PACKETS[1] = LoginPacket.class;
        MINECRAFT_PACKETS[2] = PlayStatusPacket.class;
        MINECRAFT_PACKETS[3] = ServerToClientHandshakePacket.class;
        MINECRAFT_PACKETS[4] = ClientToServerHandshakePacket.class;
        MINECRAFT_PACKETS[5] = DisconnectPacket.class;
        MINECRAFT_PACKETS[6] = ResourcePacksInfoPacket.class;
        MINECRAFT_PACKETS[7] = ResourcePackStackPacket.class;
        MINECRAFT_PACKETS[8] = ResourcePackClientResponsePacket.class;
        MINECRAFT_PACKETS[9] = TextPacket.class;
        MINECRAFT_PACKETS[10] = SetTimePacket.class;
        MINECRAFT_PACKETS[11] = StartGamePacket.class;
        MINECRAFT_PACKETS[12] = AddPlayerPacket.class;
        MINECRAFT_PACKETS[13] = AddEntityPacket.class;
        MINECRAFT_PACKETS[14] = RemoveEntityPacket.class;
        MINECRAFT_PACKETS[15] = AddItemEntityPacket.class;
        MINECRAFT_PACKETS[16] = AddHangingEntityPacket.class;
        MINECRAFT_PACKETS[17] = TakeItemEntityPacket.class;
        MINECRAFT_PACKETS[18] = MoveEntityPacket.class;
        MINECRAFT_PACKETS[19] = MovePlayerPacket.class;
        MINECRAFT_PACKETS[20] = RiderJumpPacket.class;
        MINECRAFT_PACKETS[21] = UpdateBlockPacket.class;
        MINECRAFT_PACKETS[22] = AddPaintingPacket.class;
        MINECRAFT_PACKETS[23] = ExplodePacket.class;
        MINECRAFT_PACKETS[24] = LevelSoundEventPacket.class;
        MINECRAFT_PACKETS[25] = LevelEventPacket.class;
        MINECRAFT_PACKETS[26] = BlockEventPacket.class;
        MINECRAFT_PACKETS[27] = EntityEventPacket.class;
        MINECRAFT_PACKETS[28] = MobEffectPacket.class;
        MINECRAFT_PACKETS[29] = UpdateAttributesPacket.class;
        MINECRAFT_PACKETS[30] = InventoryTransactionPacket.class;
        MINECRAFT_PACKETS[31] = MobEquipmentPacket.class;
        MINECRAFT_PACKETS[32] = MobArmorEquipmentPacket.class;
        MINECRAFT_PACKETS[33] = InteractPacket.class;
        MINECRAFT_PACKETS[34] = BlockPickRequestPacket.class;
        MINECRAFT_PACKETS[35] = EntityPickRequestPacket.class;
        MINECRAFT_PACKETS[36] = PlayerActionPacket.class;
        MINECRAFT_PACKETS[37] = EntityFallPacket.class;
        MINECRAFT_PACKETS[38] = HurtArmorPacket.class;
        MINECRAFT_PACKETS[39] = SetEntityDataPacket.class;
        MINECRAFT_PACKETS[40] = SetEntityMotionPacket.class;
        MINECRAFT_PACKETS[41] = SetEntityLinkPacket.class;
        MINECRAFT_PACKETS[42] = SetHealthPacket.class;
        MINECRAFT_PACKETS[43] = SetSpawnPositionPacket.class;
        MINECRAFT_PACKETS[44] = AnimatePacket.class;
        MINECRAFT_PACKETS[45] = RespawnPacket.class;
        MINECRAFT_PACKETS[46] = ContainerOpenPacket.class;
        MINECRAFT_PACKETS[47] = ContainerClosePacket.class;
        MINECRAFT_PACKETS[48] = PlayerHotbarPacket.class;
        MINECRAFT_PACKETS[49] = InventoryContentPacket.class;
        MINECRAFT_PACKETS[50] = InventorySlotPacket.class;
        MINECRAFT_PACKETS[51] = ContainerSetDataPacket.class;
        MINECRAFT_PACKETS[52] = CraftingDataPacket.class;
        MINECRAFT_PACKETS[53] = CraftingEventPacket.class;
        MINECRAFT_PACKETS[54] = GuiDataPickItemPacket.class;
        MINECRAFT_PACKETS[55] = AdventureSettingsPacket.class;
        MINECRAFT_PACKETS[56] = BlockEntityDataPacket.class;
        MINECRAFT_PACKETS[57] = PlayerInputPacket.class;
        MINECRAFT_PACKETS[58] = FullChunkDataPacket.class;
        MINECRAFT_PACKETS[59] = SetCommandsEnabledPacket.class;
        MINECRAFT_PACKETS[60] = SetDifficultyPacket.class;
        MINECRAFT_PACKETS[61] = ChangeDimensionPacket.class;
        MINECRAFT_PACKETS[62] = SetPlayerGameTypePacket.class;
        MINECRAFT_PACKETS[63] = PlayerListPacket.class;
        MINECRAFT_PACKETS[64] = SimpleEventPacket.class;
        MINECRAFT_PACKETS[65] = EventPacket.class;
        MINECRAFT_PACKETS[66] = SpawnExperienceOrbPacket.class;
        MINECRAFT_PACKETS[67] = ClientboundMapItemDataPacket.class;
        MINECRAFT_PACKETS[68] = MapInfoRequestPacket.class;
        MINECRAFT_PACKETS[69] = RequestChunkRadiusPacket.class;
        MINECRAFT_PACKETS[70] = ChunkRadiusUpdatePacket.class;
        MINECRAFT_PACKETS[71] = ItemFrameDropItemPacket.class;
        MINECRAFT_PACKETS[72] = GameRulesChangedPacket.class;
        MINECRAFT_PACKETS[73] = CameraPacket.class;
        MINECRAFT_PACKETS[74] = BossEventPacket.class;
        MINECRAFT_PACKETS[75] = ShowCreditsPacket.class;
        MINECRAFT_PACKETS[76] = AvailableCommandsPacket.class;
        MINECRAFT_PACKETS[77] = CommandRequestPacket.class;
        MINECRAFT_PACKETS[78] = CommandBlockUpdatePacket.class;
        MINECRAFT_PACKETS[79] = CommandOutputPacket.class;
        MINECRAFT_PACKETS[80] = UpdateTradePacket.class;
        MINECRAFT_PACKETS[81] = UpdateEquipPacket.class;
        MINECRAFT_PACKETS[82] = ResourcePackDataInfoPacket.class;
        MINECRAFT_PACKETS[83] = ResourcePackChunkDataPacket.class;
        MINECRAFT_PACKETS[84] = ResourcePackChunkRequestPacket.class;
        MINECRAFT_PACKETS[85] = TransferPacket.class;
        MINECRAFT_PACKETS[86] = PlaySoundPacket.class;
        MINECRAFT_PACKETS[87] = StopSoundPacket.class;
        MINECRAFT_PACKETS[88] = SetTitlePacket.class;
        MINECRAFT_PACKETS[89] = AddBehaviorTreePacket.class;
        MINECRAFT_PACKETS[90] = StructureBlockUpdatePacket.class;
        MINECRAFT_PACKETS[91] = ShowStoreOfferPacket.class;
        MINECRAFT_PACKETS[92] = PurchaseReceiptPacket.class;
        MINECRAFT_PACKETS[93] = PlayerSkinPacket.class;
        MINECRAFT_PACKETS[94] = SubClientLoginPacket.class;
        MINECRAFT_PACKETS[95] = InitiateWebSocketConnectionPacket.class;
        MINECRAFT_PACKETS[96] = SetLastHurtByPacket.class;
        MINECRAFT_PACKETS[97] = BookEditPacket.class;
        MINECRAFT_PACKETS[98] = NpcRequestPacket.class;
        MINECRAFT_PACKETS[99] = PhotoTransferPacket.class;
        MINECRAFT_PACKETS[100] = ModalFormRequestPacket.class;
        MINECRAFT_PACKETS[101] = ModalFormResponsePacket.class;
        MINECRAFT_PACKETS[102] = ServerSettingsRequestPacket.class;
        MINECRAFT_PACKETS[103] = ServerSettingsResponsePacket.class;
        MINECRAFT_PACKETS[104] = ShowProfilePacket.class;
        MINECRAFT_PACKETS[105] = SetDefaultGameTypePacket.class;

        Packets.registerPacketMappings(TYPE, MINECRAFT_PACKETS, new MinecraftPacketCodec());

        // Support protocol versions for Nukkit
        SUPPORTED_PROTOCOL_VERSIONS.addAll(new int[]{150, BROADCAST_PROTOCOL_VERSION});

        // Version Strings
        VERSION_STRINGS = ImmutableBiMap.<Integer, String>builder()
                .put(34, "0.12.1")
                .put(38, "0.13.0")
                .put(39, "0.13.2")
                .put(45, "0.14.0")
                .put(70, "0.14.3")
                .put(81, "0.15.0")
                .put(90, "0.16.0")
                .put(91, "0.17.0")
                .put(102, "1.0.4")
                .put(105, "1.0.5")
                .put(113, "1.1.3")
                .put(137, "1.2.0")
                .put(141, "1.2.5")
                .put(150, "1.2.6")
                .put(160, "1.2.8")
                .build();
    }

    public static boolean isProtocolCompatible(int protocolVersion) {
        return SUPPORTED_PROTOCOL_VERSIONS.contains(protocolVersion);
    }

    public static String getVersionFromProtocol(int protocolVersion) {
        String version = VERSION_STRINGS.get(protocolVersion);
        if (version == null) {
            throw new IllegalArgumentException(protocolVersion + " is not a valid protocol version!");
        }
        return version;
    }

    public static int getProtocolFromVersion(@Nonnull String version) {
        Preconditions.checkNotNull(version, "version");
        Integer protocolVersion = VERSION_STRINGS.inverse().get(version);
        if (protocolVersion == null) {
            throw new IllegalArgumentException(version + " is not a valid version!");
        }
        return protocolVersion;
    }

    @Log4j2
    private static class MinecraftPacketCodec extends Packets.PacketCodec {
        private static final byte[] BATCH_SPACE = new byte[]{0x00, 0x00};

        public NetworkPacket tryDecode(ByteBuf buf) {
            int id = buf.readUnsignedByte();
            Class<? extends NetworkPacket> packetClass = MINECRAFT_PACKETS[id];

            if (packetClass == null) {
                // Class is null so we can't instantiate it.
                return null;
            }

            NetworkPacket packet = instantiateClass(packetClass);

            buf.skipBytes(2); // Two byte space between ID and Payload.
            // Decode packet
            packet.decode(buf);

            if (log.isDebugEnabled() && buf.readableBytes() > 0) {
                log.debug("{} still has {} more bytes to read", packetClass.getSimpleName(), buf.readableBytes());
            }

            return packet;
        }

        @Override
        public ByteBuf tryEncode(NetworkPacket packet) {
            int id = Packets.getId(packet);
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
            buf.writeByte((id & 0xFF));

            if (packet instanceof MinecraftPacket) {
                buf.writeBytes(BATCH_SPACE);
            }

            packet.encode(buf);

            return buf;
        }

        protected Class<? extends NetworkPacket>[] getPackets() {
            return MINECRAFT_PACKETS;
        }
    }
}
