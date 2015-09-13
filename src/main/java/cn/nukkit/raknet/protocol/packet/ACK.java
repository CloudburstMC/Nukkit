package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.protocol.AcknowledgePacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ACK extends AcknowledgePacket {

    public static byte ID = (byte) 0xc0;

    @Override
    public byte getID() {
        return ID;
    }
}
