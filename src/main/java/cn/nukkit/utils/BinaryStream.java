package cn.nukkit.utils;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BinaryStream {
    public static final int DEFAULT_BLOCK_SIZE = 1024;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public int offset;
    private byte[] buffer;
    private ArrayDeque<byte[]> buffers = new ArrayDeque<>();
    private final byte[] shortBuffer = new byte[2];
    private final byte[] intBuffer = new byte[4];
    private final byte[] longBuffer = new byte[8];
    private int blockSize;
    private int count;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public BinaryStream() {
        this.buffer = new byte[32];
    }

    public BinaryStream(byte[] buffer) {
        this(buffer, 0);
    }

    public BinaryStream(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
    }

    public void reset() {
        setBuffer(new byte[32]);
    }

    public void setBuffer(byte[] buffer) {
        if (buffers.isEmpty()) {
            buffers.clear();
        }
        this.buffer = buffer;
    }

    public void setBuffer(byte[] buffer, int offset) {
        this.setBuffer(buffer);
        this.setOffset(offset);
    }

    private void addBuffer() {
        buffers.addLast(buffer);
        buffer = new byte[blockSize];
        count += offset;
        offset = 0;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[][] getBuffers() {
        if (offset > 0) {
            byte[] buf2 = new byte[offset];
            System.arraycopy(buffer, 0, buf2, 0, offset);
            buffers.addLast(buf2);
            count += offset;
            offset = 0;
        }
        byte[][] res = new byte[buffers.size()][];
        int i = 0;
        for (byte[] bytes : buffers) {
            res[i++] = bytes;
        }
        return res;
    }

    public byte[] getBuffer() {
        if (buffers.size() < 8) {
            if (buffers.isEmpty()) {
                buffer = Arrays.copyOfRange(buffer, 0, offset);
                return buffer;
            }
            byte[] data = new byte[getCount()];

            // Check if we have a list of buffers
            int pos = 0;

            if (buffers != null) {
                for (byte[] bytes : buffers) {
                    System.arraycopy(bytes, 0, data, pos, bytes.length);
                    pos += bytes.length;
                }
            }

            // write the internal buffer directly
            System.arraycopy(buffer, 0, data, pos, offset);

            this.buffer = data;
            this.buffers.clear();
            return this.buffer;
        }
        final byte[] data = new byte[getCount()];
        // Check if we have a list of buffers
        int pos = 0;
        int count = 0;
        if (buffers != null) {
            for (final byte[] bytes : buffers) {
                final int finalPos = pos;
                count++;
                POOL.submit(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        System.arraycopy(bytes, 0, data, finalPos, bytes.length);
                        return null;
                    }
                });
                pos += bytes.length;
            }
        }
        POOL.awaitQuiescence(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        // write the internal buffer directly
        System.arraycopy(buffer, 0, data, pos, offset);

        this.buffer = data;
        this.buffers.clear();
        return this.buffer;
    }

    public int getCount() {
        return count + offset;
    }

    public byte[] get() {
        return this.get(this.buffer.length - this.offset);
    }

    public byte[] get(int len) {
        if (len < 0) {
            this.offset = this.buffer.length - 1;
            return new byte[0];
        }
        len = Math.min(len, this.buffer.length - this.offset);
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public byte[] get(int len, byte[] useBuffer) {
        if (len < 0) {
            this.offset = this.buffer.length - 1;
            return new byte[0];
        }
        len = Math.min(len, this.buffer.length - this.offset);
        this.offset += len;
        System.arraycopy(this.buffer, this.offset - len, useBuffer, 0, len);
        return useBuffer;
    }

    public void put(byte[] b) {
        if (b.length > blockSize) {
            if (offset > 0) {
                byte[] buf2 = new byte[offset];
                System.arraycopy(buffer, 0, buf2, 0, offset);
                buffer = buf2;
                addBuffer();
            }
            count += b.length;
            buffers.addLast(b);
        } else {
            put(b, 0, b.length);
        }
    }

    public void put(int datum) {
        if (offset == blockSize) {
            addBuffer();
        }
        // store the byte
        buffer[offset++] = (byte) datum;
    }

    public void put(byte[] data, int offset, int length) {
        if ((offset < 0) || ((offset + length) > data.length) || (length < 0)) {
            throw new IndexOutOfBoundsException();
        } else {
            if ((offset + length) > blockSize) {
                int copyLength;

                do {
                    if (offset == blockSize) {
                        addBuffer();
                    }

                    copyLength = blockSize - offset;

                    if (length < copyLength) {
                        copyLength = length;
                    }

                    System.arraycopy(data, offset, buffer, offset, copyLength);
                    offset += copyLength;
                    offset += copyLength;
                    length -= copyLength;
                } while (length > 0);
            } else {
                // Copy in the subarray
                System.arraycopy(data, offset, buffer, offset, length);
                offset += length;
            }
        }
    }

    public long getLong() {
        return Binary.readLong(this.get(8, longBuffer));
    }

    public void putLong(long l) {
        this.put(Binary.writeLong(l));
    }

    public int getInt() {
        return Binary.readInt(this.get(4, intBuffer));
    }

    public void putInt(int i) {
        this.put(Binary.writeInt(i));
    }

    public long getLLong() {
        return Binary.readLLong(this.get(8, longBuffer));
    }

    public void putLLong(long l) {
        this.put(Binary.writeLLong(l));
    }

    public int getLInt() {
        return Binary.readLInt(this.get(4, intBuffer));
    }

    public void putLInt(int i) {
        this.put(Binary.writeLInt(i));
    }

    public int getShort() {
        return Binary.readShort(this.get(2, shortBuffer));
    }

    public void putShort(int s) {
        this.put(Binary.writeShort(s));
    }

    public short getSignedShort() {
        return Binary.readSignedShort(this.get(2, shortBuffer));
    }

    public void putSignedShort(short s) {
        this.put(Binary.writeShort(s));
    }

    public int getLShort() {
        return Binary.readLShort(this.get(2, shortBuffer));
    }

    public void putLShort(int s) {
        this.put(Binary.writeLShort(s));
    }

    public short getSignedLShort() {
        return Binary.readSignedLShort(this.get(2, shortBuffer));
    }

    public void putSignedLShort(short s) {
        this.put(Binary.writeLShort(s));
    }

    public float getFloat() {
        return Binary.readFloat(this.get(4, intBuffer));
    }

    public void putFloat(float v) {
        this.put(Binary.writeFloat(v));
    }

    public float getLFloat() {
        return Binary.readLFloat(this.get(4, intBuffer));
    }

    public void putLFloat(float v) {
        this.put(Binary.writeLFloat(v));
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

    public byte getSignedByte() {
        return this.buffer[this.offset++];
    }

    public boolean getBoolean() {
        return this.getByte() == 0x01;
    }

    public void putBoolean(boolean bool) {
        this.putByte((byte) (bool ? 1 : 0));
    }

    public int getByte() {
        return this.buffer[this.offset++] & 0xff;
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

    public void putDataArray(byte[][] data) {
        for (byte[] v : data) {
            this.putTriad(v.length);
            this.put(v);
        }
    }

    public void putUUID(UUID uuid) {
        this.put(Binary.writeUUID(uuid));
    }

    public UUID getUUID() {
        return Binary.readUUID(this.get(16));
    }

    public void putSkin(Skin skin) {
        this.putString(skin.getModel());
        this.putShort(skin.getData().length);
        this.put(skin.getData());
    }

    public Skin getSkin() {
        String modelId = this.getString();
        byte[] skinData = this.get(this.getShort());
        return new Skin(skinData, modelId);
    }

    public Item getSlot() {
        short id = this.getSignedShort();

        if (id <= 0) {
            return Item.get(0, 0, 0);
        }
        int cnt = this.getByte();

        int data = this.getShort();

        int nbtLen = this.getLShort();

        byte[] nbt = new byte[0];
        if (nbtLen > 0) {
            nbt = this.get(nbtLen);
        }

        return Item.get(
                id, data, cnt, nbt
        );
    }

    public void putSlot(Item item) {
        if (item == null || item.getId() == 0) {
            this.putShort(0);
            return;
        }

        this.putShort(item.getId());
        this.putByte((byte) (item.getCount() & 0xff));
        this.putShort(!item.hasMeta() ? -1 : item.getDamage());

        byte[] nbt = item.getCompoundTag();
        this.putLShort(nbt.length);
        this.put(nbt);
    }

    public String getString() {
        return new String(this.get(this.getShort()), StandardCharsets.UTF_8);
    }

    public void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putShort(b.length);
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
        }
        this.buffer = Arrays.copyOf(buffer, newCapacity);
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
