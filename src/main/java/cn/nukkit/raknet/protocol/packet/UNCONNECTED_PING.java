package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.RakNet;
import cn.nukkit.raknet.protocol.Packet;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UNCONNECTED_PING extends Packet {
    public static byte ID = (byte) 0x01;

    @Override
    public byte getID() {
        return ID;
    }

    public long pingID;

    @Override
    public void encode() {
        super.encode();
        this.putLong(this.pingID);
        this.put(RakNet.MAGIC);
    }

    @Override
    public void decode() {
        super.decode();
        this.pingID = this.getLong();
    }
    
    public static final class Factory implements Packet.PacketFactory
    {

		@Override
		public Packet create() {
			return new UNCONNECTED_PING();
		}
    	
    }
}
