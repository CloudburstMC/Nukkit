package cn.nukkit.network.protocol;

import cn.nukkit.Player;

/**
 * Created on 15-10-14.
 */
public class TakeItemEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TAKE_ITEM_ENTITY_PACKET;

    public long entityId;
    public long target;

    @Override
    public void decode() {
        this.target = this.getEntityRuntimeId();
        this.entityId = this.getEntityRuntimeId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.target);
        this.putEntityRuntimeId(this.entityId);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    protected void handle(Player player) {

    }
}
