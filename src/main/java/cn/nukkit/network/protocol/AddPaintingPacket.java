package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AddPaintingPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ADD_PAINTING_PACKET;

    public long entityUniqueId;
    public long entityRuntimeId;
    public int x;
    public int y;
    public int z;
    public int direction;
    public String title;

    @Override
    public void decode() {
        this.entityUniqueId = this.getEntityUniqueId();
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.x = this.getVarInt();
        this.y = (int) this.getUnsignedVarInt();
        this.z = this.getVarInt();
        this.direction = this.getVarInt();
        this.title = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.direction);
        this.putString(this.title);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
