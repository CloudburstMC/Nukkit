package cn.nukkit.server.network.minecraft;

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.item.ItemStackBuilder;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.item.ItemTypes;
import cn.nukkit.api.level.LevelSettings;
import cn.nukkit.api.level.gamerule.BooleanGameRule;
import cn.nukkit.api.level.gamerule.FloatGameRule;
import cn.nukkit.api.level.gamerule.GameRule;
import cn.nukkit.api.level.gamerule.IntGameRule;
import cn.nukkit.api.resourcepack.ResourcePack;
import cn.nukkit.api.util.BoundingBox;
import cn.nukkit.api.util.Rotation;
import cn.nukkit.api.util.Skin;
import cn.nukkit.server.entity.EntityAttribute;
import cn.nukkit.server.entity.EntityLink;
import cn.nukkit.server.entity.PlayerAttribute;
import cn.nukkit.server.item.NukkitItemStack;
import cn.nukkit.server.item.NukkitItemStackBuilder;
import cn.nukkit.server.metadata.serializer.MetadataSerializer;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.stream.NBTReader;
import cn.nukkit.server.nbt.stream.NBTWriter;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.Tag;
import cn.nukkit.server.network.minecraft.data.CommandOriginData;
import cn.nukkit.server.network.minecraft.data.StructureEditorData;
import cn.nukkit.server.network.minecraft.data.StructureSettings;
import cn.nukkit.server.network.util.LittleEndianByteBufInputStream;
import cn.nukkit.server.network.util.LittleEndianByteBufOutputStream;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.server.nbt.util.VarInt.*;

@UtilityClass
public final class MinecraftUtil {

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
        return readSignedLong(buffer);
    }

    public static void writeUniqueEntityId(ByteBuf buffer, long uniqueEntityId) {
        Preconditions.checkNotNull(buffer, "buffer");
        writeSignedLong(buffer, uniqueEntityId);
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

    public static List<EntityAttribute> readEntityAttributes(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        List<EntityAttribute> attributeList = new ArrayList<>();
        int size = readUnsignedInt(buffer);

        for (int i = 0; i < size; i++) {
            String name = readString(buffer);
            float min = buffer.readFloatLE();
            float max = buffer.readFloatLE();
            float val = buffer.readFloatLE();

            attributeList.add(new EntityAttribute(name, min, max, val));
        }
        return attributeList;
    }

    public static void writeEntityAttributes(ByteBuf buffer, Collection<EntityAttribute> attributeList) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(attributeList, "attributeList");
        writeUnsignedInt(buffer, attributeList.size());
        for (EntityAttribute attribute : attributeList) {
            writeString(buffer, attribute.getName());
            buffer.writeFloatLE(attribute.getMinimumValue());
            buffer.writeFloatLE(attribute.getMaximumValue());
            buffer.writeFloatLE(attribute.getValue());
        }
    }

    public static List<PlayerAttribute> readPlayerAttributes(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        List<PlayerAttribute> attributeList = new ArrayList<>();
        int size = readUnsignedInt(buffer);

        for (int i = 0; i < size; i++) {
            String name = readString(buffer);
            float min = buffer.readFloatLE();
            float max = buffer.readFloatLE();
            float val = buffer.readFloatLE();
            float def = buffer.readFloatLE();

            attributeList.add(new PlayerAttribute(name, min, max, val, def));
        }
        return attributeList;
    }

    public static void writePlayerAttributes(ByteBuf buffer, Collection<PlayerAttribute> attributeList) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(attributeList, "attributeList");
        writeUnsignedInt(buffer, attributeList.size());
        for (PlayerAttribute attribute : attributeList) {
            writeString(buffer, attribute.getName());
            buffer.writeFloatLE(attribute.getMinimumValue());
            buffer.writeFloatLE(attribute.getMaximumValue());
            buffer.writeFloatLE(attribute.getValue());
            buffer.writeFloatLE(attribute.getDefaultValue());
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
        int x = readSignedInt(buffer);
        int y = readUnsignedInt(buffer);
        int z = readSignedInt(buffer);

        return new Vector3i(x, y, z);
    }

    public static void writeVector3i(ByteBuf buffer, Vector3i blockPosition) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(blockPosition, "blockPosition");
        writeSignedInt(buffer, blockPosition.getX());
        writeUnsignedInt(buffer, blockPosition.getY());
        writeSignedInt(buffer, blockPosition.getZ());
    }

    public static Vector3i readSignedBlockPosition(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int x = readSignedInt(buffer);
        int y = readSignedInt(buffer);
        int z = readSignedInt(buffer);

        return new Vector3i(x, y, z);
    }

    public static void writeSignedBlockPosition(ByteBuf buffer, Vector3i blockPosition) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(blockPosition, "blockPosition");
        writeSignedInt(buffer, blockPosition.getX());
        writeSignedInt(buffer, blockPosition.getY());
        writeSignedInt(buffer, blockPosition.getZ());
    }

    public static ItemStack readItemStack(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int id = readSignedInt(buffer);
        if (id <= 0) {
            return new NukkitItemStack(BlockTypes.AIR, 1, null);
        }

        int aux = readSignedInt(buffer);
        short damage = (short) (aux >> 8);
        if (damage == Short.MAX_VALUE) damage = -1;
        int count = aux & 0xff;
        short nbtSize = buffer.readShortLE();

        ItemType type = ItemTypes.byId(id);

        ItemStackBuilder builder = new NukkitItemStackBuilder()
                .itemType(type)
                .itemData(MetadataSerializer.deserializeMetadata(type, damage))
                .amount(count);

        if (nbtSize > 0) {
            try (NBTReader reader = new NBTReader(new LittleEndianByteBufInputStream(buffer.readSlice(nbtSize)))) {
                Tag<?> tag = reader.readTag();
                if (tag instanceof CompoundTag) {
                    NBTIO.applyItemData(builder, ((CompoundTag) tag).getValue());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }

        // TODO: Use these instead of just ignoring.
        int canPlaceCount = readSignedInt(buffer);
        for (int i = 0; i < canPlaceCount; i++) {
            readString(buffer);
        }

        int canBreakCount = readSignedInt(buffer);
        for (int i = 0; i < canBreakCount; i++) {
            readString(buffer);
        }
        return builder.build();
    }

    public static void writeItemStack(ByteBuf buffer, ItemStack itemStack) {
        Preconditions.checkNotNull(buffer, "buffer");
        if (itemStack == null || itemStack.getItemType() == BlockTypes.AIR) {
            buffer.writeByte(0); // Save having to send all other data.
            return;
        }

        writeSignedInt(buffer, itemStack.getItemType().getId());
        short metadataValue = MetadataSerializer.serializeMetadata(itemStack);
        if (metadataValue == -1) metadataValue = Short.MAX_VALUE;
        writeSignedInt(buffer, (metadataValue << 8) | itemStack.getAmount());

        // Remember this position, since we'll be writing the true NBT size here later:
        int sizeIndex = buffer.writerIndex();
        buffer.writeShort(0);
        int afterSizeIndex = buffer.writerIndex();

        if (itemStack instanceof NukkitItemStack) {
            try (NBTWriter stream = new NBTWriter(new LittleEndianByteBufOutputStream(buffer))) {
                stream.write(((NukkitItemStack) itemStack).toSpecificNBT());
            } catch (IOException e) {
                // This shouldn't happen (as this is backed by a Netty ByteBuf), but okay...
                throw new IllegalStateException("Unable to save NBT data", e);
            }

            // Set to the written NBT size
            buffer.setShortLE(sizeIndex, buffer.writerIndex() - afterSizeIndex);
        }

        // TODO: Use these instead of just ignoring.
        writeSignedInt(buffer, 0); // can place
        writeSignedInt(buffer, 0); // can break
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
        Long varLong = null;
        if (origin == CommandOriginData.Origin.DEV_CONSOLE || origin == CommandOriginData.Origin.TEST) {
            varLong = readSignedLong(buffer);
        }
        return new CommandOriginData(origin, uuid, requestId, varLong);
    }

    public static void writeCommandOriginData(ByteBuf buffer, CommandOriginData commandOriginData) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(commandOriginData, "commandOriginData");
        writeUnsignedInt(buffer, commandOriginData.getOrigin().ordinal());
        writeUuid(buffer, commandOriginData.getUuid());
        writeString(buffer, commandOriginData.getRequestId());
        if (commandOriginData.getOrigin() == CommandOriginData.Origin.DEV_CONSOLE ||
                commandOriginData.getOrigin() == CommandOriginData.Origin.TEST && commandOriginData.getUnknown0().isPresent()) {
            writeSignedLong(buffer, commandOriginData.getUnknown0().getAsLong());
        }
    }

    public static void writeGameRules(ByteBuf buffer, Collection<GameRule> gameRules) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(gameRules, "gameRules");
        writeUnsignedInt(buffer, gameRules.size());
        for (GameRule rule: gameRules) {
            writeString(buffer, rule.getName().toLowerCase());
            if (rule instanceof BooleanGameRule) {
                buffer.writeByte(1);
                buffer.writeBoolean(((BooleanGameRule) rule).getPrimitiveValue());
            }
            if (rule instanceof IntGameRule) {
                buffer.writeByte(2);
                writeSignedInt(buffer, ((IntGameRule) rule).getPrimitiveValue());
            }
            if (rule instanceof FloatGameRule) {
                buffer.writeByte(3);
                buffer.writeFloatLE(((FloatGameRule) rule).getPrimitiveValue());
            }
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
        writeSignedInt(buffer, levelSettings.getSeed());
        writeSignedInt(buffer, levelSettings.getDimension());
        writeSignedInt(buffer, levelSettings.getGenerator());
        writeSignedInt(buffer, levelSettings.getWorldGamemode().ordinal());
        writeSignedInt(buffer, levelSettings.getDifficulty());
        writeVector3f(buffer, levelSettings.getSpawnPosition());
        buffer.writeBoolean(levelSettings.isAchievementsDisabled());
        writeSignedInt(buffer, levelSettings.getTime());
        buffer.writeBoolean(levelSettings.isEducationLevel());
        buffer.writeFloatLE(levelSettings.getRainLevel());
        buffer.writeFloatLE(levelSettings.getLightningLevel());
        buffer.writeBoolean(levelSettings.isMultiplayerGame());
        buffer.writeBoolean(levelSettings.isBroadcastingToLan());
        buffer.writeBoolean(levelSettings.isBroadcastingToXboxLive());
        buffer.writeBoolean(levelSettings.isCommandsEnabled());
        buffer.writeBoolean(levelSettings.isTexturePacksRequired());
        writeGameRules(buffer, levelSettings.getGameRules());
        buffer.writeBoolean(levelSettings.isBonusChestEnabled());
        buffer.writeBoolean(levelSettings.isStartingWithMap());
        buffer.writeBoolean(levelSettings.isTrustingPlayers());
        writeSignedInt(buffer, levelSettings.getDefaultPlayerPermission().ordinal());
        writeSignedInt(buffer, levelSettings.getXboxLiveBroadcastMode());
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
}
