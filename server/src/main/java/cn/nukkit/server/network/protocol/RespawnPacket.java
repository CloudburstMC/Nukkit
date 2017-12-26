package cn.nukkit.server.network.protocol;

import cn.nukkit.server.math.Vector3;

/**
 * @author Nukkit Project Team
 */
public class RespawnPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESPAWN_PACKET;

    public float x;
    public float y;
    public float z;

    @Override
    public void decode() {
        Vector3 v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.x, this.y, this.z);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
