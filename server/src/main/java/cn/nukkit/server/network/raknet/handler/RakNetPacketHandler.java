package cn.nukkit.server.network.raknet.handler;

import cn.nukkit.api.event.server.ConnectionRequestEvent;
import cn.nukkit.api.event.server.RefreshPingEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.network.minecraft.MinecraftPacketRegistry;
import cn.nukkit.server.network.minecraft.session.LoginPacketHandler;
import cn.nukkit.server.network.minecraft.session.MinecraftSession;
import cn.nukkit.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import cn.nukkit.server.network.raknet.packet.*;
import cn.nukkit.server.network.raknet.session.RakNetSession;
import cn.nukkit.server.network.util.EncryptionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.util.StringJoiner;

import static cn.nukkit.server.network.raknet.RakNetUtil.MAXIMUM_MTU_SIZE;
import static cn.nukkit.server.network.raknet.RakNetUtil.RAKNET_PROTOCOL_VERSION;

@Log4j2
public class RakNetPacketHandler extends SimpleChannelInboundHandler<DirectAddressedRakNetPacket> {
    private static final long SERVER_ID = EncryptionUtil.generateServerId();
    private final NukkitServer server;
    private String advert;

    public RakNetPacketHandler(NukkitServer server) {
        this.server = server;
        refreshPing();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DirectAddressedRakNetPacket packet) throws Exception {
        try {
            MinecraftSession session = server.getSessionManager().get(packet.sender());

            /*if (log.isTraceEnabled()) {
                log.trace("Inbound {}: {}", packet.sender(), packet.content());
            }*/

            // Sessionless packets
            if (session == null) {
                if (packet.content() instanceof UnconnectedPingPacket) {
                    UnconnectedPingPacket request = (UnconnectedPingPacket) packet.content();
                    UnconnectedPongPacket response = new UnconnectedPongPacket();
                    response.setPingId(request.getPingId());
                    response.setServerId(SERVER_ID);
                    response.setAdvertise(advert);
                    ctx.writeAndFlush(new DirectAddressedRakNetPacket(response, packet.sender(), packet.recipient()), ctx.voidPromise());
                    return;
                }
                if (packet.content() instanceof OpenConnectionRequest1Packet) {
                    OpenConnectionRequest1Packet request = (OpenConnectionRequest1Packet) packet.content();
                    int maximum = server.getConfiguration().getGeneral().getMaximumPlayers();
                    ConnectionRequestEvent.Result result;
                    if (request.getProtocolVersion() != RAKNET_PROTOCOL_VERSION) {
                        result = ConnectionRequestEvent.Result.INCOMPATIBLE_VERSION;
                    } else if (maximum > 0 && server.getSessionManager().playerSessionCount() >= maximum) {
                        result = ConnectionRequestEvent.Result.SERVER_FULL;
                    } else {// TODO: Implement the IP banlist here so we don't have to initialize a session just to disconnect them.
                        result = ConnectionRequestEvent.Result.CONTINUE;
                    }
                    ConnectionRequestEvent event = new ConnectionRequestEvent(packet.sender(), result);
                    server.getEventManager().fire(event);

                    switch (event.getResult()) {
                        case INCOMPATIBLE_VERSION:
                            IncompatibleProtocolVersion badVersion = new IncompatibleProtocolVersion();
                            badVersion.setServerId(SERVER_ID);
                            ctx.writeAndFlush(new DirectAddressedRakNetPacket(badVersion, packet.sender(), packet.recipient()), ctx.voidPromise());
                            return;
                        case SERVER_FULL:
                            NoFreeIncomingConnectionsPacket badResponse = new NoFreeIncomingConnectionsPacket();
                            badResponse.setServerId(SERVER_ID);
                            ctx.writeAndFlush(new DirectAddressedRakNetPacket(badResponse, packet.sender(), packet.recipient()), ctx.voidPromise());
                            return;
                        case BANNED:
                            ConnectionBannedPacket bannedPacket = new ConnectionBannedPacket();
                            bannedPacket.setServerId(SERVER_ID);
                            ctx.writeAndFlush(new DirectAddressedRakNetPacket(bannedPacket, packet.sender(), packet.recipient()), ctx.voidPromise());
                            return;
                        default:
                            OpenConnectionReply1Packet response = new OpenConnectionReply1Packet();
                            response.setMtuSize((request.getMtu() > MAXIMUM_MTU_SIZE ? MAXIMUM_MTU_SIZE : request.getMtu()));
                            response.setServerSecurity(false);
                            response.setServerId(SERVER_ID);
                            ctx.writeAndFlush(new DirectAddressedRakNetPacket(response, packet.sender(), packet.recipient()), ctx.voidPromise());
                            return;
                    }
                }
                if (packet.content() instanceof OpenConnectionRequest2Packet) {
                    OpenConnectionRequest2Packet request = (OpenConnectionRequest2Packet) packet.content();
                    OpenConnectionReply2Packet response = new OpenConnectionReply2Packet();
                    response.setMtuSize(request.getMtuSize());
                    response.setServerSecurity(false);
                    response.setClientAddress(packet.sender());
                    response.setServerId(SERVER_ID);
                    session = new MinecraftSession(null, server, new RakNetSession(packet.sender(), request.getMtuSize(), ctx.channel(), server));
                    session.setHandler(new LoginPacketHandler(session));
                    server.getSessionManager().add(packet.sender(), session);
                    ctx.writeAndFlush(new DirectAddressedRakNetPacket(response, packet.sender(), packet.recipient()), ctx.voidPromise());
                }
            } else {
                if (packet.content() instanceof AckPacket) {
                    if (session.getConnection() instanceof RakNetSession) {
                        ((RakNetSession) session.getConnection()).onAck(((AckPacket) packet.content()).getIds());
                    }
                }
                if (packet.content() instanceof NakPacket) {
                    if (session.getConnection() instanceof RakNetSession) {
                        ((RakNetSession) session.getConnection()).onNak(((NakPacket) packet.content()).getIds());
                    }
                }
            }
        } finally {
            packet.release();
        }
    }

    private void refreshPing() {
        RefreshPingEvent event = new RefreshPingEvent(server, MinecraftPacketRegistry.BROADCAST_PROTOCOL_VERSION);
        server.getEventManager().fire(event);
        StringJoiner joiner = new StringJoiner(";")
                .add("MCPE") // MCEE for Education Edition
                .add(event.getMotd())
                .add(Integer.toString(event.getProtocolVersion()))
                .add("") // This is permanently set to nothing because it's a waste of space.
                .add(Long.toString(event.getPlayerCount()))
                .add(Long.toString(event.getMaxPlayerCount()))
                .add(Long.toString(SERVER_ID))
                .add(event.getSubMotd())
                .add(event.getDefaultGamemode().name());
        advert = joiner.toString();
    }
}
