package cn.nukkit.raknet.protocol.packet;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UNCONNECTED_PING_OPEN_CONNECTIONS extends UNCONNECTED_PING {
    public static byte ID = (byte) 0x02;

    @Override
    public byte getID() {
        return ID;
    }
}
