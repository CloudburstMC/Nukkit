package cn.nukkit.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.*;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Binary {

    public static int signByte(int value) {
        return value << 56 >> 56;
    }

    public static int unsignByte(int value) {
        return value & 0xff;
    }

    public static int signShort(int value) {
        return value << 48 >> 48;
    }

    public int unsignShort(int value) {
        return value & 0xffff;
    }

    public static int signInt(int value) {
        return value << 32 >> 32;
    }

    public static int unsignInt(int value) {
        return value;
    }

    //Triad: {0x00,0x00,0x01}<=>1
    public static int readTriad(byte[] bytes) {
        return readInt(new byte[]{
                (byte) 0x00,
                bytes[0],
                bytes[1],
                bytes[2]
        });
    }

    public static byte[] writeTriad(int value) {
        return new byte[]{
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    //LTriad: {0x01,0x00,0x00}<=>1
    public static int readLTriad(byte[] bytes) {
        return readLInt(new byte[]{
                bytes[0],
                bytes[1],
                bytes[2],
                (byte) 0x00
        });
    }

    public static byte[] writeLTriad(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) ((value >>> 16) & 0xFF)
        };
    }

    public static UUID readUUID(byte[] bytes) {
        return new UUID(readLLong(bytes), readLLong(new byte[]{
                bytes[8],
                bytes[9],
                bytes[10],
                bytes[11],
                bytes[12],
                bytes[13],
                bytes[14],
                bytes[15]
        }));
    }

    public static byte[] writeUUID(UUID uuid) {
        return appendBytes(writeLLong(uuid.getMostSignificantBits()), writeLLong(uuid.getLeastSignificantBits()));
    }

    public static byte[] writeMetadata(EntityMetadata metadata) {
        BinaryStream stream = new BinaryStream();
        Map<Integer, EntityData> map = metadata.getMap();
        stream.putUnsignedVarInt(map.size());
        for (int id : map.keySet()) {
            EntityData d = map.get(id);
            stream.putUnsignedVarInt(id);
            stream.putUnsignedVarInt(d.getType());
            switch (d.getType()) {
                case Entity.DATA_TYPE_BYTE:
                    stream.putByte(((ByteEntityData) d).getData().byteValue());
                    break;
                case Entity.DATA_TYPE_SHORT:
                    stream.putLShort(((ShortEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_INT:
                    stream.putVarInt(((IntEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    stream.putLFloat(((FloatEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_STRING:
                    String s = ((StringEntityData) d).getData();
                    stream.putUnsignedVarInt(s.getBytes(StandardCharsets.UTF_8).length);
                    stream.put(s.getBytes(StandardCharsets.UTF_8));
                    break;
                case Entity.DATA_TYPE_NBT:
                    NBTEntityData slot = (NBTEntityData) d;
                    try {
                        stream.put(NBTIO.write(slot.getData(), ByteOrder.LITTLE_ENDIAN, true));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case Entity.DATA_TYPE_POS:
                    IntPositionEntityData pos = (IntPositionEntityData) d;
                    stream.putVarInt(pos.x);
                    stream.putVarInt(pos.y);
                    stream.putVarInt(pos.z);
                    break;
                case Entity.DATA_TYPE_LONG:
                    stream.putVarLong(((LongEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_VECTOR3F:
                    Vector3fEntityData v3data = (Vector3fEntityData) d;
                    stream.putLFloat(v3data.x);
                    stream.putLFloat(v3data.y);
                    stream.putLFloat(v3data.z);
                    break;
            }
        }
        return stream.getBuffer();
    }

    public static EntityMetadata readMetadata(byte[] payload) {
        BinaryStream stream = new BinaryStream();
        stream.setBuffer(payload);
        long count = stream.getUnsignedVarInt();
        EntityMetadata m = new EntityMetadata();
        for (int i = 0; i < count; i++) {
            int key = (int) stream.getUnsignedVarInt();
            int type = (int) stream.getUnsignedVarInt();
            EntityData value = null;
            switch (type) {
                case Entity.DATA_TYPE_BYTE:
                    value = new ByteEntityData(key, stream.getByte());
                    break;
                case Entity.DATA_TYPE_SHORT:
                    value = new ShortEntityData(key, stream.getLShort());
                    break;
                case Entity.DATA_TYPE_INT:
                    value = new IntEntityData(key, stream.getVarInt());
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    value = new FloatEntityData(key, stream.getLFloat());
                    break;
                case Entity.DATA_TYPE_STRING:
                    value = new StringEntityData(key, stream.getString());
                    break;
                case Entity.DATA_TYPE_NBT:
                    int offset = stream.getOffset();
                    FastByteArrayInputStream fbais = new FastByteArrayInputStream(stream.get());
                    try {
                        CompoundTag tag = NBTIO.read(fbais, ByteOrder.LITTLE_ENDIAN, true);
                        value = new NBTEntityData(key, tag);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    stream.setOffset(offset + (int) fbais.position());
                    break;
                case Entity.DATA_TYPE_POS:
                    BlockVector3 v3 = stream.getSignedBlockPosition();
                    value = new IntPositionEntityData(key, v3.x, v3.y, v3.z);
                    break;
                case Entity.DATA_TYPE_LONG:
                    value = new LongEntityData(key, stream.getVarLong());
                    break;
                case Entity.DATA_TYPE_VECTOR3F:
                    value = new Vector3fEntityData(key, stream.getVector3f());
                    break;
            }
            if (value != null) m.put(value);
        }
        return m;
    }

    public static boolean readBool(byte b) {
        return b == 0;
    }

    public static byte writeBool(boolean b) {
        return (byte) (b ? 0x01 : 0x00);
    }

    public static int readSignedByte(byte b) {
        return b & 0xFF;
    }

    public static byte writeByte(byte b) {
        return b;
    }

    public static int readShort(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 8) + (bytes[1] & 0xFF);
    }

    public static short readSignedShort(byte[] bytes) {
        return (short) readShort(bytes);
    }

    public static byte[] writeShort(int s) {
        return new byte[]{
                (byte) ((s >>> 8) & 0xFF),
                (byte) (s & 0xFF)
        };
    }

    public static int readLShort(byte[] bytes) {
        return ((bytes[1] & 0xFF) << 8) + (bytes[0] & 0xFF);
    }

    public static short readSignedLShort(byte[] bytes) {
        return (short) readLShort(bytes);
    }

    public static byte[] writeLShort(int s) {
        s &= 0xffff;
        return new byte[]{
                (byte) (s & 0xFF),
                (byte) ((s >>> 8) & 0xFF)
        };
    }

    public static int readInt(byte[] bytes) {
        return ((bytes[0] & 0xff) << 24) +
                ((bytes[1] & 0xff) << 16) +
                ((bytes[2] & 0xff) << 8) +
                (bytes[3] & 0xff);
    }

    public static byte[] writeInt(int i) {
        return new byte[]{
                (byte) ((i >>> 24) & 0xFF),
                (byte) ((i >>> 16) & 0xFF),
                (byte) ((i >>> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    public static int readLInt(byte[] bytes) {
        return ((bytes[3] & 0xff) << 24) +
                ((bytes[2] & 0xff) << 16) +
                ((bytes[1] & 0xff) << 8) +
                (bytes[0] & 0xff);
    }

    public static byte[] writeLInt(int i) {
        return new byte[]{
                (byte) (i & 0xFF),
                (byte) ((i >>> 8) & 0xFF),
                (byte) ((i >>> 16) & 0xFF),
                (byte) ((i >>> 24) & 0xFF)
        };
    }

    public static float readFloat(byte[] bytes) {
        return readFloat(bytes, -1);
    }

    public static float readFloat(byte[] bytes, int accuracy) {
        float val = Float.intBitsToFloat(readInt(bytes));
        if (accuracy > -1) {
            return (float) NukkitMath.round(val, accuracy);
        } else {
            return val;
        }
    }

    public static byte[] writeFloat(float f) {
        return writeInt(Float.floatToIntBits(f));
    }

    public static float readLFloat(byte[] bytes) {
        return readLFloat(bytes, -1);
    }

    public static float readLFloat(byte[] bytes, int accuracy) {
        float val = Float.intBitsToFloat(readLInt(bytes));
        if (accuracy > -1) {
            return (float) NukkitMath.round(val, accuracy);
        } else {
            return val;
        }
    }

    public static byte[] writeLFloat(float f) {
        return writeLInt(Float.floatToIntBits(f));
    }

    public static double readDouble(byte[] bytes) {
        return Double.longBitsToDouble(readLong(bytes));
    }

    public static byte[] writeDouble(double d) {
        return writeLong(Double.doubleToLongBits(d));
    }

    public static double readLDouble(byte[] bytes) {
        return Double.longBitsToDouble(readLLong(bytes));
    }

    public static byte[] writeLDouble(double d) {
        return writeLLong(Double.doubleToLongBits(d));
    }

    public static long readLong(byte[] bytes) {
        return (((long) bytes[0] << 56) +
                ((long) (bytes[1] & 0xFF) << 48) +
                ((long) (bytes[2] & 0xFF) << 40) +
                ((long) (bytes[3] & 0xFF) << 32) +
                ((long) (bytes[4] & 0xFF) << 24) +
                ((bytes[5] & 0xFF) << 16) +
                ((bytes[6] & 0xFF) << 8) +
                ((bytes[7] & 0xFF)));
    }

    public static byte[] writeLong(long l) {
        return new byte[]{
                (byte) (l >>> 56),
                (byte) (l >>> 48),
                (byte) (l >>> 40),
                (byte) (l >>> 32),
                (byte) (l >>> 24),
                (byte) (l >>> 16),
                (byte) (l >>> 8),
                (byte) (l)
        };
    }

    public static long readLLong(byte[] bytes) {
        return (((long) bytes[7] << 56) +
                ((long) (bytes[6] & 0xFF) << 48) +
                ((long) (bytes[5] & 0xFF) << 40) +
                ((long) (bytes[4] & 0xFF) << 32) +
                ((long) (bytes[3] & 0xFF) << 24) +
                ((bytes[2] & 0xFF) << 16) +
                ((bytes[1] & 0xFF) << 8) +
                ((bytes[0] & 0xFF)));
    }

    public static byte[] writeLLong(long l) {
        return new byte[]{
                (byte) (l),
                (byte) (l >>> 8),
                (byte) (l >>> 16),
                (byte) (l >>> 24),
                (byte) (l >>> 32),
                (byte) (l >>> 40),
                (byte) (l >>> 48),
                (byte) (l >>> 56),
        };
    }

    public static byte[] writeVarInt(int v) {
        BinaryStream stream = new BinaryStream();
        stream.putVarInt(v);
        return stream.getBuffer();
    }

    public static byte[] writeUnsignedVarInt(long v) {
        BinaryStream stream = new BinaryStream();
        stream.putUnsignedVarInt(v);
        return stream.getBuffer();
    }

    public static byte[] reserveBytes(byte[] bytes) {
        byte[] newBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            newBytes[bytes.length - 1 - i] = bytes[i];
        }
        return newBytes;
    }

    public static String bytesToHexString(byte[] src) {
        return bytesToHexString(src, false);
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
        String str = "0123456789ABCDEF";
        hexString = hexString.toUpperCase().replace(" ", "");
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (((byte) str.indexOf(hexChars[pos]) << 4) | ((byte) str.indexOf(hexChars[pos + 1])));
        }
        return d;
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

        byte[] appendedBytes = new byte[length];
        int index = 0;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, appendedBytes, index, b.length);
            index += b.length;
        }
        return appendedBytes;
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

        byte[] appendedBytes = new byte[length];
        System.arraycopy(bytes1, 0, appendedBytes, 0, bytes1.length);
        int index = bytes1.length;

        for (byte[] b : bytes2) {
            System.arraycopy(b, 0, appendedBytes, index, b.length);
            index += b.length;
        }
        return appendedBytes;
    }


}
