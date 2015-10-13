package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.utils.Binary;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket implements Cloneable {

    public int offset = 0;
    public byte[] buffer = new byte[0];
    public boolean isEncoded = false;
    private int channel = 0;
    public EncapsulatedPacket encapsulatedPacket;
    public byte reliability;
    public Integer orderIndex = null;
    public Integer orderChannel = null;

    public abstract byte pid();

    public abstract void encode();

    public abstract void decode();

    protected void reset() {
        this.buffer = new byte[]{this.pid()};
        this.offset = 0;
    }

    public DataPacket setChannel(int channel) {
        this.channel = channel;
        return this;
    }

    public int getChannel() {
        return channel;
    }

    public void setBuffer() {
        this.setBuffer(null);
    }

    public void setBuffer(byte[] buffer) {
        this.setBuffer(buffer, 0);
    }

    public void setBuffer(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    protected byte[] get() {
        return Binary.subBytes(this.buffer, this.offset);
    }

    protected byte[] get(int len) {
        if (len < 0) {
            this.offset = this.buffer.length - 1;
            return new byte[0];
        }

        return Binary.subBytes(this.buffer, (this.offset += len) - len, len);
    }

    protected void put(byte[] bytes) {
        this.buffer = Binary.appendBytes(this.buffer, bytes);
    }

    protected long getLong() {
        return Binary.readLong(this.get(8));
    }

    protected void putLong(long l) {
        this.put(Binary.writeLong(l));
    }

    protected int getInt() {
        return Binary.readInt(this.get(4));
    }

    protected void putInt(int i) {
        this.put(Binary.writeInt(i));
    }

    protected short getShort() {
        return this.getShort(true);
    }

    protected short getShort(boolean signed) {
        return signed ? Binary.readSignedShort(this.get(2)) : Binary.readShort(this.get(2));
    }

    protected void putShort(short s) {
        this.put(Binary.writeShort(s));
    }

    protected float getFloat() {
        return Binary.readFloat(this.get(4));
    }

    protected void putFloat(float v) {
        this.put(Binary.writeFloat(v));
    }

    protected int getTriad() {
        return Binary.readTriad(this.get(3));
    }

    protected void putTriad(int triad) {
        this.put(Binary.writeTriad(triad));
    }

    protected int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }

    protected void putLTriad(int triad) {
        this.put(Binary.writeLTriad(triad));
    }

    protected byte getByte() {
        return this.buffer[this.offset++];
    }

    protected void putByte(byte b) {
        this.put(new byte[]{b});
    }

    protected byte[][] getDataArray() {
        return this.getDataArray(10);
    }

    protected byte[][] getDataArray(int len) {
        byte[][] data = new byte[len][];
        for (int i = 0; i < len && !this.feof(); ++i) {
            data[i] = this.get(this.getTriad());
        }
        return data;
    }

    protected void putDataArray() {
        this.putDataArray(new byte[0][]);
    }

    protected void putDataArray(byte[][] data) {
        for (byte[] v : data) {
            this.putTriad(v.length);
            this.put(v);
        }
    }

    protected void putUUID(UUID uuid) {
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

    protected UUID getUUID() {
        int[] parts = new int[4];
        for (int i = 0; i < 4; i++) {
            parts[i] = this.getInt();
        }
        long msb = (parts[0] << 32) | ((parts[1] & 0xFFFF) << 16) | ((parts[2] & 0xF) << 12) | (parts[3] & 0xFFF);
        return new UUID(msb, new Random().nextLong());
    }

    protected Item getSlot() {
        short id = this.getShort();
        byte cnt = this.getByte();
        return Item.get(id, (int) this.getShort(), cnt);
    }

    protected void putSlot(Item item) {
        this.putShort((short) item.getId());
        this.putByte((byte) (item.getCount() & 0xff));
        this.putShort((short) item.getDamage());
    }

    protected String getString() {
        return new String(this.get(this.getShort()), StandardCharsets.UTF_8);
    }

    protected void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putShort((short) b.length);
        this.put(b);
    }

    protected boolean feof() {
        return this.offset < 0 || this.offset >= this.buffer.length;
    }

    public DataPacket clean() {
        this.buffer = null;
        this.isEncoded = false;
        this.offset = 0;
        return this;
    }

    @Override
    public DataPacket clone() {
        try {
            return (DataPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
