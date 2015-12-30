package cn.nukkit.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
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

    public static byte[] writeMetadata(Map<Integer, EntityData> data) {
        BinaryStream stream = new BinaryStream();
        for (int bottom : data.keySet()) {
            EntityData d = data.get(bottom);
            stream.putByte((byte) (((d.getType() << 5) | (bottom & 0x1F)) & 0xff));
            switch (d.getType()) {
                case Entity.DATA_TYPE_BYTE:
                    stream.putByte(((ByteEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_SHORT:
                    stream.putLShort(((ShortEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_INT:
                    stream.putLInt(((IntEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    stream.putLFloat(((FloatEntityData) d).getData());
                    break;
                case Entity.DATA_TYPE_STRING:
                    String s = ((StringEntityData) d).getData();
                    stream.putLShort(s.getBytes(StandardCharsets.UTF_8).length);
                    stream.put(s.getBytes(StandardCharsets.UTF_8));
                    break;
                case Entity.DATA_TYPE_SLOT:
                    SlotEntityData slot = (SlotEntityData) d;
                    stream.putLShort(slot.id);
                    stream.putByte(slot.meta);
                    stream.putLShort(slot.count);
                    break;
                case Entity.DATA_TYPE_POS:
                    PositionEntityData pos = (PositionEntityData) d;
                    stream.putLInt(pos.x);
                    stream.putLInt(pos.y);
                    stream.putLInt(pos.z);
                    break;
                case Entity.DATA_TYPE_LONG:
                    stream.putLLong(((LongEntityData) d).getData());
                    break;
            }
        }

        stream.putByte((byte) 0x7f);
        return stream.getBuffer();
    }

    public static Map<Integer, EntityData> readMetadata(byte[] payload) {
        int offset = 0;
        Map<Integer, EntityData> m = new HashMap<>();
        int b = payload[offset] & 0xff;
        ++offset;
        while (b != 0x7f && offset < payload.length) {
            int bottom = b & 0x1f;
            int type = b >> 5;

            EntityData entry;
            switch (type) {
                case Entity.DATA_TYPE_BYTE:
                    entry = new ByteEntityData(payload[offset]);
                    ++offset;
                    break;
                case Entity.DATA_TYPE_SHORT:
                    entry = new ShortEntityData(readLShort(subBytes(payload, offset, 2)));
                    offset += 2;
                    break;
                case Entity.DATA_TYPE_INT:
                    entry = new IntEntityData(readLInt(subBytes(payload, offset, 4)));
                    offset += 4;
                    break;
                case Entity.DATA_TYPE_FLOAT:
                    entry = new FloatEntityData(readLFloat(subBytes(payload, offset, 4)));
                    offset += 4;
                    break;
                case Entity.DATA_TYPE_STRING:
                    int len = readLShort(subBytes(payload, offset, 2));
                    offset += 2;
                    String str = new String(subBytes(payload, offset, len));
                    offset += len;
                    entry = new StringEntityData(str);
                    break;
                case Entity.DATA_TYPE_SLOT:
                    int id = readLShort(subBytes(payload, offset, 2));
                    offset += 2;
                    byte meta = payload[offset];
                    ++offset;
                    int count = readLShort(subBytes(payload, offset, 2));
                    offset += 2;
                    entry = new SlotEntityData(id, meta, count);
                    break;
                case Entity.DATA_TYPE_POS:
                    int[] intArray = new int[3];
                    for (int i = 0; i < 3; ++i) {
                        intArray[i] = readLInt(subBytes(payload, offset, 4));
                        offset += 4;
                    }
                    entry = new PositionEntityData(intArray[0], intArray[1], intArray[2]);
                    break;
                case Entity.DATA_TYPE_LONG:
                    entry = new LongEntityData(readLLong(subBytes(payload, offset, 4)));
                    offset += 8;
                    break;
                default:
                    return new HashMap<>();
            }

            m.put(bottom, entry);
            b = payload[offset] & 0xff;
            ++offset;
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
        hexString = hexString.toUpperCase();
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
        byte[][] splits = new byte[1024][chunkSize];
        int chunks = 0;
        for (int i = 0; i < bytes.length; i += chunkSize) {
            if ((bytes.length - i) > chunkSize) {
                splits[chunks] = Arrays.copyOfRange(bytes, i, i + chunkSize);
            } else {
                splits[chunks] = Arrays.copyOfRange(bytes, i, bytes.length);
            }
            chunks++;
        }

        splits = Arrays.copyOf(splits, chunks);

        return splits;
    }

    public static byte[] appendBytes(byte[][] bytes) {
        int length = 1;
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
