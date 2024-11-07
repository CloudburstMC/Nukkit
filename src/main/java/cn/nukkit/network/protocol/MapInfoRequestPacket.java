package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created by CreeperFace on 5.3.2017.
 */
@ToString
public class MapInfoRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MAP_INFO_REQUEST_PACKET;

    public long mapId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        mapId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }
}
