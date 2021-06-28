package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ClientCacheStatusPacket extends DataPacket {

    public boolean supported;

    @Override
    public byte pid() {
        return ProtocolInfo.CLIENT_CACHE_STATUS_PACKET;
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
