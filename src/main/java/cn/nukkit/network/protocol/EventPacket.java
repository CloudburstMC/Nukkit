package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class EventPacket extends DataPacket {

    public long eid;
    public int unknown1;
    public byte unknown2;

    @Override
    public short pid() {
        return ProtocolInfo.EVENT_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarLong(buffer, this.eid);
        Binary.writeVarInt(buffer, this.unknown1);
        buffer.writeByte(this.unknown2);
    }
}
