package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.protocol.Packet;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PONG_DataPacket extends Packet {
    public static byte ID = (byte) 0x03;

    @Override
    public byte getID() {
        return ID;
    }

    public long pongID;

    @Override
    public void encode() {
        super.encode();
        this.putLong(this.pongID);
    }

    @Override
    public void decode() {
        super.decode();
        this.pongID = this.getLong();
    }
}
