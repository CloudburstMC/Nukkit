package cn.nukkit.network.protocol;

public class EntityFallPacket extends DataPacket {

    public long eid;
    public float fallDistance;
    public boolean unknown;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.eid = this.getEntityRuntimeId();
        this.fallDistance = this.getLFloat();
        this.unknown = this.getBoolean();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.ENTITY_FALL_PACKET :
                ProtocolInfo.ENTITY_FALL_PACKET;
    }
}
