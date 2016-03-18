package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.protocol.Packet;

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
    
    public static final class Factory implements Packet.PacketFactory
    {

		@Override
		public Packet create() {
			return new UNCONNECTED_PING_OPEN_CONNECTIONS();
		}
    	
    }
}
