package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetLocalPlayerAsInitializedPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;

    public long entityRuntimeId;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;
    }

    @Override
    public void decode() {
        $this->entityRuntimeId = $this->getEntityRuntimeId();
    }

    @Override
    public void encode() {
    	this.restore()
        $this->putEntityRuntimeId($this->entityRuntimeId);
    }
}
