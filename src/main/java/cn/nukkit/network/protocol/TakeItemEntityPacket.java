package cn.nukkit.network.protocol;

/**
 * Created on 15-10-14.
 */
public class TakeItemEntityPacket extends DataPacket {

    public long entityId;
    public long target;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("TAKE_ITEM_ENTITY_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.target = this.getEntityRuntimeId();
        this.entityId = this.getEntityRuntimeId();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityRuntimeId(this.target);
        this.putEntityRuntimeId(this.entityId);
    }

}
