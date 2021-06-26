package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ClientCacheMissResponsePacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.CLIENT_CACHE_BLOB_STATUS_PACKET;
    }

    @Override
    public void decode() {
    	//TODO
    }

    @Override
    public void encode() {
    	//TODO
    }
}
