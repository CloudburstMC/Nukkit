package cn.nukkit.network.protocol;

/**
 * Created on 2016/1/6 by xtypr.
 * Package cn.nukkit.network.protocol in project nukkit .
 * todo verify
 */
public class TelemetryEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TELEMETRY_EVENT_PACKET;

    public long entityId;
    public int unknown1; //always 0x00000003?
    public int fromDimension; //?
    public int toDimension; //?

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(entityId);
        this.putInt(unknown1);
        this.putInt(fromDimension);
        this.putInt(toDimension);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
