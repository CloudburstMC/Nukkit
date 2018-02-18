package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AddPaintingPacket extends DataPacket {

    public long entityUniqueId;
    public long entityRuntimeId;
    public int x;
    public int y;
    public int z;
    public int direction;
    public String title;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("ADD_PAINTING_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.direction);
        this.putString(this.title);
    }

}
