package cn.nukkit.server.network.raknet.handler;

import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.packet.WrappedPacket;
import cn.nukkit.server.network.minecraft.session.MinecraftSession;
import cn.nukkit.server.network.raknet.NetworkPacket;
import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetPacketRegistry;
import cn.nukkit.server.network.raknet.datagram.EncapsulatedRakNetPacket;
import cn.nukkit.server.network.raknet.enveloped.AddressedRakNetDatagram;
import cn.nukkit.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import cn.nukkit.server.network.raknet.packet.*;
import cn.nukkit.server.network.raknet.session.RakNetSession;
import cn.nukkit.server.network.raknet.util.IntRange;
import com.google.common.net.InetAddresses;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Log4j2
public class RakNetDatagramHandler extends SimpleChannelInboundHandler<AddressedRakNetDatagram> {
    private static final InetSocketAddress LOOPBACK_MCPE = new InetSocketAddress(InetAddress.getLoopbackAddress(), 19132);
    private static final InetSocketAddress JUNK_ADDRESS = new InetSocketAddress(InetAddresses.forString("255.255.255.255"), 19132);
    private final NukkitServer server;

    public RakNetDatagramHandler(NukkitServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram) throws Exception {
        MinecraftSession session = server.getSessionManager().get(datagram.sender());

        if (session == null)
            return;

        // Make sure a RakNet session is backing this packet.
        if (!(session.getConnection() instanceof RakNetSession)) {
            return;
        }

        RakNetSession rakNetSession = (RakNetSession) session.getConnection();

        // Acknowledge receipt of the datagram.
        AckPacket ackPacket = new AckPacket();
        ackPacket.getIds().add(new IntRange(datagram.content().getDatagramSequenceNumber()));
        ctx.writeAndFlush(new DirectAddressedRakNetPacket(ackPacket, datagram.sender()), ctx.voidPromise());

        // Update session touch time.
        session.touch();

        // Check the datagram contents.
        if (datagram.content().getFlags().isValid()) {
            for (EncapsulatedRakNetPacket packet : datagram.content().getPackets()) {
                // Try to figure out what packet got sent.
                if (packet.isHasSplit()) {
                    Optional<ByteBuf> possiblyReassembled = rakNetSession.addSplitPacket(packet);
                    if (possiblyReassembled.isPresent()) {
                        @Cleanup("release") ByteBuf reassembled = possiblyReassembled.get();
                        RakNetPacket pk = RakNetPacketRegistry.decode(reassembled);
                        handlePackage(pk, session);
                    }
                } else {
                    // Try to decode the full packet.
                    NetworkPacket pk = RakNetPacketRegistry.decode(packet.getBuffer());
                    handlePackage(pk, session);
                }
            }
        }
    }

    private void handlePackage(NetworkPacket packet, MinecraftSession session) throws Exception {
        if (packet == null) {
            return;
        }

        if (session.getHandler() == null) {
            log.error("Session " + session.getRemoteAddress() + " has no handler!?");
            return;
        }

        // All Minecraft packets are wrapped to provide encryption and compression.
        if (packet instanceof WrappedPacket) {
            Collection<MinecraftPacket> packets;

            ByteBuf wrappedData = ((WrappedPacket) packet).getPayload();
            ByteBuf unwrappedData = null;
            try {
                if (session.isEncrypted()) {
                    unwrappedData = PooledByteBufAllocator.DEFAULT.directBuffer(wrappedData.readableBytes());
                    session.getDecryptionCipher().cipher(wrappedData, unwrappedData);
                    // TODO: Check adler32 checksum?
                    unwrappedData = unwrappedData.slice(0, unwrappedData.readableBytes() - 8);
                } else {
                    unwrappedData = wrappedData;
                }

                packets = session.getWrapperHandler().decompressPackets(unwrappedData);
            } finally {
                if (unwrappedData != null && unwrappedData != wrappedData) {
                    unwrappedData.release();
                }
            }

            String to = session.getRemoteAddress().orElse(LOOPBACK_MCPE).toString();
            for (MinecraftPacket minecraftPacket : packets) {
                if (log.isTraceEnabled()) {
                    log.trace("Inbound {}: {}", to, minecraftPacket.toString());
                }
                minecraftPacket.handle(session.getHandler());
            }

            return;
        }
        if (packet instanceof ConnectedPingPacket) {
            ConnectedPingPacket request = (ConnectedPingPacket) packet;
            ConnectedPongPacket response = new ConnectedPongPacket();
            response.setPingTime(request.getPingTime());
            response.setPongTime(System.currentTimeMillis());
            session.sendImmediatePackage(response);
            return;
        }
        if (packet instanceof ConnectionRequestPacket) {
            ConnectionRequestPacket request = (ConnectionRequestPacket) packet;
            ConnectionRequestAcceptedPacket response = new ConnectionRequestAcceptedPacket();
            response.setIncomingTimestamp(request.getTimestamp());
            response.setSystemTimestamp(System.currentTimeMillis());
            response.setSystemAddress(session.getRemoteAddress().orElse(LOOPBACK_MCPE));
            InetSocketAddress[] addresses = new InetSocketAddress[20];
            Arrays.fill(addresses, JUNK_ADDRESS);
            addresses[0] = LOOPBACK_MCPE;
            response.setSystemAddresses(addresses);
            response.setSystemIndex((short) 0);
            session.sendImmediatePackage(response);
            return;
        }
        if (packet instanceof DisconnectNotificationPacket) {
            session.disconnect("disconnect.disconnected", false);
            return;
        }

        log.debug("Packet not handled: {}", packet);
    }
}
