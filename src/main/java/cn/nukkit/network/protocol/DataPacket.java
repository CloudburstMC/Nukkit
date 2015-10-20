package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public abstract class DataPacket extends BinaryStream {

    public boolean isEncoded = false;
    private int channel = 0;

    public EncapsulatedPacket encapsulatedPacket;
    public byte reliability;
    public Integer orderIndex = null;
    public Integer orderChannel = null;

    public abstract byte pid();

    public abstract void encode();

    public abstract void decode();

    @Override
    public void reset() {
        super.reset();
        putByte(pid());
    }

    public DataPacket setChannel(int channel) {
        this.channel = channel;
        return this;
    }

    public int getChannel() {
        return channel;
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
