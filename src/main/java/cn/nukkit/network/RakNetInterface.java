package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Utils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.nukkitx.network.raknet.*;
import com.nukkitx.network.util.DisconnectReason;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class RakNetInterface implements RakNetServerListener, AdvancedSourceInterface {

    private final Server server;

    private Network network;

    private final RakNetServer raknet;

    private final Map<InetSocketAddress, NukkitRakNetSession> sessions = new ConcurrentHashMap<>();

    private final Set<ScheduledFuture<?>> tickFutures = new HashSet<>();
    
    private final FastThreadLocal<Map.Entry<Object, Set<NukkitRakNetSession>>> sessionsToTick = new FastThreadLocal<Map.Entry<Object, Set<NukkitRakNetSession>>>() {
        @Override
        protected Map.Entry<Object, Set<NukkitRakNetSession>> initialValue() {
            return new AbstractMap.SimpleEntry<>(new Object(), Collections.newSetFromMap(new IdentityHashMap<>()));
        }
    };

    private byte[] advertisement;

    public RakNetInterface(Server server) {
        try {
            this.server = server;

            InetSocketAddress bindAddress = new InetSocketAddress(Strings.isNullOrEmpty(this.server.getIp()) ? "0.0.0.0" : this.server.getIp(), this.server.getPort());

            this.raknet = new RakNetServer(bindAddress, Runtime.getRuntime().availableProcessors());
            this.raknet.setProtocolVersion(10);
            this.raknet.bind().join();
            this.raknet.setListener(this);

            for (EventExecutor executor : this.raknet.getBootstrap().config().group()) {
                this.tickFutures.add(executor.scheduleAtFixedRate(() -> {
                    Map.Entry<Object, Set<NukkitRakNetSession>> sess = sessionsToTick.get();
                    synchronized (sess.getKey()) {
                        for (NukkitRakNetSession session : sess.getValue()) {
                            try {
                                session.sendOutbound();
                            } catch (Exception e) {
                                log.fatal("Exception while sending packets to {}", session.player.getName(), e);
                                //session.player.close("Outbound packet error");
                            }
                        }
                    }
                }, 0, 50, TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            log.fatal("An exception occurred while creating a new RakNetInterface!", e);
            throw e;
        }
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean process() {
        try {
            Iterator<NukkitRakNetSession> iterator = this.sessions.values().iterator();
            while (iterator.hasNext()) {
                NukkitRakNetSession listener = iterator.next();
                Player player = listener.player;
                if (listener.disconnectReason != null) {
                    player.close(player.getLeaveMessage(), listener.disconnectReason, false);
                    iterator.remove();
                    continue;
                }
                DataPacket packet;
                while ((packet = listener.inbound.poll()) != null) {
                    listener.player.handleDataPacket(packet);
                }
            }
            return true;
        } catch (Exception e) {
            log.fatal("An exception occurred while processing the RakNetInterface of the sessions: {}", sessions, e);
            throw e;
        }
    }

    @Override
    public int getNetworkLatency(Player player) {
        try {
            RakNetServerSession session = this.raknet.getSession(player.getSocketAddress());
            return session == null ? -1 : (int) session.getPing();
        } catch (Exception e) {
            log.fatal("An exception occurred while getting the network latency of {}", player, e);
            throw e;
        }
    }

    @Override
    public void close(Player player) {
        try {
            this.close(player, "unknown reason");
        } catch (Exception e) {
            log.fatal("An exception occurred while closing the player connection for {}", player, e);
            throw e;
        }
    }

    @Override
    public void close(Player player, String reason) {
        try {
            RakNetServerSession session = this.raknet.getSession(player.getSocketAddress());
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            log.fatal("An exception occurred while closing the player connection for {} with reason {}", player, reason, e);
            throw e;
        }
    }

    @Override
    public void shutdown() {
        try {
            this.tickFutures.forEach(future -> future.cancel(false));
            this.raknet.close();
        } catch (Exception e) {
            log.fatal("An exception occurred while shutting down the RakNet interface with {}", sessions, e);
            throw e;
        }
    }

    @Override
    public void emergencyShutdown() {
        try {
            this.tickFutures.forEach(future -> future.cancel(true));
            this.raknet.close();
        } catch (Exception e) {
            log.fatal("An exception occurred while emergency shutting down the RakNet interface with {}", sessions, e);
            throw e;
        }
    }

    @Override
    public void blockAddress(InetAddress address) {
        try {
            this.raknet.block(address);
        } catch (Exception e) {
            log.fatal("An exception occurred while blocking the address {}", address, e);
            throw e;
        }
    }

    @Override
    public void blockAddress(InetAddress address, int timeout) {
        try {
            this.raknet.block(address, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.fatal("An exception occurred while blocking the address {} with timeout {}", address, timeout, e);
            throw e;
        }
    }

    @Override
    public void unblockAddress(InetAddress address) {
        try {
            this.raknet.unblock(address);
        } catch (Exception e) {
            log.fatal("An exception occurred while unblocking the address {}", address, e);
            throw e;
        }
    }

    @Override
    public void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload) {
        try {
            this.raknet.send(socketAddress, payload);
        } catch (Exception e) {
            log.fatal("An exception occurred while sending a raw packet to {}", socketAddress, e);
            throw e;
        }
    }

    @Override
    public void setName(String name) {
        try {
            QueryRegenerateEvent info = this.server.getQueryInformation();
            String[] names = name.split("!@#");  //Split double names within the program
            String motd = Utils.rtrim(names[0].replace(";", "\\;"), '\\');
            String subMotd = names.length > 1 ? Utils.rtrim(names[1].replace(";", "\\;"), '\\') : "";
            StringJoiner joiner = new StringJoiner(";")
                    .add("MCPE")
                    .add(motd)
                    .add(Integer.toString(ProtocolInfo.CURRENT_PROTOCOL))
                    .add(ProtocolInfo.MINECRAFT_VERSION_NETWORK)
                    .add(Integer.toString(info.getPlayerCount()))
                    .add(Integer.toString(info.getMaxPlayerCount()))
                    .add(Long.toString(this.raknet.getGuid()))
                    .add(subMotd)
                    .add(Server.getGamemodeString(this.server.getDefaultGamemode(), true))
                    .add("1");

            this.advertisement = joiner.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.fatal("An exception occurred while updating the MOTD (server ping advertisement)", e);
            throw e;
        }
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet) {
        try {
            return this.putPacket(player, packet, false);
        } catch (Exception e) {
            log.fatal("An exception occurred while putting the packet {} to {}", packet, player, e);
            throw e;
        }
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK) {
        try {
            return this.putPacket(player, packet, needACK, false);
        } catch (Exception e) {
            log.fatal("An exception occurred while putting the packet {} to {} with needACK: {}", packet, player, needACK, e);
            throw e;
        }
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK, boolean immediate) {
        try {
            NukkitRakNetSession session = this.sessions.get(player.getSocketAddress());

            if (session != null) {
                session.outbound.offer(packet);
            }

            return null;
        } catch (Exception e) {
            log.fatal("An exception occurred while putting the packet {} to {} with needACK: {} and immediate {}", packet, player, needACK, immediate, e);
            throw e;
        }
    }

    @Override
    public boolean onConnectionRequest(InetSocketAddress inetSocketAddress) {
        return true;
    }

    @Override
    public byte[] onQuery(InetSocketAddress inetSocketAddress) {
        return this.advertisement;
    }

    @Override
    public void onSessionCreation(RakNetServerSession session) {
        try {
            PlayerCreationEvent ev = new PlayerCreationEvent(this, Player.class, Player.class, null, session.getAddress());
            this.server.getPluginManager().callEvent(ev);
            Class<? extends Player> clazz = ev.getPlayerClass();

            Player player;
            InetSocketAddress socketAddress = ev.getSocketAddress();
            try {
                Constructor<? extends Player> constructor = clazz.getConstructor(SourceInterface.class, Long.class, InetSocketAddress.class);
                player = constructor.newInstance(this, ev.getClientId(), socketAddress);
            } catch (ReflectiveOperationException e) {
                try {
                    Constructor<? extends Player> constructor = clazz.getConstructor(SourceInterface.class, Long.class, String.class, Integer.TYPE);
                    player = constructor.newInstance(this, ev.getClientId(), socketAddress.getHostString(), socketAddress.getPort());
                } catch (ReflectiveOperationException e2) {
                    e2.addSuppressed(e);
                    Server.getInstance().getLogger().logException(e);
                    session.disconnect();
                    return;
                }
            }

            this.server.addPlayer(session.getAddress(), player);
            NukkitRakNetSession nukkitSession = new NukkitRakNetSession(session, player);
            this.sessions.put(session.getAddress(), nukkitSession);
            Map.Entry<Object, Set<NukkitRakNetSession>> sess = this.sessionsToTick.get();
            synchronized (sess.getKey()) {
                sess.getValue().add(nukkitSession);
            }
            session.setListener(nukkitSession);
        } catch (Exception e) {
            log.fatal("An exception occurred while processing the RakNetServerSession event for {}", session, e);
            throw e;
        }
    }

    @Override
    public void onUnhandledDatagram(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        try {
            this.server.handlePacket(datagramPacket.sender(), datagramPacket.content());
        } catch (Exception e) {
            log.fatal("An exception occurred while processing a datagram from {}", datagramPacket.sender(), e);
            throw e;
        }
    }

    @RequiredArgsConstructor
    private class NukkitRakNetSession implements RakNetSessionListener {
        private final RakNetServerSession session;
        private final Player player;
        private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();
        private final Queue<DataPacket> outbound = PlatformDependent.newSpscQueue();
        private String disconnectReason = null;

        @Override
        public void onSessionChangeState(RakNetState rakNetState) {
        }

        @Override
        public void onDisconnect(DisconnectReason disconnectReason) {
            try {
                if (disconnectReason == DisconnectReason.TIMED_OUT) {
                    this.disconnectReason = "Timed out";
                } else {
                    this.disconnectReason = "Disconnected from Server";
                }
                Map.Entry<Object, Set<NukkitRakNetSession>> sess = RakNetInterface.this.sessionsToTick.get();
                synchronized (sess.getKey()) {
                    sess.getValue().remove(this);
                }
            } catch (Exception e) {
                log.fatal("An exception occurred while processing the onDisconnect of the player {}", player, e);
                throw e;
            }
        }

        @Override
        public void onEncapsulated(EncapsulatedPacket packet) {
            try {
                ByteBuf buffer = packet.getBuffer();
                short packetId = buffer.readUnsignedByte();
                if (packetId == 0xfe) {
                    DataPacket batchPacket = RakNetInterface.this.network.getPacket(ProtocolInfo.BATCH_PACKET);
                    if (batchPacket == null) {
                        return;
                    }

                    byte[] packetBuffer = new byte[buffer.readableBytes()];
                    buffer.readBytes(packetBuffer);
                    batchPacket.setBuffer(packetBuffer);
                    batchPacket.decode();

                    this.inbound.offer(batchPacket);
                }
            } catch (Exception e) {
                log.fatal("An exception occurred while processing the onEncapsulated {} of the player {}", packet, player, e);
                throw e;
            }
        }

        @Override
        public void onDirect(ByteBuf byteBuf) {
            // We don't allow any direct packets so ignore.
        }

        private void sendOutbound() {
            try {
                List<DataPacket> toBatch = new ArrayList<>();
                DataPacket packet;
                while ((packet = this.outbound.poll()) != null) {
                    if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                        if (!toBatch.isEmpty()) {
                            this.sendPackets(toBatch.toArray(new DataPacket[0]));
                            toBatch.clear();
                        }

                        this.sendPacket(((BatchPacket) packet).payload);
                    } else {
                        toBatch.add(packet);
                    }
                }

                if (!toBatch.isEmpty()) {
                    this.sendPackets(toBatch.toArray(new DataPacket[0]));
                }
            } catch (Exception e) {
                log.fatal("An exception occurred while processing the sendOutbound of the player {}", player, e);
                throw e;
            }
        }

        private void sendPackets(DataPacket[] packets) {
            try {
                BinaryStream batched = new BinaryStream();
                for (DataPacket packet : packets) {
                    Preconditions.checkArgument(!(packet instanceof BatchPacket), "Cannot batch BatchPacket");
                    try {
                        if (!packet.isEncoded) packet.encode();
                    } catch (Exception e) {
                        log.fatal("Exception while encoding packet {} to {}", packet, player.getName(), e);
                        continue;
                    }
                    byte[] buf = packet.getBuffer();
                    batched.putUnsignedVarInt(buf.length);
                    batched.put(buf);

                    if (log.isTraceEnabled() && !server.isIgnoredPacket(packet.getClass())) {
                        log.trace("Outbound {}: {}", player.getName(), packet);
                    }
                }

                try {
                    this.sendPacket(Network.deflateRaw(batched.getBuffer(), network.getServer().networkCompressionLevel));
                } catch (IOException e) {
                    log.info("Unable to deflate batched packets", e);
                }
            } catch (Exception e) {
                log.fatal("An exception occurred while processing the sendPackets of the player {}. Packets: {}", player, packets != null? Arrays.asList(packets) : "null", e);
                throw e;
            }
        }

        private void sendPacket(byte[] payload) {
            try {
                ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer(1 + payload.length);
                byteBuf.writeByte(0xfe);
                byteBuf.writeBytes(payload);
                this.session.send(byteBuf);
            } catch (Exception e) {
                log.fatal("An exception occurred while processing the sendPacket of the player {}. ", player, e);
                throw e;
            }
        }
    }
}
