package cn.nukkit.network.protocol;

public class ClientCacheStatusPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CLIENT_CACHE_STATUS_PACKET;

    public boolean supported;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.supported = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.supported);
    }
}
