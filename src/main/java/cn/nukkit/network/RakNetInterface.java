package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.Zlib;
import com.google.common.base.Strings;
import com.nukkitx.network.raknet.*;
import com.nukkitx.network.util.DisconnectReason;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class RakNetInterface implements RakNetServerListener, AdvancedSourceInterface {

    private final Server server;

    private Network network;

    private final RakNetServer raknet;

    private Set<NukkitSessionListener> sessionListeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private byte[] advertisement;

    public RakNetInterface(Server server) {
        this.server = server;

        InetSocketAddress bindAddress = new InetSocketAddress(Strings.isNullOrEmpty(this.server.getIp()) ? "0.0.0.0" : this.server.getIp(), this.server.getPort());

        this.raknet = new RakNetServer(bindAddress);
        this.raknet.bind().join();
        this.raknet.setListener(this);
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean process() {
        for (NukkitSessionListener listener : sessionListeners) {
            DataPacket packet;
            while ((packet = listener.packets.poll()) != null) {
                listener.player.handleDataPacket(packet);
            }
        }
        return true;
    }

    @Override
    public int getNetworkLatency(Player player) {
        RakNetServerSession session = this.raknet.getSession(player.getSocketAddress());
        return session == null ? -1 : (int) session.getPing();
    }

    @Override
    public void close(Player player) {
        this.close(player, "unknown reason");
    }

    @Override
    public void close(Player player, String reason) {
        RakNetServerSession session = this.raknet.getSession(player.getSocketAddress());
        if (session != null) {
            session.disconnect();
        }
    }

    @Override
    public void shutdown() {
        this.raknet.close();
    }

    @Override
    public void emergencyShutdown() {
        this.raknet.close();
    }

    @Override
    public void blockAddress(InetAddress address) {
        this.raknet.block(address);
    }

    @Override
    public void blockAddress(InetAddress address, int timeout) {
        this.raknet.block(address, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void unblockAddress(InetAddress address) {
        this.raknet.unblock(address);
    }

    @Override
    public void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload) {
        this.raknet.send(socketAddress, payload);
    }

    @Override
    public void setName(String name) {
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
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet) {
        return this.putPacket(player, packet, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK) {
        return this.putPacket(player, packet, needACK, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK, boolean immediate) {
        RakNetServerSession session = this.raknet.getSession(player.getSocketAddress());
        if (session == null) {
            return null;
        }

        byte[] buffer;
        if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
            buffer = ((BatchPacket) packet).payload;
        } else if (!needACK) {
            this.server.batchPackets(new Player[]{player}, new DataPacket[]{packet}, true);
            return null;
        } else {
            if (!packet.isEncoded) {
                packet.encode();
                packet.isEncoded = true;
            }
            buffer = packet.getBuffer();
            try {
                buffer = Zlib.deflate(
                        Binary.appendBytes(Binary.writeUnsignedVarInt(buffer.length), buffer),
                        Server.getInstance().networkCompressionLevel);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            byteBuf.writeByte(0xfe);
            byteBuf.writeBytes(buffer);

            session.send(byteBuf, packet.reliability, packet.getChannel());

            return null;
        } finally {
            byteBuf.release();
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
        PlayerCreationEvent ev = new PlayerCreationEvent(this, Player.class, Player.class, null, session.getAddress());
        this.server.getPluginManager().callEvent(ev);
        Class<? extends Player> clazz = ev.getPlayerClass();

        try {
            Constructor constructor = clazz.getConstructor(SourceInterface.class, Long.class, InetSocketAddress.class);
            Player player = (Player) constructor.newInstance(this, ev.getClientId(), ev.getSocketAddress());
            this.server.addPlayer(session.getAddress(), player);
            NukkitSessionListener listener = new NukkitSessionListener(player);
            this.sessionListeners.add(listener);
            session.setListener(listener);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    @Override
    public void onUnhandledDatagram(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        this.server.handlePacket(datagramPacket.sender(), datagramPacket.content());
    }

    @RequiredArgsConstructor
    private class NukkitSessionListener implements RakNetSessionListener {
        private final Player player;
        private final Queue<DataPacket> packets = new ConcurrentLinkedQueue<>();

        @Override
        public void onSessionChangeState(RakNetState rakNetState) {

        }

        @Override
        public void onDisconnect(DisconnectReason disconnectReason) {
            this.player.close(player.getLeaveMessage(), "Disconnected from Server");
            RakNetInterface.this.sessionListeners.remove(this);
        }

        @Override
        public void onUserPacket(ByteBuf buffer) {
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

                packets.offer(batchPacket);
            }
        }
    }
}
