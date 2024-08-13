package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.network.protocol in project nukkit .
 */
@ToString
public class ChangeDimensionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CHANGE_DIMENSION_PACKET;

    public int dimension;

    public float x;
    public float y;
    public float z;

    public boolean respawn;

    public Integer loadingScreenId = null;

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.dimension);
        this.putVector3f(this.x, this.y, this.z);
        this.putBoolean(this.respawn);
        this.putBoolean(this.loadingScreenId != null);
        if (this.loadingScreenId != null) {
            this.putLInt(this.loadingScreenId);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
