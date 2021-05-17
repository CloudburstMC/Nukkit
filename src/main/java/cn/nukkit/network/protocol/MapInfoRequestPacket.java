package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author CreeperFace
 * @since 5.3.2017
 */
@ToString
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public byte pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        mapId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {

    }
}
