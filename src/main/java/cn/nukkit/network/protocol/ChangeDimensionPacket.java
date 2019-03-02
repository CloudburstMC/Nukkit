package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.network.protocol in project nukkit .
 */
public class ChangeDimensionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CHANGE_DIMENSION_PACKET;

    public int dimension;

    public float x;
    public float y;
    public float z;

    public boolean respawn;

    @Override
    public void decode() {
        this.dimension = this.getVarInt();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.respawn = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.dimension);
        this.putVector3f(this.x, this.y, this.z);
        this.putBoolean(this.respawn);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
