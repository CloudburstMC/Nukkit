package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerClosePacket extends DataPacket {
    public static final byte NETWORK_ID = Info.CONTAINER_OPEN_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public byte windowid;

    @Override
    public void decode() {
        this.windowid = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowid);
    }
}
