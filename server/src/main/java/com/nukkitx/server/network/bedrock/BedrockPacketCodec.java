package com.nukkitx.server.network.bedrock;

import com.google.common.base.Preconditions;
import com.nukkitx.network.PacketCodec;
import com.nukkitx.network.PacketFactory;
import com.nukkitx.server.network.bedrock.packet.*;
import com.nukkitx.server.network.util.VarInts;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

@Log4j2
@Immutable
@RequiredArgsConstructor
public class BedrockPacketCodec implements PacketCodec<BedrockPacket> {
    public static final int BROADCAST_PROTOCOL_VERSION = 310;
    public static final BedrockPacketCodec DEFAULT;

    private final int[] compatibleVersions;
    private final PacketFactory<BedrockPacket>[] factories;
    private final TObjectIntMap<Class<? extends BedrockPacket>> idMapping;

    static {
        DEFAULT = builder()
                .addCompatibleVersion(BROADCAST_PROTOCOL_VERSION)
                .registerPacket(LoginPacket::new, 1)
                .registerPacket(PlayStatusPacket::new, 2)
                .registerPacket(ServerToClientHandshakePacket::new, 3)
                .registerPacket(ClientToServerHandshakePacket::new, 4)
                .registerPacket(DisconnectPacket::new, 5)
                .registerPacket(ResourcePacksInfoPacket::new, 6)
                .registerPacket(ResourcePackStackPacket::new, 7)
                .registerPacket(ResourcePackClientResponsePacket::new, 8)
                .registerPacket(TextPacket::new, 9)
                .registerPacket(SetTimePacket::new, 10)
                .registerPacket(StartGamePacket::new, 11)
                .registerPacket(AddPlayerPacket::new, 12)
                .registerPacket(AddEntityPacket::new, 13)
                .registerPacket(RemoveEntityPacket::new, 14)
                .registerPacket(AddItemEntityPacket::new, 15)
                .registerPacket(AddHangingEntityPacket::new, 16)
                .registerPacket(TakeItemEntityPacket::new, 17)
                .registerPacket(MoveEntityAbsolutePacket::new, 18)
                .registerPacket(MovePlayerPacket::new, 19)
                .registerPacket(RiderJumpPacket::new, 20)
                .registerPacket(UpdateBlockPacket::new, 21)
                .registerPacket(AddPaintingPacket::new, 22)
                .registerPacket(ExplodePacket::new, 23)
                .registerPacket(LevelSoundEventPacket::new, 24)
                .registerPacket(LevelEventPacket::new, 25)
                .registerPacket(BlockEventPacket::new, 26)
                .registerPacket(EntityEventPacket::new, 27)
                .registerPacket(MobEffectPacket::new, 28)
                .registerPacket(UpdateAttributesPacket::new, 29)
                .registerPacket(InventoryTransactionPacket::new, 30)
                .registerPacket(MobEquipmentPacket::new, 31)
                .registerPacket(MobArmorEquipmentPacket::new, 32)
                .registerPacket(InteractPacket::new, 33)
                .registerPacket(BlockPickRequestPacket::new, 34)
                .registerPacket(EntityPickRequestPacket::new, 35)
                .registerPacket(PlayerActionPacket::new, 36)
                .registerPacket(EntityFallPacket::new, 37)
                .registerPacket(HurtArmorPacket::new, 38)
                .registerPacket(SetEntityDataPacket::new, 39)
                .registerPacket(SetEntityMotionPacket::new, 40)
                .registerPacket(SetEntityLinkPacket::new, 41)
                .registerPacket(SetHealthPacket::new, 42)
                .registerPacket(SetSpawnPositionPacket::new, 43)
                .registerPacket(AnimatePacket::new, 44)
                .registerPacket(RespawnPacket::new, 45)
                .registerPacket(ContainerOpenPacket::new, 46)
                .registerPacket(ContainerClosePacket::new, 47)
                .registerPacket(PlayerHotbarPacket::new, 48)
                .registerPacket(InventoryContentPacket::new, 49)
                .registerPacket(InventorySlotPacket::new, 50)
                .registerPacket(ContainerSetDataPacket::new, 51)
                .registerPacket(CraftingDataPacket::new, 52)
                .registerPacket(CraftingEventPacket::new, 53)
                .registerPacket(GuiDataPickItemPacket::new, 54)
                .registerPacket(AdventureSettingsPacket::new, 55)
                .registerPacket(BlockEntityDataPacket::new, 56)
                .registerPacket(PlayerInputPacket::new, 57)
                .registerPacket(FullChunkDataPacket::new, 58)
                .registerPacket(SetCommandsEnabledPacket::new, 59)
                .registerPacket(SetDifficultyPacket::new, 60)
                .registerPacket(ChangeDimensionPacket::new, 61)
                .registerPacket(SetPlayerGameTypePacket::new, 62)
                .registerPacket(PlayerListPacket::new, 63)
                .registerPacket(SimpleEventPacket::new, 64)
                .registerPacket(EventPacket::new, 65)
                .registerPacket(SpawnExperienceOrbPacket::new, 66)
                .registerPacket(ClientboundMapItemDataPacket::new, 67)
                .registerPacket(MapInfoRequestPacket::new, 68)
                .registerPacket(RequestChunkRadiusPacket::new, 69)
                .registerPacket(ChunkRadiusUpdatePacket::new, 70)
                .registerPacket(ItemFrameDropItemPacket::new, 71)
                .registerPacket(GameRulesChangedPacket::new, 72)
                .registerPacket(CameraPacket::new, 73)
                .registerPacket(BossEventPacket::new, 74)
                .registerPacket(ShowCreditsPacket::new, 75)
                .registerPacket(AvailableCommandsPacket::new, 76)
                .registerPacket(CommandRequestPacket::new, 77)
                .registerPacket(CommandBlockUpdatePacket::new, 78)
                .registerPacket(CommandOutputPacket::new, 79)
                .registerPacket(UpdateTradePacket::new, 80)
                .registerPacket(UpdateEquipPacket::new, 81)
                .registerPacket(ResourcePackDataInfoPacket::new, 82)
                .registerPacket(ResourcePackChunkDataPacket::new, 83)
                .registerPacket(ResourcePackChunkRequestPacket::new, 84)
                .registerPacket(TransferPacket::new, 85)
                .registerPacket(PlaySoundPacket::new, 86)
                .registerPacket(StopSoundPacket::new, 87)
                .registerPacket(SetTitlePacket::new, 88)
                .registerPacket(AddBehaviorTreePacket::new, 89)
                .registerPacket(StructureBlockUpdatePacket::new, 90)
                .registerPacket(ShowStoreOfferPacket::new, 91)
                .registerPacket(PurchaseReceiptPacket::new, 92)
                .registerPacket(PlayerSkinPacket::new, 93)
                .registerPacket(SubClientLoginPacket::new, 94)
                .registerPacket(InitiateWebSocketConnectionPacket::new, 95)
                .registerPacket(SetLastHurtByPacket::new, 96)
                .registerPacket(BookEditPacket::new, 97)
                .registerPacket(NpcRequestPacket::new, 98)
                .registerPacket(PhotoTransferPacket::new, 99)
                .registerPacket(ModalFormRequestPacket::new, 100)
                .registerPacket(ModalFormResponsePacket::new, 101)
                .registerPacket(ServerSettingsRequestPacket::new, 102)
                .registerPacket(ServerSettingsResponsePacket::new, 103)
                .registerPacket(ShowProfilePacket::new, 104)
                .registerPacket(SetDefaultGameTypePacket::new, 105)
                .registerPacket(RemoveObjectivePacket::new, 106)
                .registerPacket(SetDisplayObjectivePacket::new, 107)
                .registerPacket(SetScorePacket::new, 108)
                .registerPacket(LabTablePacket::new, 109)
                .registerPacket(UpdateBlockSyncedPacket::new, 110)
                .registerPacket(MoveEntityDeltaPacket::new, 111)
                .registerPacket(SetScoreboardIdentityPacket::new, 112)
                .registerPacket(SetLocalPlayerAsInitializedPacket::new, 113)
                .registerPacket(UpdateSoftEnumPacket::new, 114)
                .registerPacket(NetworkStackLatencyPacket::new, 115)
                .registerPacket(ScriptCustomEventPacket::new, 117)
                .registerPacket(SpawnParticleEffectPacket::new, 118)
                .registerPacket(AvailableEntityIdentifiersPacket::new, 119)
                .registerPacket(LevelSoundEvent2Packet::new, 120)
                .registerPacket(NetworkChunkPublisherUpdatePacket::new, 121)
                .build();

    }

    public boolean isCompatibleVersion(int protocolVersion) {
        return Arrays.binarySearch(compatibleVersions, protocolVersion) >= 0;
    }

    @Override
    public BedrockPacket tryDecode(ByteBuf buf) {
        int id = VarInts.readUnsignedInt(buf);
        BedrockPacket packet = factories[id].newInstance();
        packet.decode(buf);
        if (log.isDebugEnabled() && buf.isReadable()) {
            log.debug(packet.getClass().getSimpleName() + " still has " + buf.readableBytes() + " bytes to read!");
        }
        return packet;
    }

    @Override
    public ByteBuf tryEncode(BedrockPacket packet) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        VarInts.writeUnsignedInt(buf, getId(packet));
        packet.encode(buf);
        return buf;
    }

    @Override
    public int getId(BedrockPacket packet) {
        Class<? extends BedrockPacket> clazz = packet.getClass();
        int id = idMapping.get(clazz);
        if (id == -1) {
            throw new IllegalArgumentException("Packet ID for " + clazz.getName() + " does not exist.");
        }
        return id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TIntObjectMap<PacketFactory<BedrockPacket>> packets = new TIntObjectHashMap<>();
        private final TObjectIntMap<Class<? extends BedrockPacket>> ids = new TObjectIntHashMap<>(64, 0.75f, -1);
        private final TIntSet compatibleVersions = new TIntHashSet();

        public Builder registerPacket(PacketFactory<BedrockPacket> packet, @Nonnegative int id) {
            Preconditions.checkArgument(id >= 0, "id cannot be negative");
            if (packets.containsKey(id)) {
                throw new IllegalArgumentException("Packet id already registered");
            }
            Class<? extends BedrockPacket> packetClass = packet.getPacketClass();
            if (ids.containsKey(packetClass)) {
                throw new IllegalArgumentException("Packet class already registered");
            }

            packets.put(id, packet);
            ids.put(packetClass, id);
            return this;
        }

        public Builder addCompatibleVersion(@Nonnegative int protocolVersion) {
            Preconditions.checkArgument(protocolVersion >= 0, "protocolVersion cannot be negative");
            compatibleVersions.add(protocolVersion);
            return this;
        }

        public BedrockPacketCodec build() {
            Preconditions.checkArgument(!compatibleVersions.isEmpty(), "Must have at least one compatible version");
            int largestId = -1;
            for (int id : packets.keys()) {
                if (id > largestId) {
                    largestId = id;
                }
            }
            Preconditions.checkArgument(largestId > -1, "Must have at least one packet registered");
            PacketFactory<BedrockPacket>[] packets = new PacketFactory[largestId + 1];

            TIntObjectIterator<PacketFactory<BedrockPacket>> iter = this.packets.iterator();

            while (iter.hasNext()) {
                iter.advance();
                packets[iter.key()] = iter.value();
            }
            return new BedrockPacketCodec(compatibleVersions.toArray(), packets, ids);
        }
    }
}
