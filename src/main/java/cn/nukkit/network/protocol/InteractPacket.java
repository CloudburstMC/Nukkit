package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-15.
 */
@ToString
public class InteractPacket extends DataPacket {

    public static final byte ACTION_VEHICLE_EXIT = 3;
    public static final byte ACTION_MOUSEOVER = 4;
    public static final byte ACTION_OPEN_NPC = 5;
    public static final byte ACTION_OPEN_INVENTORY = 6;

    public byte action;
    public long entityRuntimeId;
    public float x;
    public float y;
    public float z;

    @Override
    public byte pid() {
        return ProtocolInfo.INTERACT_PACKET;
    }

    @Override
    public void decode() {
        this.action = this.getByte();
        this.entityRuntimeId = this.getEntityRuntimeId();
        if (this.action == ACTION_MOUSEOVER) {
            this.x = this.getLFloat();
            this.y = this.getLFloat();
            this.z = this.getLFloat();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.action);
        this.putEntityRuntimeId(this.entityRuntimeId);
        if (this.action == ACTION_MOUSEOVER) {
            this.putLFloat(this.x);
            this.putLFloat(this.y);
            this.putLFloat(this.z);
        }
    }
}
