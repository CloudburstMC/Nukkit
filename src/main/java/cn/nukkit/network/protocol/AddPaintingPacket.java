package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class AddPaintingPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.ADD_PAINTING_PACKET;

    public long entityUniqueId;
    public long entityRuntimeId;
    public float x;
    public float y;
    public float z;
    public int direction;
    public String title;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.entityUniqueId);
        Binary.writeEntityRuntimeId(buffer, this.entityRuntimeId);

        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeVarInt(buffer, this.direction);
        Binary.writeString(buffer, this.title);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
