package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DataPacket extends BinaryStream implements Cloneable {

    public boolean isEncoded = false;
    private int channel = 0;

    public EncapsulatedPacket encapsulatedPacket;
    public byte reliability;
    public Integer orderIndex = null;
    public Integer orderChannel = null;

    public abstract byte pid();

    public abstract void decode();

    public abstract void encode();

    protected abstract void handle(Player player);

    public final void doHandle(Player player)   {
        if (!player.isConnected())  {
            return;
        }

        DataPacketReceiveEvent ev = new DataPacketReceiveEvent(player, this);
        player.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        this.handle(player);
    }

    @Override
    public DataPacket reset() {
        super.reset();
        this.putByte(this.pid());
        this.putShort(0);
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
            return (DataPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public BatchPacket compress() {
        return compress(Server.getInstance().networkCompressionLevel);
    }

    public BatchPacket compress(int level) {
        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        byte[] data = Binary.appendBytes(batchPayload);
        try {
            batch.payload = Zlib.deflate(data, level);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }
}
