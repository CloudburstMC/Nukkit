package cn.nukkit.raknet.protocol.packet;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ADVERTISE_SYSTEM extends UNCONNECTED_PONG {
    public static byte ID = (byte) 0x1d;

    @Override
    public byte getID() {
        return ID;
    }
}
