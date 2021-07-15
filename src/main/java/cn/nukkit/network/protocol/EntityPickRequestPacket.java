package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EntityPickRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;

    public long entityRuntimeId;
    public byte hotbarSlot;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.hotbarSlot = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte(this.hotbarSlot);
    }
}
