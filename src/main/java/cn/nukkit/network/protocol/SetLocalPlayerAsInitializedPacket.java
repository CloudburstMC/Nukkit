package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetLocalPlayerAsInitializedPacket extends DataPacket {

    public long entityRuntimeId;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
    }

    @Override
    public void encode() {
    	this.restore()
        this.putEntityRuntimeId(this.entityRuntimeId);
    }
}
