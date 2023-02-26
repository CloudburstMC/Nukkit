package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.session.NetworkPlayerSession;
import cn.nukkit.network.session.RakNetPlayerSession;
import cn.nukkit.utils.Utils;
import com.google.common.base.Strings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.log4j.Log4j2;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class RakNetInterface implements AdvancedSourceInterface {

    private final Server server;
    private Network network;

    private final Map<InetSocketAddress, RakNetPlayerSession> sessions = new HashMap<>();
    private final Queue<RakNetPlayerSession> sessionCreationQueue = PlatformDependent.newMpscQueue();
    private final List<Channel> channels = new ArrayList<>();

    public RakNetInterface(Server server) {
        this.server = server;

        InetSocketAddress bindAddress = new InetSocketAddress(Strings.isNullOrEmpty(this.server.getIp()) ? "0.0.0.0" : this.server.getIp(), this.server.getPort());
        
        ServerBootstrap bootstrap = new ServerBootstrap()
                .channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
                .childHandler(new BedrockServerInitializer() {
                    @Override
                    protected void initSession(BedrockServerSession bedrockServerSession) {
                        RakNetInterface.this.onSessionCreation0(bedrockServerSession);
                    }
                })
                .localAddress(bindAddress);

        // TODO: Epoll, KQueue, etc.
        // Bind to the network interface
        this.channels.add(bootstrap.bind()
                .awaitUninterruptibly()
                .channel());
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean process() {
        RakNetPlayerSession session;
        while ((session = this.sessionCreationQueue.poll()) != null) {
            InetSocketAddress address = (InetSocketAddress) session.getBackingSession().getSocketAddress();
            try {
                PlayerCreationEvent event = new PlayerCreationEvent(this, Player.class, Player.class, null, address);
                this.server.getPluginManager().callEvent(event);

                this.sessions.put(event.getSocketAddress(), session);

                Constructor<? extends Player> constructor = event.getPlayerClass().getConstructor(SourceInterface.class, Long.class, InetSocketAddress.class);
                Player player = constructor.newInstance(this, event.getClientId(), event.getSocketAddress());
                this.server.addPlayer(address, player);
                session.setPlayer(player);
            } catch (Exception e) {
                Server.getInstance().getLogger().error("Failed to create player", e);
                session.disconnect("Internal error");
                this.sessions.remove(address);
            }
        }

        Iterator<RakNetPlayerSession> iterator = this.sessions.values().iterator();
        while (iterator.hasNext()) {
            RakNetPlayerSession nukkitSession = iterator.next();
            Player player = nukkitSession.getPlayer();
            if (nukkitSession.getDisconnectReason() != null) {
                player.close(player.getLeaveMessage(), nukkitSession.getDisconnectReason(), false);
                iterator.remove();
            } else {
                nukkitSession.serverTick();
            }
        }
        return true;
    }

    @Override
    public int getNetworkLatency(Player player) {
        return -1;
//        RakNetServerSession session = this.raknet.getSession(player.getSocketAddress());
//        return session == null ? -1 : (int) session.getPing();
    }

    @Override
    public NetworkPlayerSession getSession(InetSocketAddress address) {
        return this.sessions.get(address);
    }

    @Override
    public void close(Player player) {
        this.close(player, "unknown reason");
    }

    @Override
    public void close(Player player, String reason) {
        NetworkPlayerSession playerSession = this.getSession(player.getSocketAddress());
        if (playerSession != null) {
            playerSession.disconnect(reason);
        }
    }

    @Override
    public void shutdown() {
        this.emergencyShutdown(); // Does the same thing
    }

    @Override
    public void emergencyShutdown() {
        this.sessions.values().forEach(session -> session.disconnect("Shutdown"));
        List<ChannelFuture> closeFutures = new ArrayList<>(this.channels.size());
        for (Channel channel : this.channels) {
            closeFutures.add(channel.close());
        }

        for (ChannelFuture future : closeFutures) {
            future.awaitUninterruptibly();
        }
    }

    @Override
    public void blockAddress(InetAddress address) {
        // TODO
    }

    @Override
    public void blockAddress(InetAddress address, int timeout) {
        // TODO
    }

    @Override
    public void unblockAddress(InetAddress address) {
        // TODO
    }

    @Override
    public void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload) {
        // TODO
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
                .add(Long.toString(this.channels.get(0).config().getOption(RakChannelOption.RAK_GUID)))
                .add(subMotd)
                .add(Server.getGamemodeString(this.server.getDefaultGamemode(), true))
                .add("1");

        byte[] advertisement = joiner.toString().getBytes(StandardCharsets.UTF_8);

        for (Channel channel : this.channels) {
            channel.config().setOption(RakChannelOption.RAK_ADVERTISEMENT, Unpooled.wrappedBuffer(advertisement));
        }
    }

    @Override
    public Integer putPacket(Player player, BedrockPacket packet) {
        return this.putPacket(player, packet, false);
    }

    @Override
    public Integer putPacket(Player player, BedrockPacket packet, boolean needACK) {
        return this.putPacket(player, packet, needACK, false);
    }

    @Override
    public Integer putPacket(Player player, BedrockPacket packet, boolean needACK, boolean immediate) {
        RakNetPlayerSession session = this.sessions.get(player.getSocketAddress());
        if (session != null) {
            session.sendPacket(packet);
        }
        return null;
    }

    private void onSessionCreation0(BedrockServerSession backingSession) {
        RakNetPlayerSession session = new RakNetPlayerSession(this, backingSession);
        backingSession.setPacketHandler(session);
        this.sessionCreationQueue.offer(session);
    }

//    FIXME: Replace with handler for boss channel
//    @Override
//    public void onUnhandledDatagram(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
//        this.server.handlePacket(datagramPacket.sender(), datagramPacket.content());
//    }

    public Network getNetwork() {
        return this.network;
    }
}
