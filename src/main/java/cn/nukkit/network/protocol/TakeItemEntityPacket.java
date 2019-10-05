package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created on 15-10-14.
 */
@ToString
public class TakeItemEntityPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.TAKE_ITEM_ENTITY_PACKET;

    public long entityId;
    public long target;

    @Override
    protected void decode(ByteBuf buffer) {
        this.target = Binary.readEntityRuntimeId(buffer);
        this.entityId = Binary.readEntityRuntimeId(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.target);
        Binary.writeEntityRuntimeId(buffer, this.entityId);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
