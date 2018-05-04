package com.nukkitx.server.network.minecraft;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.nukkitx.server.network.PacketCodec;
import com.nukkitx.server.network.PacketFactory;
import com.nukkitx.server.network.minecraft.packet.*;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;

@Log4j2
public class MinecraftPacketRegistry implements PacketCodec<MinecraftPacket> {
    public static final int BROADCAST_PROTOCOL_VERSION = 271;
    private static final MinecraftPacketRegistry INSTANCE = new MinecraftPacketRegistry();
    private static final TIntSet SUPPORTED_PROTOCOL_VERSIONS = new TIntHashSet();
    private static final ImmutableBiMap<Integer, String> VERSION_STRINGS;
    private final PacketFactory<MinecraftPacket>[] factories = (PacketFactory<MinecraftPacket>[]) new PacketFactory[256];
    private final TObjectByteMap<Class<? extends MinecraftPacket>> idMapping = new TObjectByteHashMap<>(64, 0.75f, (byte) -1);

    static {
        // Support protocol versions for Nukkit
        SUPPORTED_PROTOCOL_VERSIONS.addAll(new int[]{270, BROADCAST_PROTOCOL_VERSION});

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
                .put(161, "1.2.10")
                .put(223, "1.2.13")
                .build();
    }

    private MinecraftPacketRegistry() {
        factories[1] = LoginPacket::new;
        factories[2] = PlayStatusPacket::new;
        factories[3] = ServerToClientHandshakePacket::new;
        factories[4] = ClientToServerHandshakePacket::new;
        factories[5] = DisconnectPacket::new;
        factories[6] = ResourcePacksInfoPacket::new;
        factories[7] = ResourcePackStackPacket::new;
        factories[8] = ResourcePackClientResponsePacket::new;
        factories[9] = TextPacket::new;
        factories[10] = SetTimePacket::new;
        factories[11] = StartGamePacket::new;
        factories[12] = AddPlayerPacket::new;
        factories[13] = AddEntityPacket::new;
        factories[14] = RemoveEntityPacket::new;
        factories[15] = AddItemEntityPacket::new;
        factories[16] = AddHangingEntityPacket::new;
        factories[17] = TakeItemEntityPacket::new;
        factories[18] = MoveEntityPacket::new;
        factories[19] = MovePlayerPacket::new;
        factories[20] = RiderJumpPacket::new;
        factories[21] = UpdateBlockPacket::new;
        factories[22] = AddPaintingPacket::new;
        factories[23] = ExplodePacket::new;
        factories[24] = LevelSoundEventPacket::new;
        factories[25] = LevelEventPacket::new;
        factories[26] = BlockEventPacket::new;
        factories[27] = EntityEventPacket::new;
        factories[28] = MobEffectPacket::new;
        factories[29] = UpdateAttributesPacket::new;
        factories[30] = InventoryTransactionPacket::new;
        factories[31] = MobEquipmentPacket::new;
        factories[32] = MobArmorEquipmentPacket::new;
        factories[33] = InteractPacket::new;
        factories[34] = BlockPickRequestPacket::new;
        factories[35] = EntityPickRequestPacket::new;
        factories[36] = PlayerActionPacket::new;
        factories[37] = EntityFallPacket::new;
        factories[38] = HurtArmorPacket::new;
        factories[39] = SetEntityDataPacket::new;
        factories[40] = SetEntityMotionPacket::new;
        factories[41] = SetEntityLinkPacket::new;
        factories[42] = SetHealthPacket::new;
        factories[43] = SetSpawnPositionPacket::new;
        factories[44] = AnimatePacket::new;
        factories[45] = RespawnPacket::new;
        factories[46] = ContainerOpenPacket::new;
        factories[47] = ContainerClosePacket::new;
        factories[48] = PlayerHotbarPacket::new;
        factories[49] = InventoryContentPacket::new;
        factories[50] = InventorySlotPacket::new;
        factories[51] = ContainerSetDataPacket::new;
        factories[52] = CraftingDataPacket::new;
        factories[53] = CraftingEventPacket::new;
        factories[54] = GuiDataPickItemPacket::new;
        factories[55] = AdventureSettingsPacket::new;
        factories[56] = BlockEntityDataPacket::new;
        factories[57] = PlayerInputPacket::new;
        factories[58] = FullChunkDataPacket::new;
        factories[59] = SetCommandsEnabledPacket::new;
        factories[60] = SetDifficultyPacket::new;
        factories[61] = ChangeDimensionPacket::new;
        factories[62] = SetPlayerGameTypePacket::new;
        factories[63] = PlayerListPacket::new;
        factories[64] = SimpleEventPacket::new;
        factories[65] = EventPacket::new;
        factories[66] = SpawnExperienceOrbPacket::new;
        factories[67] = ClientboundMapItemDataPacket::new;
        factories[68] = MapInfoRequestPacket::new;
        factories[69] = RequestChunkRadiusPacket::new;
        factories[70] = ChunkRadiusUpdatePacket::new;
        factories[71] = ItemFrameDropItemPacket::new;
        factories[72] = GameRulesChangedPacket::new;
        factories[73] = CameraPacket::new;
        factories[74] = BossEventPacket::new;
        factories[75] = ShowCreditsPacket::new;
        factories[76] = AvailableCommandsPacket::new;
        factories[77] = CommandRequestPacket::new;
        factories[78] = CommandBlockUpdatePacket::new;
        factories[79] = CommandOutputPacket::new;
        factories[80] = UpdateTradePacket::new;
        factories[81] = UpdateEquipPacket::new;
        factories[82] = ResourcePackDataInfoPacket::new;
        factories[83] = ResourcePackChunkDataPacket::new;
        factories[84] = ResourcePackChunkRequestPacket::new;
        factories[85] = TransferPacket::new;
        factories[86] = PlaySoundPacket::new;
        factories[87] = StopSoundPacket::new;
        factories[88] = SetTitlePacket::new;
        factories[89] = AddBehaviorTreePacket::new;
        factories[90] = StructureBlockUpdatePacket::new;
        factories[91] = ShowStoreOfferPacket::new;
        factories[92] = PurchaseReceiptPacket::new;
        factories[93] = PlayerSkinPacket::new;
        factories[94] = SubClientLoginPacket::new;
        factories[95] = InitiateWebSocketConnectionPacket::new;
        factories[96] = SetLastHurtByPacket::new;
        factories[97] = BookEditPacket::new;
        factories[98] = NpcRequestPacket::new;
        factories[99] = PhotoTransferPacket::new;
        factories[100] = ModalFormRequestPacket::new;
        factories[101] = ModalFormResponsePacket::new;
        factories[102] = ServerSettingsRequestPacket::new;
        factories[103] = ServerSettingsResponsePacket::new;
        factories[104] = ShowProfilePacket::new;
        factories[105] = SetDefaultGameTypePacket::new;
        factories[106] = RemoveObjectivePacket::new;
        factories[107] = SetDisplayObjectivePacket::new;
        factories[108] = SetScorePacket::new;
        factories[109] = LabTablePacket::new;
        factories[110] = UpdateBlockSyncedPacket::new;

        for (int i = 0; i < factories.length; i++) {
            if (factories[i] == null) continue;
            idMapping.put(factories[i].newInstance().getClass(), (byte) i);
        }
    }

    @Override
    public MinecraftPacket tryDecode(ByteBuf byteBuf) {
        short id = byteBuf.readUnsignedByte();
        byteBuf.skipBytes(2); // This is for split-screen.
        MinecraftPacket packet = factories[id].newInstance();
        packet.decode(byteBuf);
        if (log.isDebugEnabled() && byteBuf.isReadable()) {
            log.debug(packet.getClass().getSimpleName() + " still has " + byteBuf.readableBytes() + " bytes to read!");
        }
        return packet;
    }

    @Override
    public ByteBuf tryEncode(MinecraftPacket packet) {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        byteBuf.writeByte(getId(packet));
        byteBuf.writeBytes(new byte[]{0, 0});
        packet.encode(byteBuf);
        return byteBuf;
    }

    public static MinecraftPacket decode(ByteBuf byteBuf) {
        return INSTANCE.tryDecode(byteBuf);
    }

    public static ByteBuf encode(MinecraftPacket packet) {
        return INSTANCE.tryEncode(packet);
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

    public static byte getId(MinecraftPacket packet) {
        Class<? extends MinecraftPacket> clazz = packet.getClass();
        byte id = INSTANCE.idMapping.get(clazz);
        if (id == -1) {
            throw new IllegalArgumentException("Packet ID for " + clazz.getName() + " does not exist.");
        }
        return id;
    }
}
