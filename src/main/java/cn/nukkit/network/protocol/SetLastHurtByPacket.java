package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetLastHurtByPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_LAST_HURT_BY_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }
}
