package cn.nukkit.network.protocol;

import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

@ToString
public class PositionTrackingDBServerBroadcastPacket extends DataPacket {

    public static final byte ACTION_UPDATE = 0;
    public static final byte ACTION_DESTROY = 1;
    public static final byte ACTION_NOT_FOUND = 2;

    public byte action;
    public int trackingId;
    public CompoundTag namedTag;

    @Override
    public byte pid() {
        return ProtocolInfo.POSITION_TRACKING_D_B_SERVER_BROADCAST_PACKET;
    }

    @Override
    public void decode() {
        this.action = this.getByte();
        this.trackingId = this.getVarInt();
        //TODO: this.namedTag
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.action);
        this.putVarInt(this.trackingId);
        //TODO: this.namedTag
    }
}
