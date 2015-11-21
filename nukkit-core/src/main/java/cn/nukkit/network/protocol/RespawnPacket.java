package cn.nukkit.network.protocol;

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
        this.x = getFloat();
        this.y = getFloat();
        this.z = getFloat();
    }

    @Override
    public void encode() {
        this.reset();
        this.putFloat(this.x);
        this.putFloat(this.y);
        this.putFloat(this.z);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
