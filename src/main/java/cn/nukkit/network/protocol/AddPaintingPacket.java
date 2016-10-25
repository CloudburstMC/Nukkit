package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AddPaintingPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ADD_PAINTING_PACKET;

    public long eid;
    public int x;
    public int y;
    public int z;
    public int direction;
    public String title;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityId(eid);
        this.putBlockCoords(x, y, z);
        this.putInt(direction);
        this.putString(title);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
