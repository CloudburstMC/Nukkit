package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BatchPacket extends DataPacket {

    public byte[] payload;

    @Override
    public byte pid() {
        return ProtocolInfo.BATCH_PACKET;
    }

    @Override
    public void decode() {
        this.payload = this.get();
    }

    @Override
    public void encode() {
        //TODO
    }

    public void trim() {
        setBuffer(null);
    }
}
