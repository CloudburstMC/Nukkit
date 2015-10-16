package cn.nukkit.utils;

import cn.nukkit.item.Item;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BinaryStream {

    public int offset;
    private byte[] buffer = new byte[32];
    private int count;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public BinaryStream() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public BinaryStream(byte[] buffer) {
        this(buffer, 0);
    }

    public BinaryStream(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.count = buffer.length;
    }

    public void reset() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.count = buffer.length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public byte[] getBuffer() {
        return Arrays.copyOf(buffer, count);
    }

    public byte[] get() {
        return Arrays.copyOfRange(this.buffer, this.offset, this.count - 1);
    }

    public byte[] get(int len) {
        if (len < 0) {
            this.offset = this.count - 1;
            return new byte[0];
        }
        this.offset = Math.min(this.offset + len, this.count - 1);
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public void put(byte[] bytes) {
        this.ensureCapacity(this.count + bytes.length);
        System.arraycopy(bytes, 0, this.buffer, this.offset, bytes.length);
        this.count += bytes.length;
    }

    public long getLong() {
        return Binary.readLong(this.get(8));
    }

    public void putLong(long l) {
        this.put(Binary.writeLong(l));
    }

    public int getInt() {
        return Binary.readInt(this.get(4));
    }

    public void putInt(int i) {
        this.put(Binary.writeInt(i));
    }

    public short getShort() {
        return this.getShort(true);
    }

    public short getShort(boolean signed) {
        return signed ? Binary.readSignedShort(this.get(2)) : Binary.readShort(this.get(2));
    }

    public void putShort(short s) {
        this.put(Binary.writeShort(s));
    }

    public float getFloat() {
        return Binary.readFloat(this.get(4));
    }

    public void putFloat(float v) {
        this.put(Binary.writeFloat(v));
    }

    public int getTriad() {
        return Binary.readTriad(this.get(3));
    }

    public void putTriad(int triad) {
        this.put(Binary.writeTriad(triad));
    }

    public int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }

    public void putLTriad(int triad) {
        this.put(Binary.writeLTriad(triad));
    }

    public byte getByte() {
        return this.buffer[this.offset++];
    }

    public void putByte(byte b) {
        this.put(new byte[]{b});
    }

    public byte[][] getDataArray() {
        return this.getDataArray(10);
    }

    public byte[][] getDataArray(int len) {
        byte[][] data = new byte[len][];
        for (int i = 0; i < len && !this.feof(); ++i) {
            data[i] = this.get(this.getTriad());
        }
        return data;
    }

    public void putDataArray() {
        this.putDataArray(new byte[0][]);
    }

    public void putDataArray(byte[][] data) {
        for (byte[] v : data) {
            this.putTriad(v.length);
            this.put(v);
        }
    }

    public void putUUID(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        int[] parts = new int[4];
        parts[0] = (int) (msb >> 32);
        parts[1] = (int) ((msb >> 16) & 0xFFFF);
        parts[2] = (int) ((msb >> 12) & 0xF);
        parts[3] = (int) (msb & 0xFFF);
        for (int i = 0; i < 4; i++) {
            this.putInt(parts[i]);
        }
    }

    public UUID getUUID() {
        int[] parts = new int[4];
        for (int i = 0; i < 4; i++) {
            parts[i] = this.getInt();
        }
        long msb = (parts[0] << 32) | ((parts[1] & 0xFFFF) << 16) | ((parts[2] & 0xF) << 12) | (parts[3] & 0xFFF);
        return new UUID(msb, new Random().nextLong());
    }

    public Item getSlot() {
        short id = this.getShort();
        byte cnt = this.getByte();
        return Item.get(id, (int) this.getShort(), cnt);
    }

    public void putSlot(Item item) {
        this.putShort((short) item.getId());
        this.putByte((byte) (item.getCount() & 0xff));
        this.putShort(item.getDamage());
    }

    public String getString() {
        return new String(this.get(this.getShort()), StandardCharsets.UTF_8);
    }

    public void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putShort((short) b.length);
        this.put(b);
    }

    public boolean feof() {
        return this.offset < 0 || this.offset >= this.buffer.length;
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buffer.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
            buffer = Arrays.copyOf(buffer, newCapacity);
        }
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
}
