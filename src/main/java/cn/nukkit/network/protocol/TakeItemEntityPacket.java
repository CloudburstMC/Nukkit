package cn.nukkit.network.protocol;

/**
 * Created on 15-10-14.
 */
public class TakeItemEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TAKE_ITEM_ENTITY_PACKET;

    public long entityId;
    public long target;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        reset();
        putEntityId(target);
        putEntityId(entityId);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
