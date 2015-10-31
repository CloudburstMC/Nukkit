package cn.nukkit.raknet.protocol;

import cn.nukkit.utils.Binary;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Packet implements Cloneable {

    protected int offset = 0;
    public byte[] buffer;
    public Long sendTime;

    public abstract byte getID();

    protected byte[] get(int len) {
        if (len < 0) {
            this.offset = this.buffer.length - 1;
            return new byte[0];
        }

        byte[] buffer = new byte[len];
        for (int i = 0; i < len; i++) {
            buffer[i] = this.buffer[this.offset++];
        }
        return buffer;
    }

    protected byte[] getAll() {
        return this.get();
    }

    protected byte[] get() {
        return Arrays.copyOfRange(this.buffer, this.offset, this.buffer.length - 1);
    }

    protected long getLong() {
        return Binary.readLong(this.get(8));
    }

    protected int getInt() {
        return Binary.readInt(this.get(4));
    }

    protected short getShort() {
        return (short) this.getShort(true);
    }

    protected int getShort(boolean signed) {
        return signed ? Binary.readSignedShort(this.get(2)) : Binary.readShort(this.get(2));
    }

    protected int getTriad() {
        return Binary.readTriad(this.get(3));
    }

    protected int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }

    protected byte getByte() {
        return this.buffer[this.offset++];
    }

    protected String getString() {
        return new String(this.get(this.getShort()), StandardCharsets.UTF_8);
    }

    protected InetSocketAddress getAddress() {
        byte version = this.getByte();
        if (version == 4) {
            String addr = ((~this.getByte()) & 0xff) + "." + ((~this.getByte()) & 0xff) + "." + ((~this.getByte()) & 0xff) + "." + ((~this.getByte()) & 0xff);
            int port = this.getShort() & 0xffff;
            return new InetSocketAddress(addr, port);
        } else {
            //todo IPV6 SUPPORT
            return null;
        }
    }

    protected boolean feof() {
        return this.offset >= 0 && this.offset + 1 <= this.buffer.length;
    }

    protected void put(byte[] b) {
        this.buffer = Binary.appendBytes(this.buffer, b);
    }

    protected void putLong(long v) {
        this.put(Binary.writeLong(v));
    }

    protected void putInt(int v) {
        this.put(Binary.writeInt(v));
    }

    protected void putShort(short v) {
        this.put(Binary.writeShort(v));
    }

    protected void putTriad(int v) {
        this.put(Binary.writeTriad(v));
    }

    protected void putLTriad(int v) {
        this.put(Binary.writeLTriad(v));
    }

    protected void putByte(byte b) {
        byte[] newBytes = new byte[this.buffer.length + 1];
        System.arraycopy(this.buffer, 0, newBytes, 0, this.buffer.length);
        newBytes[this.buffer.length] = b;
        this.buffer = newBytes;
    }

    protected void putString(String str) {
        byte[] b = str.getBytes(StandardCharsets.UTF_8);
        this.putShort((short) b.length);
        this.put(b);
    }

    protected void putAddress(String addr, int port) {
        this.putAddress(addr, port, (byte) 4);
    }

    protected void putAddress(String addr, int port, byte version) {
        this.putByte(version);
        if (version == 4) {
            for (String b : addr.split("\\.")) {
                this.putByte((byte) ((~Integer.valueOf(b)) & 0xff));
            }
            this.putShort((short) port);
        } else {
            //todo ipv6
        }
    }

    protected void putAddress(InetSocketAddress address) {
        this.putAddress(address.getHostString(), address.getPort());
    }

    public void encode() {
        this.buffer = new byte[]{getID()};
    }

    public void decode() {
        this.offset = 1;
    }

    public Packet clean() {
        this.buffer = null;
        this.offset = 0;
        this.sendTime = null;
        return this;
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        Packet packet = (Packet) super.clone();
        packet.buffer = this.buffer.clone();
        return packet;
    }
}
