package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelEventPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.LEVEL_EVENT_PACKET;

    public short evid;
    public float x;
    public float y;
    public float z;
    public int data;

    @Override
    public byte pid() {
        return 0;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putShort(this.evid);
        this.putFloat(this.x);
        this.putFloat(this.y);
        this.putFloat(this.z);
        this.putInt(this.data);
    }
}
