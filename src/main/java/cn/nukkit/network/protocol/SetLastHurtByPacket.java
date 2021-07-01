package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetLastHurtByPacket extends DataPacket {

	public int entityType;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_LAST_HURT_BY_PACKET;
    }

    @Override
    public void decode() {
    	this.entityType = this.getVarInt();
    }

    @Override
    public void encode() {
    	this.reset();
    	this.putVarInt(this.entityType);
    }
}
