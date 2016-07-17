package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.protocol.Packet;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CLIENT_CONNECT_DataPacket extends Packet {
    public static final byte ID = (byte) 0x09;

    @Override
    public byte getID() {
        return ID;
    }

    public long clientID;
    public long sendPing;
    public boolean useSecurity = false;

    @Override
    public void encode() {
        super.encode();
        this.putLong(this.clientID);
        this.putLong(this.sendPing);
        this.putByte((byte) (this.useSecurity ? 1 : 0));
    }

    @Override
    public void decode() {
        super.decode();
        this.clientID = this.getLong();
        this.sendPing = this.getLong();
        this.useSecurity = this.getByte() > 0;
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new CLIENT_CONNECT_DataPacket();
        }

    }
}
