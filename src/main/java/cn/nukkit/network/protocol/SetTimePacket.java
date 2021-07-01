package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetTimePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_TIME_PACKET;

    public int time;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.time);
    }

}
