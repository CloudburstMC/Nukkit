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
        this.x = getLFloat();
        this.y = getLFloat();
        this.z = getLFloat();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLFloat(this.x);
        this.putLFloat(this.y);
        this.putLFloat(this.z);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
