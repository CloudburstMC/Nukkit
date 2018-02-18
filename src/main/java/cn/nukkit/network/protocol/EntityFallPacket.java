package cn.nukkit.network.protocol;

public class EntityFallPacket extends DataPacket {

    public long eid;
    public float fallDistance;
    public boolean unknown;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("ENTITY_FALL_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.eid = this.getEntityRuntimeId();
        this.fallDistance = this.getLFloat();
        this.unknown = this.getBoolean();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
