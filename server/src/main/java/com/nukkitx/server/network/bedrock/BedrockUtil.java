package com.nukkitx.server.network.bedrock;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemInstanceBuilder;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.level.GameRules;
import com.nukkitx.api.level.LevelSettings;
import com.nukkitx.api.level.data.GameRule;
import com.nukkitx.api.resourcepack.ResourcePack;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.api.util.Skin;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.Tag;
import com.nukkitx.server.entity.Attribute;
import com.nukkitx.server.entity.EntityLink;
import com.nukkitx.server.item.ItemUtil;
import com.nukkitx.server.item.NukkitItemInstance;
import com.nukkitx.server.item.NukkitItemInstanceBuilder;
import com.nukkitx.server.level.NukkitGameRules;
import com.nukkitx.server.metadata.MetadataSerializers;
import com.nukkitx.server.network.bedrock.data.CommandOriginData;
import com.nukkitx.server.network.bedrock.data.ScoreInfo;
import com.nukkitx.server.network.bedrock.data.StructureEditorData;
import com.nukkitx.server.network.bedrock.data.StructureSettings;
import com.nukkitx.server.network.util.LittleEndianByteBufInputStream;
import com.nukkitx.server.network.util.LittleEndianByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.nukkitx.server.network.util.VarInts.*;

@UtilityClass
public final class BedrockUtil {

    public static String readString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int length = readUnsignedInt(buffer);
        byte[] readBytes = new byte[length];
        buffer.readBytes(readBytes);
        return new String(readBytes, StandardCharsets.UTF_8);
    }

    public static void writeString(ByteBuf buffer, String string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        byte[] bytes = string.getBytes(CharsetUtil.UTF_8);
        writeUnsignedInt(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }

    public static AsciiString readLEAsciiString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        int length = buffer.readIntLE();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new AsciiString(bytes);
    }

    public static void writeLEAsciiString(ByteBuf buffer, AsciiString string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        buffer.writeIntLE(string.length());
        buffer.writeBytes(string.toByteArray());
    }

    public static AsciiString readVarIntAsciiString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        int length = readUnsignedInt(buffer);
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new AsciiString(bytes);
    }

    public static void writeVarIntAsciiString(ByteBuf buffer, AsciiString string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        writeUnsignedInt(buffer, string.length());
        buffer.writeBytes(string.toByteArray());
    }

    public static long readUniqueEntityId(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        return readLong(buffer);
    }

    public static void writeUniqueEntityId(ByteBuf buffer, long uniqueEntityId) {
        Preconditions.checkNotNull(buffer, "buffer");
        writeLong(buffer, uniqueEntityId);
    }

    public static long readRuntimeEntityId(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        return readUnsignedLong(buffer);
    }

    public static void writeRuntimeEntityId(ByteBuf buffer, long runtimeEntityId) {
        Preconditions.checkNotNull(buffer, "buffer");
        writeUnsignedLong(buffer, runtimeEntityId);
    }

    public static UUID readUuid(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        return new UUID(buffer.readLong(), buffer.readLong());
    }

    public static void writeUuid(ByteBuf buffer, UUID uuid) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(uuid , "uuid");
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
    }

    public static Vector3f readVector3f(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        float x = buffer.readFloatLE();
        float y = buffer.readFloatLE();
        float z = buffer.readFloatLE();
        return new Vector3f(x, y, z);
    }

    public static void writeVector3f(ByteBuf buffer, Vector3f vector3f) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(vector3f, "vector3f");
        buffer.writeFloatLE(vector3f.getX());
        buffer.writeFloatLE(vector3f.getY());
        buffer.writeFloatLE(vector3f.getZ());
    }

    public static Vector2f readVector2f(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        float x = buffer.readFloatLE();
        float y = buffer.readFloatLE();
        return new Vector2f(x, y);
    }

    public static void writeVector2f(ByteBuf buffer, Vector2f vector2f) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(vector2f, "vector2f");
        buffer.writeFloatLE(vector2f.getX());
        buffer.writeFloatLE(vector2f.getY());
    }

    public static List<Attribute> readEntityAttributes(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        List<Attribute> attributeList = new ArrayList<>();
        int size = readUnsignedInt(buffer);

        for (int i = 0; i < size; i++) {
            String name = readString(buffer);
            float min = buffer.readFloatLE();
            float max = buffer.readFloatLE();
            float val = buffer.readFloatLE();

            attributeList.add(new Attribute(name, val, min, max));
        }
        return attributeList;
    }

    public static void writeEntityAttributes(ByteBuf buffer, Collection<Attribute> attributeList) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(attributeList, "attributeList");
        writeUnsignedInt(buffer, attributeList.size());
        for (Attribute attribute : attributeList) {
            writeString(buffer, attribute.getName());
            buffer.writeFloatLE(attribute.getMinimumValue());
            buffer.writeFloatLE(attribute.getMaximumValue());
            buffer.writeFloatLE(attribute.getValue());
        }
    }

    public static List<Attribute> readPlayerAttributes(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        List<Attribute> attributeList = new ArrayList<>();
        int size = readUnsignedInt(buffer);

        for (int i = 0; i < size; i++) {
            float min = buffer.readFloatLE();
            float max = buffer.readFloatLE();
            float val = buffer.readFloatLE();
            float def = buffer.readFloatLE();
            String name = readString(buffer);

            attributeList.add(new Attribute(name, val, min, max, def));
        }
        return attributeList;
    }

    public static void writePlayerAttributes(ByteBuf buffer, Collection<Attribute> attributeList) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(attributeList, "attributeList");
        writeUnsignedInt(buffer, attributeList.size());
        for (Attribute attribute : attributeList) {
            buffer.writeFloatLE(attribute.getMinimumValue());
            buffer.writeFloatLE(attribute.getMaximumValue());
            buffer.writeFloatLE(attribute.getValue());
            buffer.writeFloatLE(attribute.getDefaultValue());
            writeString(buffer, attribute.getName());
        }
    }

    public static Collection<EntityLink> readEntityLinks(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        List<EntityLink> entityLinkList = new ArrayList<>();
        int size = readUnsignedInt(buffer);

        for (int i = 0; i < size; i++) {
            long from = readUniqueEntityId(buffer);
            long to = readUniqueEntityId(buffer);
            byte type = buffer.readByte();
            boolean bool = buffer.readBoolean();

            entityLinkList.add(new EntityLink(from, to, type, bool));
        }
        return entityLinkList;
    }

    public static void writeEntityLinks(ByteBuf buffer, Collection<EntityLink> entityLinkList) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(entityLinkList, "entityLinkList");
        writeUnsignedInt(buffer, entityLinkList.size());
        for (EntityLink entityLink: entityLinkList) {
            writeUniqueEntityId(buffer, entityLink.getFromUniqueEntityId());
            writeUniqueEntityId(buffer, entityLink.getToUniqueEntityId());
            buffer.writeByte(entityLink.getType());
            buffer.writeBoolean(entityLink.isUnknown());
        }
    }

    public static Vector3i readVector3i(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int x = readInt(buffer);
        int y = readUnsignedInt(buffer);
        int z = readInt(buffer);

        return new Vector3i(x, y, z);
    }

    public static void writeVector3i(ByteBuf buffer, Vector3i blockPosition) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(blockPosition, "blockPosition");
        writeInt(buffer, blockPosition.getX());
        writeUnsignedInt(buffer, blockPosition.getY());
        writeInt(buffer, blockPosition.getZ());
    }

    public static Vector3i readSignedBlockPosition(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int x = readInt(buffer);
        int y = readInt(buffer);
        int z = readInt(buffer);

        return new Vector3i(x, y, z);
    }

    public static void writeSignedBlockPosition(ByteBuf buffer, Vector3i blockPosition) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(blockPosition, "blockPosition");
        writeInt(buffer, blockPosition.getX());
        writeInt(buffer, blockPosition.getY());
        writeInt(buffer, blockPosition.getZ());
    }

    public static ItemInstance readItemInstance(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int id = readInt(buffer);
        if (id <= 0) {
            return new NukkitItemInstance(BlockTypes.AIR, 1, null);
        }

        int aux = readInt(buffer);
        short damage = (short) (aux >> 8);
        if (damage == Short.MAX_VALUE) damage = -1;
        int count = aux & 0xff;
        short nbtSize = buffer.readShortLE();

        ItemType type = ItemTypes.byId(id);

        ItemInstanceBuilder builder = new NukkitItemInstanceBuilder()
                .itemType(type)
                .itemData(MetadataSerializers.deserializeMetadata(type, damage))
                .amount(count);

        if (nbtSize > 0) {
            try (NBTInputStream reader = new NBTInputStream(new LittleEndianByteBufInputStream(buffer.readSlice(nbtSize)))) {
                Tag<?> tag = reader.readTag();
                if (tag instanceof CompoundTag) {
                    ItemUtil.applyItemData(builder, ((CompoundTag) tag).getValue());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }

        // TODO: Use these instead of just ignoring.
        int canPlaceCount = readInt(buffer);
        for (int i = 0; i < canPlaceCount; i++) {
            readString(buffer);
        }

        int canBreakCount = readInt(buffer);
        for (int i = 0; i < canBreakCount; i++) {
            readString(buffer);
        }
        return builder.build();
    }

    public static void writeItemInstance(ByteBuf buffer, ItemInstance itemInstance) {
        Preconditions.checkNotNull(buffer, "buffer");
        if (itemInstance == null || itemInstance.getItemType() == BlockTypes.AIR) {
            buffer.writeByte(0); // Save having to send all other data.
            return;
        }

        writeInt(buffer, itemInstance.getItemType().getId());
        short metadataValue = MetadataSerializers.serializeMetadata(itemInstance);
        if (metadataValue == -1) metadataValue = Short.MAX_VALUE;
        writeInt(buffer, (metadataValue << 8) | itemInstance.getAmount());

        // Remember this position, since we'll be writing the true NBT size here later:
        int sizeIndex = buffer.writerIndex();
        buffer.writeShort(0);
        int afterSizeIndex = buffer.writerIndex();

        if (itemInstance instanceof NukkitItemInstance) {
            try (NBTOutputStream stream = new NBTOutputStream(new LittleEndianByteBufOutputStream(buffer))) {
                stream.write(((NukkitItemInstance) itemInstance).toSpecificNBT());
            } catch (IOException e) {
                // This shouldn't happen (as this is backed by a Netty ByteBuf), but okay...
                throw new IllegalStateException("Unable to save NBT data", e);
            }

            // Set to the written NBT size
            buffer.setShortLE(sizeIndex, buffer.writerIndex() - afterSizeIndex);
        }

        // TODO: Use these instead of just ignoring.
        writeInt(buffer, 0); // can place
        writeInt(buffer, 0); // can break
    }

    public static Rotation readRotation(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        float pitch = buffer.readFloatLE();
        float yaw = buffer.readFloatLE();
        float headYaw = buffer.readFloatLE();
        return new Rotation(pitch, yaw, headYaw);
    }

    public static void writeRotation(ByteBuf buffer, Rotation rotation) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(rotation, "rotation");
        buffer.writeFloatLE(rotation.getPitch());
        buffer.writeFloatLE(rotation.getYaw());
        buffer.writeFloatLE(rotation.getHeadYaw());
    }

    public static Rotation readByteRotation(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        float pitch = rotationByteToAngle(buffer.readByte());
        float headYaw = rotationByteToAngle(buffer.readByte());
        float yaw = rotationByteToAngle(buffer.readByte());
        return new Rotation(pitch, yaw, headYaw);
    }

    public static void writeByteRotation(ByteBuf buffer, Rotation rotation) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(rotation, "rotation");
        buffer.writeByte(rotationAngleToByte(rotation.getPitch()));
        buffer.writeByte(rotationAngleToByte(rotation.getHeadYaw()));
        buffer.writeByte(rotationAngleToByte(rotation.getYaw()));
    }

    private static byte rotationAngleToByte(float angle) {
        return (byte) Math.ceil(angle / 360 * 255);
    }

    private static float rotationByteToAngle(byte angle) {
        return angle / 255f * 360f;
    }

    public static CommandOriginData readCommandOriginData(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        CommandOriginData.Origin origin = CommandOriginData.Origin.values()[readUnsignedInt(buffer)];
        UUID uuid = readUuid(buffer);
        String requestId = readString(buffer);
        long varLong = -1;
        if (origin == CommandOriginData.Origin.DEV_CONSOLE || origin == CommandOriginData.Origin.TEST) {
            varLong = readLong(buffer);
        }
        return new CommandOriginData(origin, uuid, requestId, varLong);
    }

    public static void writeCommandOriginData(ByteBuf buffer, CommandOriginData commandOriginData) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(commandOriginData, "commandOriginData");
        writeUnsignedInt(buffer, commandOriginData.getOrigin().ordinal());
        writeUuid(buffer, commandOriginData.getUuid());
        writeString(buffer, commandOriginData.getRequestId());
        commandOriginData.getUnknown0().ifPresent(unknown0 -> writeLong(buffer, unknown0));
    }

    public static void writeGameRules(ByteBuf buffer, GameRules gameRules) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(gameRules, "gameRules");
        Preconditions.checkArgument(gameRules instanceof NukkitGameRules, "gameRules are not of class NukkitGameRules");

        Map<GameRule, NukkitGameRules.Value> rules = ((NukkitGameRules) gameRules).getGameRules();
        writeUnsignedInt(buffer, rules.size());
        for (Map.Entry<GameRule, NukkitGameRules.Value> entry : rules.entrySet()) {
            writeString(buffer, entry.getKey().getName().toLowerCase());
            entry.getValue().write(buffer);
        }
    }

    public static void writeSkin(ByteBuf buffer, Skin skin) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(skin, "skin");
        byte[] skinData = skin.getSkinData();
        byte[] capeData = skin.getCapeData();
        byte[] geometryData = skin.getGeometryData();
        writeString(buffer, skin.getSkinId());
        writeUnsignedInt(buffer, skinData.length);
        buffer.writeBytes(skinData);
        writeUnsignedInt(buffer, capeData.length);
        buffer.writeBytes(capeData);
        writeString(buffer, skin.getGeometryName());
        writeUnsignedInt(buffer, geometryData.length);
        buffer.writeBytes(geometryData);
    }

    public static void writePackInstanceEntries(ByteBuf buffer, Collection<ResourcePack> packInstanceEntries) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(packInstanceEntries, "packInstanceEntries");
        buffer.writeShortLE(packInstanceEntries.size());
        for (ResourcePack packInstanceEntry: packInstanceEntries) {
            writeString(buffer, packInstanceEntry.getId().toString());
            writeString(buffer, packInstanceEntry.getVersion().toString());
            writeString(buffer, packInstanceEntry.getName());
        }
    }

    public static void writePackInfoEntries(ByteBuf buffer, Collection<ResourcePack> packInfoEntries) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(packInfoEntries, "packInfoEntries");
        buffer.writeShortLE(packInfoEntries.size());
        for (ResourcePack packInfoEntry: packInfoEntries) {
            writeString(buffer, packInfoEntry.getId().toString());
            writeString(buffer, packInfoEntry.getVersion().toString());
            buffer.writeLongLE(packInfoEntry.getPackSize());
            writeString(buffer, packInfoEntry.getName());
            writeString(buffer, packInfoEntry.getDescription());
        }
    }

    public static void writeLevelSettings(ByteBuf buffer, LevelSettings levelSettings) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(levelSettings, "levelSettings");
        writeInt(buffer, levelSettings.getSeed());
        writeInt(buffer, levelSettings.getDimension().ordinal());
        writeInt(buffer, levelSettings.getGenerator().ordinal());
        writeInt(buffer, levelSettings.getGameMode().ordinal());
        writeInt(buffer, levelSettings.getDifficulty().ordinal());
        writeVector3i(buffer, levelSettings.getDefaultSpawn().toInt());
        buffer.writeBoolean(levelSettings.isAchievementsDisabled());
        writeInt(buffer, levelSettings.getTime());
        buffer.writeBoolean(levelSettings.isEduWorld());
        buffer.writeBoolean(levelSettings.isEduFeaturesEnabled());
        buffer.writeFloatLE(levelSettings.getRainLevel());
        buffer.writeFloatLE(levelSettings.getLightningLevel());
        buffer.writeBoolean(levelSettings.isMultiplayerGame());
        buffer.writeBoolean(levelSettings.isBroadcastingToLan());
        buffer.writeBoolean(levelSettings.isBroadcastingToXBL());
        buffer.writeBoolean(levelSettings.isCommandsEnabled());
        buffer.writeBoolean(levelSettings.isTexturepacksRequired());
        writeGameRules(buffer, levelSettings.getGameRules());
        buffer.writeBoolean(levelSettings.isBonusChestEnabled());
        buffer.writeBoolean(levelSettings.isStartingWithMap());
        buffer.writeBoolean(levelSettings.isTrustingPlayers());
        writeInt(buffer, levelSettings.getDefaultPlayerPermission().ordinal());
        writeInt(buffer, levelSettings.getXBLBroadcastMode());
        buffer.writeIntLE(levelSettings.getServerChunkTickRange());
        buffer.writeBoolean(levelSettings.isBroadcastingToPlatform());
        writeUnsignedInt(buffer, levelSettings.getPlatformBroadcastMode());
        buffer.writeBoolean(levelSettings.isIntentOnXBLBroadcast());
        buffer.writeBoolean(levelSettings.isBehaviorPackLocked());
        buffer.writeBoolean(levelSettings.isResourcePackLocked());
        buffer.writeBoolean(levelSettings.isFromLockedWorldTemplate());
    }

    public static void writeStructureEditorData(ByteBuf buffer, StructureEditorData structureEditorData) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(structureEditorData, "structureEditorData");
        writeString(buffer, structureEditorData.getUnknown0());
        writeString(buffer, structureEditorData.getMetadata());
        writeVector3i(buffer, structureEditorData.getStructureOffset());
        writeVector3i(buffer, structureEditorData.getStructureSize());
        buffer.writeBoolean(structureEditorData.isIncludingEntities());
        buffer.writeBoolean(structureEditorData.isIgnoringBlocks());
        buffer.writeBoolean(structureEditorData.isIncludingPlayers());
        buffer.writeBoolean(structureEditorData.isShowingAir());
        writeStructureSettings(buffer, structureEditorData.getStructureSettings());
    }

    public static void writeStructureSettings(ByteBuf buffer, StructureSettings structureSettings) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(structureSettings, "structureSettings");
        buffer.writeBoolean(structureSettings.isIntegrity());
        writeUnsignedInt(buffer, structureSettings.getSeed());
        writeUnsignedInt(buffer, structureSettings.getMirror());
        writeUnsignedInt(buffer, structureSettings.getRotation());
        buffer.writeBoolean(structureSettings.isIgnoreEntities());
        buffer.writeBoolean(structureSettings.isIgnoreStructureBlocks());
        writeIntBoundingBox(buffer, structureSettings.getBoundingBox());
    }

    public static void writeIntBoundingBox(ByteBuf buffer, BoundingBox bb) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(bb, "bb");
        writeVector3i(buffer, bb.getMax().toInt());
        writeVector3i(buffer, bb.getMin().toInt());
    }

    public static void writeScoreInfo(ByteBuf buffer, Collection<ScoreInfo> infos) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(infos, "infos");
        writeUnsignedInt(buffer, infos.size());
        for (ScoreInfo info : infos) {
            writeUuid(buffer, info.getPlayerUuid());
            writeString(buffer, info.getObjectiveId());
            buffer.writeIntLE(info.getScore());
        }
    }
}
