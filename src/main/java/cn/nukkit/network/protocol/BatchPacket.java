package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BatchPacket extends DataPacket {

    public byte[] payload;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("BATCH_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.payload = this.get();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
