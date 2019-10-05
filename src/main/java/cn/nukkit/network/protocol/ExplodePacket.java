package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ExplodePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.EXPLODE_PACKET;

    public float x;
    public float y;
    public float z;
    public float radius;
    public Vector3[] records = new Vector3[0];

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeVarInt(buffer, (int) (this.radius * 32));
        Binary.writeUnsignedVarInt(buffer, this.records.length);
        if (this.records.length > 0) {
            for (Vector3 record : records) {
                Binary.writeSignedBlockPosition(buffer, record.asBlockVector3());
            }
        }
    }

}
