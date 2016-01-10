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

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(dimension);
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        this.putByte((byte) 0);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
