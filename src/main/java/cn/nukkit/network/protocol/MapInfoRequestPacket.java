package cn.nukkit.network.protocol;

/**
 * Created by CreeperFace on 5.3.2017.
 */
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.MAP_INFO_REQUEST_PACKET :
                ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        mapId = this.getEntityUniqueId();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
