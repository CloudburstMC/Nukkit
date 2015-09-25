package cn.nukkit.network.protocol;

import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetTimePacket extends DataPacket {
    public static final byte NETWORK_ID = Info.SET_TIME_PACKET;

    public int time;
    public boolean started = true;

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
        this.putInt((this.time / Level.TIME_FULL) * 19200);
        this.putByte((byte) (this.started ? 0x80 : 0x00));
    }

}
