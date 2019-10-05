package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ContainerOpenPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.CONTAINER_OPEN_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public int type;
    public int x;
    public int y;
    public int z;
    public long entityId = -1;

    @Override
    protected void decode(ByteBuf buffer) {
        this.windowId = buffer.readUnsignedByte();
        this.type = buffer.readUnsignedByte();
        BlockVector3 v = Binary.readBlockVector3(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.entityId = Binary.readEntityUniqueId(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte(this.windowId);
        buffer.writeByte(this.type);
        Binary.writeBlockVector3(buffer, this.x, this.y, this.z);
        Binary.writeEntityUniqueId(buffer, this.entityId);
    }
}
