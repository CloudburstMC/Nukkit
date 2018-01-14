package cn.nukkit.server.network.raknet.codec;

import cn.nukkit.server.network.Packets;
import cn.nukkit.server.network.raknet.NetworkPacket;
import cn.nukkit.server.network.raknet.RakNetPackets;
import cn.nukkit.server.network.raknet.datagram.RakNetDatagramFlags;
import cn.nukkit.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import cn.nukkit.server.network.raknet.packet.AckPacket;
import cn.nukkit.server.network.raknet.packet.NakPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class DatagramRakNetPacketCodec extends MessageToMessageCodec<DatagramPacket, DirectAddressedRakNetPacket> {
    private static final int USER_ID_START = 0x80;

    @Override
    protected void encode(ChannelHandlerContext ctx, DirectAddressedRakNetPacket pkg, List<Object> list) throws Exception {
        // Certain RakNet packets do not require special encapsulation. This encoder tries to handle them.
        try {
            ByteBuf buf = Packets.getCodec(RakNetPackets.TYPE).tryEncode(pkg.content());
            list.add(new DatagramPacket(buf, pkg.recipient(), pkg.sender()));
        } finally {
            pkg.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> list) throws Exception {
        // Certain RakNet packets do not require special encapsulation. This encoder tries to handle them.
        ByteBuf buf = packet.content();
        if (buf.readableBytes() == 0) {
            // not interested
            return;
        }

        buf.markReaderIndex();

        int id = buf.readUnsignedByte();
        if (id < USER_ID_START) { // User data
            buf.resetReaderIndex();

            // We can decode a packet immediately.
            NetworkPacket netPackage = Packets.getCodec(RakNetPackets.TYPE).tryDecode(buf);
            if (netPackage != null) {
                list.add(new DirectAddressedRakNetPacket(netPackage, packet.recipient(), packet.sender()));
            }
        } else {
            // We can decode some datagrams directly.
            buf.resetReaderIndex();
            RakNetDatagramFlags flags = new RakNetDatagramFlags(buf.readByte());
            if (flags.isValid()) {
                if (flags.isAck()) {
                    // ACK
                    AckPacket ackPacket = new AckPacket();
                    ackPacket.decode(buf);
                    list.add(new DirectAddressedRakNetPacket(ackPacket, packet.recipient(), packet.sender()));
                } else if (flags.isNak()) {
                    // NAK
                    NakPacket nakPacket = new NakPacket();
                    nakPacket.decode(buf);
                    list.add(new DirectAddressedRakNetPacket(nakPacket, packet.recipient(), packet.sender()));
                } else {
                    buf.readerIndex(0);
                    list.add(packet.retain()); // needs further processing
                }
            } else {
                buf.readerIndex(0); // not interested
            }
        }
    }
}
