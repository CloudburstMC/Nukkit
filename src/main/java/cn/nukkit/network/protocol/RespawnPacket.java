package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class RespawnPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESPAWN_PACKET;

    public float x;
    public float y;
    public float z;
    public int respawnState;
    public long unknownEntityId; //???

    @Override
    public void decode() {
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.respawnState = this.getByte();
        this.unknownEntityId = this.getEntityRuntimeId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.x, this.y, this.z);
        this.putByte((byte) respawnState);
        this.putEntityRuntimeId(unknownEntityId);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
