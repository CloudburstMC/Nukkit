package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created by CreeperFace on 5.3.2017.
 */
@ToString
public class MapInfoRequestPacket extends DataPacket {

    public long mapUniqueId;

    @Override
    public byte pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.mapUniqueId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.mapUniqueId);
    }
}
