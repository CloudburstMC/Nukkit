package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetTimePacket extends DataPacket {

    public int time;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_TIME_PACKET;
    }

    @Override
    public void decode() {
    	this.time = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.time);
    }
}
