package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created on 15-10-15.
 */
@ToString
public class InteractPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.INTERACT_PACKET;

    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;

    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;

    @Override
    protected void decode(ByteBuf buffer) {
        this.action = buffer.readByte();
        this.target = Binary.readEntityRuntimeId(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte((byte) this.action);
        Binary.writeEntityRuntimeId(buffer, this.target);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
