package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EntityPickRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;

    public long entityId;
    public int hotbarSlot;
    public boolean withData;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityId = this.getLLong();
        this.hotbarSlot = this.getByte();
        this.withData = this.getBoolean();
    }

    @Override
    public void encode() {

    }
}
