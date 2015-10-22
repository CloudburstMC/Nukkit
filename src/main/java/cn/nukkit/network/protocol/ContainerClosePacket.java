package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class ContainerClosePacket extends DataPacket {

    public static final byte NETWORK_ID = Info.CONTAINER_CLOSE_PACKET;

    public int windowId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
    }
}
