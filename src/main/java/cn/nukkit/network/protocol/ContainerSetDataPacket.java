package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class ContainerSetDataPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.CONTAINER_SET_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public byte windowId;
    public int property;
    public int value;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putShort(this.property);
        this.putShort(this.value);
    }

}
