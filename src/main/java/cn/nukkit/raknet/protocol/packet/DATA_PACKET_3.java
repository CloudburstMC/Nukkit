package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.protocol.DataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DATA_PACKET_3 extends DataPacket {
    public static byte ID = (byte) 0x83;

    @Override
    public byte getID() {
        return ID;
    }

}
