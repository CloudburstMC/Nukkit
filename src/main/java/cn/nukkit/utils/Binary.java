package cn.nukkit.utils;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.types.EntityLink;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufUtil;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@UtilityClass
public class Binary {

    /**
     * Reads a list of Attributes from the stream.
     *
     * @return Attribute[]
     */
    public static Attribute[] readAttributes(ByteBuf buffer) throws Exception {
        List<Attribute> list = new ArrayList<>();
        long count = readUnsignedVarInt(buffer);

        for (int i = 0; i < count; ++i) {
            String name = readString(buffer);
            Attribute attr = Attribute.getAttributeByName(name);
            if (attr != null) {
                attr.setMinValue(buffer.readFloatLE());
                attr.setValue(buffer.readFloatLE());
                attr.setMaxValue(buffer.readFloatLE());
                list.add(attr);
            } else {
                throw new Exception("Unknown attribute type \"" + name + "\"");
            }
        }

        return list.toArray(new Attribute[0]);
    }

    /**
     * Writes a list of Attributes to the packet buffer using the standard format.
     */
    public static void writeAttributes(ByteBuf buffer, Attribute[] attributes) {
        writeUnsignedVarInt(buffer, attributes.length);
        for (Attribute attribute : attributes) {
            writeString(buffer, attribute.getName());
            buffer.writeFloatLE(attribute.getMinValue());
            buffer.writeFloatLE(attribute.getValue());
            buffer.writeFloatLE(attribute.getMaxValue());
        }
    }

    public static UUID readUuid(ByteBuf buffer) {
        return new UUID(buffer.readLongLE(), buffer.readLongLE());
    }

    public static void writeUuid(ByteBuf buffer, UUID uuid) {
        buffer.writeLongLE(uuid.getMostSignificantBits());
        buffer.writeLongLE(uuid.getLeastSignificantBits());
    }

    public static void writeSkin(ByteBuf buffer, Skin skin) {
        writeString(buffer, skin.getSkinId());
        writeByteArray(buffer, skin.getSkinData());
        writeByteArray(buffer, skin.getCapeData());
        writeString(buffer, skin.getGeometryName());
        writeString(buffer, skin.getGeometryData());
    }

    public static Skin readSkin(ByteBuf buffer) {
        Skin skin = new Skin();
        skin.setSkinId(readString(buffer));
        skin.setSkinData(readByteArray(buffer));
        skin.setCapeData(readByteArray(buffer));
        skin.setGeometryName(readString(buffer));
        skin.setGeometryData(readString(buffer));
        return skin;
    }

    public static Item readItem(ByteBuf buffer) {
        int id = readVarInt(buffer);

        if (id == 0) {
            return Item.get(0, 0, 0);
        }
        int auxValue = readVarInt(buffer);
        int data = auxValue >> 8;
        if (data == Short.MAX_VALUE) {
            data = -1;
        }
        int cnt = auxValue & 0xff;

        int nbtLen = buffer.readShortLE();
        byte[] nbt = new byte[0];
        if (nbtLen > 0) {
            nbt = new byte[nbtLen];
            buffer.readBytes(nbt);
        } else if (nbtLen == -1) {
            int nbtTagCount = (int) readUnsignedVarInt(buffer);
            ByteBufInputStream stream = new ByteBufInputStream(buffer);
            for (int i = 0; i < nbtTagCount; i++) {
                try {
                    // TODO: 05/02/2019 This hack is necessary because we keep the raw NBT tag. Try to remove it.
                    CompoundTag tag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, true);
                    // tool damage hack
                    if (tag.contains("Damage")) {
                        data = tag.getInt("Damage");
                        tag.remove("Damage");
                    }
                    if (tag.contains("__DamageConflict__")) {
                        tag.put("Damage", tag.removeAndGet("__DamageConflict__"));
                    }
                    if (tag.getAllTags().size() > 0) {
                        nbt = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, false);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String[] canPlaceOn = new String[readVarInt(buffer)];
        for (int i = 0; i < canPlaceOn.length; ++i) {
            canPlaceOn[i] = readString(buffer);
        }

        String[] canDestroy = new String[readVarInt(buffer)];
        for (int i = 0; i < canDestroy.length; ++i) {
            canDestroy[i] = readString(buffer);
        }

        Item item = Item.get(
                id, data, cnt, nbt
        );

        if (canDestroy.length > 0 || canPlaceOn.length > 0) {
            CompoundTag namedTag = item.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }

            if (canDestroy.length > 0) {
                ListTag<StringTag> listTag = new ListTag<>("CanDestroy");
                for (String blockName : canDestroy) {
                    listTag.add(new StringTag("", blockName));
                }
                namedTag.put("CanDestroy", listTag);
            }

            if (canPlaceOn.length > 0) {
                ListTag<StringTag> listTag = new ListTag<>("CanPlaceOn");
                for (String blockName : canPlaceOn) {
                    listTag.add(new StringTag("", blockName));
                }
                namedTag.put("CanPlaceOn", listTag);
            }
            item.setNamedTag(namedTag);
        }

        if (item.getId() == 513) { // TODO: Shields
            readVarLong(buffer);
        }

        return item;
    }

    public static void writeItem(ByteBuf buffer, Item item) {
        if (item == null || item.getId() == 0) {
            writeVarInt(buffer, 0);
            return;
        }

        boolean isDurable = item instanceof ItemDurable;

        writeVarInt(buffer, item.getId());

        int auxValue = item.getCount();
        if (!isDurable) {
            auxValue |= (((item.hasMeta() ? item.getDamage() : -1) & 0x7fff) << 8);
        }
        writeVarInt(buffer, auxValue);

        if (item.hasCompoundTag() || isDurable) {
            try {
                // hack for tool damage
                byte[] nbt = item.getCompoundTag();
                CompoundTag tag;
                if (nbt == null || nbt.length == 0) {
                    tag = new CompoundTag();
                } else {
                    tag = NBTIO.read(nbt, ByteOrder.LITTLE_ENDIAN, false);
                }
                if (tag.contains("Damage")) {
                    tag.put("__DamageConflict__", tag.removeAndGet("Damage"));
                }
                if (isDurable) {
                    tag.putInt("Damage", item.getDamage());
                }

                buffer.writeShortLE(-1);
                buffer.writeByte(1);
                buffer.writeBytes(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            buffer.writeShortLE(0);
        }
        List<String> canPlaceOn = extractStringList(item, "CanPlaceOn");
        List<String> canDestroy = extractStringList(item, "CanDestroy");
        writeVarInt(buffer, canPlaceOn.size());
        for (String block : canPlaceOn) {
            writeString(buffer, block);
        }
        writeVarInt(buffer, canDestroy.size());
        for (String block : canDestroy) {
            writeString(buffer, block);
        }

        if (item.getId() == 513) { // TODO: Shields
            writeVarLong(buffer, 0);
        }
    }

    public static Item readRecipeIngredient(ByteBuf buffer) {
        int id = readVarInt(buffer);

        if (id == 0) {
            return Item.get(0, 0, 0);
        }

        int damage = readVarInt(buffer);
        if (damage == 0x7fff) damage = -1;
        int count = readVarInt(buffer);

        return Item.get(id, damage, count);
    }

    public static void writeRecipeIngredient(ByteBuf buffer, Item ingredient) {
        if (ingredient.isNull()) {
            writeVarInt(buffer, 0);
        } else {
            writeVarInt(buffer, ingredient.getId());
            writeVarInt(buffer, ingredient.getDamage() & 0x7fff);
            writeVarInt(buffer, ingredient.getCount());
        }
    }

    private List<String> extractStringList(Item item, String tagName) {
        CompoundTag namedTag = item.getNamedTag();
        if (namedTag == null) {
            return Collections.emptyList();
        }

        ListTag<StringTag> listTag = namedTag.getList(tagName, StringTag.class);
        if (listTag == null) {
            return Collections.emptyList();
        }

        int size = listTag.size();
        List<String> values = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            StringTag stringTag = listTag.get(i);
            if (stringTag != null) {
                values.add(stringTag.data);
            }
        }

        return values;
    }

    public static byte[] readByteArray(ByteBuf buffer) {
        byte[] bytes = new byte[(int) readUnsignedVarInt(buffer)];
        buffer.readBytes(bytes);
        return bytes;
    }

    public static void writeByteArray(ByteBuf buffer, byte[] bytes) {
        writeUnsignedVarInt(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }

    public static ByteBuf readVarIntBuffer(ByteBuf buffer) {
        return buffer.readSlice((int) readUnsignedVarInt(buffer));
    }

    public static void writeVarIntBuffer(ByteBuf buffer, ByteBuf toWrite) {
        writeUnsignedVarInt(buffer, toWrite.readableBytes());
        buffer.writeBytes(toWrite);
    }

    public static String readString(ByteBuf buffer) {
        return new String(readByteArray(buffer), StandardCharsets.UTF_8);
    }

    public static void writeString(ByteBuf buffer, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeByteArray(buffer, bytes);
    }

    public static long readUnsignedVarInt(ByteBuf buffer) {
        return VarInt.readUnsignedVarInt(buffer);
    }

    public static void writeUnsignedVarInt(ByteBuf buffer, long v) {
        VarInt.writeUnsignedVarInt(buffer, v);
    }

    public static int readVarInt(ByteBuf buffer) {
        return VarInt.readVarInt(buffer);
    }

    public static void writeVarInt(ByteBuf buffer, int v) {
        VarInt.writeVarInt(buffer, v);
    }

    public static long readVarLong(ByteBuf buffer) {
        return VarInt.readVarLong(buffer);
    }

    public static void writeVarLong(ByteBuf buffer, long v) {
        VarInt.writeVarLong(buffer, v);
    }

    public static long readUnsignedVarLong(ByteBuf buffer) {
        return VarInt.readUnsignedVarLong(buffer);
    }

    public static void writeUnsignedVarLong(ByteBuf buffer, long v) {
        VarInt.writeUnsignedVarLong(buffer, v);
    }

    public static BlockVector3 readBlockVector3(ByteBuf buffer) {
        return new BlockVector3(readVarInt(buffer), (int) readUnsignedVarInt(buffer), readVarInt(buffer));
    }

    public static BlockVector3 readSignedBlockPosition(ByteBuf buffer) {
        return new BlockVector3(readVarInt(buffer), readVarInt(buffer), readVarInt(buffer));
    }

    public static void writeSignedBlockPosition(ByteBuf buffer, BlockVector3 v) {
        writeVarInt(buffer, v.x);
        writeVarInt(buffer, v.y);
        writeVarInt(buffer, v.z);
    }

    public static void writeBlockVector3(ByteBuf buffer, BlockVector3 v) {
        writeBlockVector3(buffer, v.x, v.y, v.z);
    }

    public static void writeBlockVector3(ByteBuf buffer, int x, int y, int z) {
        writeVarInt(buffer, x);
        writeUnsignedVarInt(buffer, y);
        writeVarInt(buffer, z);
    }

    public static Vector3f readVector3f(ByteBuf buffer) {
        return new Vector3f(buffer.readFloatLE(), buffer.readFloatLE(), buffer.readFloatLE());
    }

    public static void writeVector3f(ByteBuf buffer, Vector3f v) {
        writeVector3f(buffer, v.x, v.y, v.z);
    }

    public static void writeVector3f(ByteBuf buffer, float x, float y, float z) {
        buffer.writeFloatLE(x);
        buffer.writeFloatLE(y);
        buffer.writeFloatLE(z);
    }

    public static void writeGameRules(ByteBuf buffer, GameRuleMap gameRules) {
        writeUnsignedVarInt(buffer, gameRules.size());
        gameRules.forEach((gameRule, value) -> {
            writeString(buffer, gameRule.getName());

            Class<?> valueClass = gameRule.getValueClass();
            if (valueClass == Boolean.class) {
                Binary.writeUnsignedVarInt(buffer, 1);
                buffer.writeBoolean((boolean) value);
            } else if (valueClass == Integer.class) {
                Binary.writeUnsignedVarInt(buffer, 2);
                Binary.writeUnsignedVarInt(buffer, (int) value);
            } else if (valueClass == Float.class) {
                Binary.writeUnsignedVarInt(buffer, 3);
                buffer.writeFloatLE((float) value);
            } else {
                throw new IllegalArgumentException("Unknown GameRule type");
            }
        });
    }

    /**
     * Reads and returns an EntityUniqueID
     *
     * @return int
     */
    public static long readEntityUniqueId(ByteBuf buffer) {
        return readVarLong(buffer);
    }

    /**
     * Writes an EntityUniqueID
     */
    public static void writeEntityUniqueId(ByteBuf buffer, long eid) {
        writeVarLong(buffer, eid);
    }

    /**
     * Reads and returns an EntityRuntimeID
     */
    public static long readEntityRuntimeId(ByteBuf buffer) {
        return readUnsignedVarLong(buffer);
    }

    /**
     * Writes an EntityUniqueID
     */
    public static void writeEntityRuntimeId(ByteBuf buffer, long eid) {
        writeUnsignedVarLong(buffer, eid);
    }

    public static BlockFace readBlockFace(ByteBuf buffer) {
        return BlockFace.fromIndex(readVarInt(buffer));
    }

    public static void writeBlockFace(ByteBuf buffer, BlockFace face) {
        writeVarInt(buffer, face.getIndex());
    }

    public static void writeEntityLink(ByteBuf buffer, EntityLink link) {
        writeEntityUniqueId(buffer, link.fromEntityUniquieId);
        writeEntityUniqueId(buffer, link.toEntityUniquieId);
        buffer.writeByte(link.type);
        buffer.writeBoolean(link.immediate);
    }

    public static EntityLink readEntityLink(ByteBuf buffer) {
        return new EntityLink(
                readEntityUniqueId(buffer),
                readEntityUniqueId(buffer),
                buffer.readByte(),
                buffer.readBoolean()
        );
    }

    public static void writeMetadata(ByteBuf buffer, EntityMetadata metadata) {
        Map<Integer, EntityData> map = metadata.getMap();
        writeUnsignedVarInt(buffer, map.size());
        for (int id : map.keySet()) {
            EntityData d = map.get(id);
            writeUnsignedVarInt(buffer, id);
            writeUnsignedVarInt(buffer, d.getType());
            switch (d.getType()) {
                case Entity.DATA_TYPE_BYTE:
                    buffer.writeByte(((ByteEntityData) d).getData().byteValue());
                    break;
                case Entity.DATA_TYPE_SHORT:
                    buffer.writeShortLE(((ShortEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_INT:
                    writeVarInt(buffer, ((IntEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    buffer.writeFloatLE(((FloatEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_STRING:
                    writeString(buffer, ((StringEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_NBT:
                    NBTEntityData slot = (NBTEntityData) d;
                    try {
                        buffer.writeBytes(NBTIO.write(slot.getData(), ByteOrder.LITTLE_ENDIAN, true));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case Entity.DATA_TYPE_POS:
                    IntPositionEntityData pos = (IntPositionEntityData) d;
                    writeSignedBlockPosition(buffer, pos.getData());
                    break;
                case Entity.DATA_TYPE_LONG:
                    writeVarLong(buffer, ((LongEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_VECTOR3F:
                    Vector3fEntityData v3data = (Vector3fEntityData) d;
                    writeVector3f(buffer, v3data.getData());
                    break;
            }
        }
    }

    public static EntityMetadata readMetadata(ByteBuf buffer) {
        long count = readUnsignedVarInt(buffer);
        EntityMetadata m = new EntityMetadata();
        for (int i = 0; i < count; i++) {
            int key = (int) readUnsignedVarInt(buffer);
            int type = (int) readUnsignedVarInt(buffer);
            EntityData value = null;
            switch (type) {
                case Entity.DATA_TYPE_BYTE:
                    value = new ByteEntityData(key, buffer.readByte());
                    break;
                case Entity.DATA_TYPE_SHORT:
                    value = new ShortEntityData(key, buffer.readShortLE());
                    break;
                case Entity.DATA_TYPE_INT:
                    value = new IntEntityData(key, readVarInt(buffer));
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    value = new FloatEntityData(key, buffer.readFloatLE());
                    break;
                case Entity.DATA_TYPE_STRING:
                    value = new StringEntityData(key, readString(buffer));
                    break;
                case Entity.DATA_TYPE_NBT:
                    ByteBufInputStream stream = new ByteBufInputStream(buffer);
                    try {
                        CompoundTag tag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, true);
                        value = new NBTEntityData(key, tag);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case Entity.DATA_TYPE_POS:
                    BlockVector3 v3 = readSignedBlockPosition(buffer);
                    value = new IntPositionEntityData(key, v3.x, v3.y, v3.z);
                    break;
                case Entity.DATA_TYPE_LONG:
                    value = new LongEntityData(key, readVarLong(buffer));
                    break;
                case Entity.DATA_TYPE_VECTOR3F:
                    value = new Vector3fEntityData(key, readVector3f(buffer));
                    break;
            }
            if (value != null) m.put(value);
        }
        return m;
    }

    public static String bytesToHexString(ByteBuf src) {
        return ByteBufUtil.hexDump(src);
    }

    public static String bytesToHexString(byte[] src, boolean blank) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }

        for (byte b : src) {
            if (!(stringBuilder.length() == 0) && blank) {
                stringBuilder.append(" ");
            }
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        return ByteBufUtil.decodeHexDump(hexString);
    }

    public static byte[] subBytes(byte[] bytes, int start, int length) {
        int len = Math.min(bytes.length, start + length);
        return Arrays.copyOfRange(bytes, start, len);
    }

    public static byte[] subBytes(byte[] bytes, int start) {
        return subBytes(bytes, start, bytes.length - start);
    }

    public static byte[][] splitBytes(byte[] bytes, int chunkSize) {
        byte[][] splits = new byte[(bytes.length + chunkSize - 1) / chunkSize][chunkSize];
        int chunks = 0;

        for (int i = 0; i < bytes.length; i += chunkSize) {
            if ((bytes.length - i) > chunkSize) {
                splits[chunks] = Arrays.copyOfRange(bytes, i, i + chunkSize);
            } else {
                splits[chunks] = Arrays.copyOfRange(bytes, i, bytes.length);
            }
            chunks++;
        }

        return splits;
    }

    public static byte[] appendBytes(byte[][] bytes) {
        int length = 0;
        for (byte[] b : bytes) {
            length += b.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (byte[] b : bytes) {
            buffer.put(b);
        }
        return buffer.array();
    }

    public static byte[] appendBytes(byte byte1, byte[]... bytes2) {
        int length = 1;
        for (byte[] bytes : bytes2) {
            length += bytes.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(byte1);
        for (byte[] bytes : bytes2) {
            buffer.put(bytes);
        }
        return buffer.array();
    }

    public static byte[] appendBytes(byte[] bytes1, byte[]... bytes2) {
        int length = bytes1.length;
        for (byte[] bytes : bytes2) {
            length += bytes.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(bytes1);
        for (byte[] bytes : bytes2) {
            buffer.put(bytes);
        }
        return buffer.array();
    }


}
