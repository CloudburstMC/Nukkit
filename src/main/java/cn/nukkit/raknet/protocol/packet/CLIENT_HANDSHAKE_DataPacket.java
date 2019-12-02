package cn.nukkit.raknet.protocol.packet;

import cn.nukkit.raknet.protocol.Packet;

import java.net.InetSocketAddress;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CLIENT_HANDSHAKE_DataPacket extends Packet {
    public static final byte ID = (byte) 0x13;
    public final InetSocketAddress[] systemAddresses = new InetSocketAddress[10];
    public String address;
    public int port;
    public long sendPing;
    public long sendPong;

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void encode() {
    }

    @Override
    public void decode() {
        super.decode();
        InetSocketAddress addr = this.getAddress();
        this.address = addr.getHostString();
        this.port = addr.getPort();

        for (int i = 0; i < 10; i++) {
            this.systemAddresses[i] = this.getAddress();
        }

        this.sendPing = this.getLong();
        this.sendPong = this.getLong();
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new CLIENT_HANDSHAKE_DataPacket();
        }

    }

}
