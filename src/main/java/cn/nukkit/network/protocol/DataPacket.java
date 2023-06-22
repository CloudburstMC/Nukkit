package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.network.Network;
import cn.nukkit.utils.BinaryStream;
import com.nukkitx.network.raknet.RakNetReliability;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket extends BinaryStream implements Cloneable {

    public volatile boolean isEncoded = false;
    private int channel = 0;

    public RakNetReliability reliability = RakNetReliability.RELIABLE_ORDERED;

    public abstract byte pid();

    public abstract void decode();

    public abstract void encode();

    public final void tryEncode() {
        if (!this.isEncoded) {
            this.isEncoded = true;
            this.encode();
        }
    }

    @Override
    public DataPacket reset() {
        super.reset();

        byte packetId = this.pid();
        if (packetId < 0 && packetId >= -56) { // Hack: (byte) 200+ --> (int) 300+
            this.putUnsignedVarInt(packetId + 356);
        } else {
            this.putUnsignedVarInt(packetId & 0xff);
        }

        return this;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getChannel() {
        return channel;
    }

    public DataPacket clean() {
        this.setBuffer(null);
        this.setOffset(0);
        this.isEncoded = false;
        return this;
    }

    @Override
    public DataPacket clone() {
        try {
            DataPacket packet = (DataPacket) super.clone();
            packet.setBuffer(this.getBuffer()); // prevent reflecting same buffer instance
            packet.offset = this.offset;
            packet.count = this.count;
            return packet;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public BatchPacket compress() {
        return compress(Server.getInstance().networkCompressionLevel);
    }

    public BatchPacket compress(int level) {
        BinaryStream stream = new BinaryStream();
        byte[] buf = this.getBuffer();
        stream.putUnsignedVarInt(buf.length);
        stream.put(buf);
        try {
            BatchPacket batched = new BatchPacket();
            batched.payload = Network.deflateRaw(stream.getBuffer(), level);
            return batched;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
