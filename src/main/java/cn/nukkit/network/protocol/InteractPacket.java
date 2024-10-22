package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class InteractPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INTERACT_PACKET;

    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;
    public static final int ACTION_OPEN_NPC = 5;
    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;
    float x;
    float y;
    float z;

    @Override
    public void decode() {
        this.action = this.getByte();
        this.target = this.getEntityRuntimeId();
        if (this.action == ACTION_MOUSEOVER || this.action == ACTION_VEHICLE_EXIT) {
            this.x = this.getFloat();
            this.y = this.getFloat();
            this.z = this.getFloat();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action);
        this.putEntityRuntimeId(this.target);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
