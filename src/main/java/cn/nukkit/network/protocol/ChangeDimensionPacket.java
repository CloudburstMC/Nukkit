package cn.nukkit.network.protocol;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.network.protocol in project nukkit .
 */
public class ChangeDimensionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CHANGE_DIMENSION_PACKET;

    public byte dimension;

    public float x;
    public float y;
    public float z;

    public boolean unknown;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(dimension);
        this.putVector3f(x, y, z);
        this.putBoolean(unknown);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
