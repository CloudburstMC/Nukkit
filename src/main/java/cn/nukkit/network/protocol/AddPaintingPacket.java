package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class AddPaintingPacket extends DataPacket {

    public long entityUniqueId;
    public long entityRuntimeId;
    public Vector3f position;
    public int direction;
    public String title;

    @Override
    public byte pid() {
        return ProtocolInfo.ADD_PAINTING_PACKET;
    }

    @Override
    public void decode() {
    	this.entityUniqueId = this.getEntityUniqueId();
		this.entityRuntimeId = this.getEntityRuntimeId();
		this.position = this.getVector3f();
		this.direction = this.getVarInt();
		this.title = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVector3f(this.position);
        this.putVarInt(this.direction);
        this.putString(this.title);
    }
}
