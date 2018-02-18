package cn.nukkit.network.protocol;

/**
 * Created by CreeperFace on 5.3.2017.
 */
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("MAP_INFO_REQUEST_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        mapId = this.getEntityUniqueId();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
