package cn.nukkit.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Binary {

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
        return new UUID(readLong(bytes), readLong(new byte[]{
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
        return appendBytes(writeLong(uuid.getMostSignificantBits()), writeLong(uuid.getLeastSignificantBits()));
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
                case Entity.DATA_TYPE_SLOT:
                    SlotEntityData slot = (SlotEntityData) d;
                    stream.putLShort(slot.blockId);
                    stream.putByte((byte) slot.meta);
                    stream.putLShort(slot.count);
                    break;
                case Entity.DATA_TYPE_POS:
                    IntPositionEntityData pos = (IntPositionEntityData) d;
                    stream.putVarInt(pos.x);
                    stream.putByte((byte) pos.y);
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
            int key = (int)stream.getUnsignedVarInt();
            int type = (int)stream.getUnsignedVarInt();
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
                case Entity.DATA_TYPE_SLOT:
                    Item item = stream.getSlot();
                    value = new SlotEntityData(key, item.getId(), item.getDamage(), item.getCount());
                    break;
                case Entity.DATA_TYPE_POS:
                    BlockVector3 v3 = stream.getBlockCoords();
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
        return (bytes[1] & 0xFF << 8) + (bytes[0] & 0xFF);
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
        return Float.intBitsToFloat(readInt(bytes));
    }

    public static byte[] writeFloat(float f) {
        return writeInt(Float.floatToIntBits(f));
    }

    public static float readLFloat(byte[] bytes) {
        return Float.intBitsToFloat(readLInt(bytes));
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

    public static int readVarInt(BinaryStream stream) {
        long raw = readUnsignedVarInt(stream);
        long temp = (((raw << 31) >> 31) ^ raw) >> 1;
        return (int) (temp ^ (raw & (1 << 31)));
    }

    public static int readVarInt(DataInputStream stream) throws IOException {
        long raw = readUnsignedVarInt(stream);
        long temp = (((raw << 31) >> 31) ^ raw) >> 1;
        return (int) (temp ^ (raw & (1 << 31)));
    }

    public static byte[] writeVarInt(int v) {
        return writeUnsignedVarInt((v << 1) ^ (v >> 31));
    }

    public static long readUnsignedVarInt(BinaryStream stream) {
        long value = 0;
        int i = 0;
        int b;
        do {
            if (i > 63) {
                throw new IllegalArgumentException("Varint did not terminate after 10 bytes!");
            }
            value |= (((b = stream.getByte()) & 0x7f) << i);
            i += 7;
        } while ((b & 0x80) != 0);
        return value;
    }

    public static long readUnsignedVarInt(DataInputStream stream) throws IOException {
        long value = 0;
        int i = 0;
        byte b;
        do {
            if (i > 63) {
                throw new IllegalArgumentException("Varint did not terminate after 10 bytes!");
            }
            value |= (((b = stream.readByte()) & 0x7f) << i);
            i += 7;
        } while ((b & 0x80) != 0);
        return value;
    }

    public static byte[] writeUnsignedVarInt(long v) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int loops = 0;
        do {
            if (loops > 9) {
                throw new IllegalArgumentException("Varint cannot be longer than 10 bytes!"); //for safety reasons
            }
            long w = v & 0x7f;
            if ((v >> 7) != 0) {
                w = v | 0x80;
            }
            stream.write((byte) w);
            v = ((v >> 7) & (Integer.MAX_VALUE >> 6));
            ++loops;
        } while (v != 0);
        return stream.toByteArray();
    }

    public static long readVarLong(BinaryStream stream){
        BigInteger raw = readUnsignedVarLong(stream);
        BigInteger temp = raw.shiftLeft(63).shiftRight(63).xor(raw).shiftRight(1);
        return temp.xor(raw.and(BigInteger.ONE.shiftLeft(63))).longValue();
    }
    

    public static byte[] writeVarLong(long v){
        return writeUnsignedVarLong((v << 1) ^ (v >> 63));
    }

    public static BigInteger readUnsignedVarLong(BinaryStream stream){
        BigInteger value = BigInteger.ZERO;
        int i = 0;
        int b;
        while(((b = stream.getByte()) & 0x80) != 0){
            value = value.or(BigInteger.valueOf((b & 0x7f) << i));
            i += 7;
            if(i > 63){
                throw new IllegalArgumentException("Value is too long to be an int64");
            }
        }
        return value.or(BigInteger.valueOf(b << i));
    }

    public static byte[] writeUnsignedVarLong(long v) {
        return writeUnsignedVarLong(BigInteger.valueOf(v));
    }

    public static byte[] writeUnsignedVarLong(BigInteger v){
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        v = v.and(new BigInteger("ffffffffffffffff", 16));
        BigInteger b1 = BigInteger.valueOf(0xFFFFFFFFFFFFFF80L);
        BigInteger b2 = BigInteger.valueOf(0x7f);
        BigInteger b3 = BigInteger.valueOf(0x80);
        while (!v.and(b1).equals(BigInteger.ZERO)) {
            buf.write(v.and(b2).or(b3).byteValue());
            v = v.shiftRight(7);
        }
        buf.write(v.byteValue());
        return buf.toByteArray();
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
        StringBuilder stringBuilder = new StringBuilder("");
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
