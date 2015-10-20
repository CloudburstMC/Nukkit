package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.utils.Binary;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

/**
 * @author Nukkit Project Team
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

    @Deprecated
    public DataPacket setChannel(int channel) {
        this.channel = channel;
        return this;
    }

    public int getChannel() {
        return channel;
    }

    public DataPacket clean() {
        this.setBuffer(null);

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
