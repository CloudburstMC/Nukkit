package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;

public class MapCreateLockedCopyPacket extends DataPacket {

    public long originalMapId;
    public long newMapId;

    @Override
    public short pid() {
        return ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.originalMapId = Binary.readVarLong(buffer);
        this.newMapId = Binary.readVarLong(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarLong(buffer, this.originalMapId);
        Binary.writeVarLong(buffer, this.newMapId);
    }
}
