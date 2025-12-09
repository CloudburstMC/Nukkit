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
        if (this.getBoolean()) {
            this.x = this.getFloat();
            this.y = this.getFloat();
            this.z = this.getFloat();
        }
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
