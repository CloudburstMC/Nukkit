package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerOpenPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_OPEN_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public byte windowid;
    public byte type;
    public int slots;
    public int x;
    public int y;
    public int z;
    public long entityId = -1;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowid);
        this.putByte(this.type);
        this.putShort(this.slots);
        this.putInt(this.x);
        this.putInt(this.y);
        this.putInt(this.z);
        this.putLong(this.entityId);
    }
}
