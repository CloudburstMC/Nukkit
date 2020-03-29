package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.player.Player;
import cn.nukkit.player.handler.LoginPacketHandler;
import cn.nukkit.utils.Utils;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServer;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Log4j2
@ParametersAreNonnullByDefault
public class BedrockInterface implements AdvancedSourceInterface, BedrockServerEventHandler {

    private final Server server;

    private final BedrockServer bedrock;
    private final BedrockPong advertisement = new BedrockPong();
    private Queue<NukkitSessionListener> disconnectQueue = new ConcurrentLinkedQueue<>();

    public BedrockInterface(Server server) throws Exception {
        this.server = server;

        InetSocketAddress bindAddress = new InetSocketAddress(this.server.getIp(), this.server.getPort());

        this.bedrock = new BedrockServer(bindAddress, Runtime.getRuntime().availableProcessors());
        this.bedrock.setHandler(this);
        try {
            this.bedrock.bind().join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public boolean onConnectionRequest(InetSocketAddress inetSocketAddress) {
        return true; // TODO: 29/01/2020 Add an event?
    }

    @Nullable
    @Override
    public BedrockPong onQuery(InetSocketAddress inetSocketAddress) {
        return advertisement;
    }

    @Override
    public void onSessionCreation(BedrockServerSession session) {
        session.setLogging(false);
        session.setPacketHandler(new LoginPacketHandler(session, server, this));
    }

    @Override
    public void blockAddress(InetAddress address) {
        this.bedrock.getRakNet().block(address);
    }

    @Override
    public void blockAddress(InetAddress address, long timeout, TimeUnit unit) {
        this.bedrock.getRakNet().block(address, timeout, unit);
    }

    @Override
    public void unblockAddress(InetAddress address) {
        this.bedrock.getRakNet().unblock(address);
    }

    @Override
    public void setNetwork(Network network) {
        // no-op
    }

    @Override
    public void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload) {
        this.bedrock.getRakNet().send(socketAddress, payload);
    }

    @Override
    public void onUnhandledDatagram(ChannelHandlerContext ctx, DatagramPacket packet) {
        this.server.handlePacket(packet.sender(), packet.content());
    }

    @Override
    public void setName(String name) {
        QueryRegenerateEvent info = this.server.getQueryInformation();
        String[] names = name.split("!@#");  //Split double names within the program
        String motd = Utils.rtrim(names[0].replace(";", "\\;"), '\\');
        String subMotd = names.length > 1 ? Utils.rtrim(names[1].replace(";", "\\;"), '\\') : "";

        this.advertisement.setEdition("MCPE");
        this.advertisement.setMotd(motd);
        this.advertisement.setSubMotd(subMotd);
        this.advertisement.setPlayerCount(info.getPlayerCount());
        this.advertisement.setMaximumPlayerCount(info.getMaxPlayerCount());
        this.advertisement.setVersion("");
        this.advertisement.setProtocolVersion(0);
        this.advertisement.setGameType(Server.getGamemodeString(this.server.getDefaultGamemode(), true));
        this.advertisement.setNintendoLimited(false);
        this.advertisement.setIpv4Port(this.server.getPort());
        this.advertisement.setIpv6Port(this.server.getPort());
    }

    @Override
    public boolean process() {
        NukkitSessionListener listener;
        while ((listener = disconnectQueue.poll()) != null) {
            listener.player.close(listener.player.getLeaveMessage(), listener.disconnectReason, false);
        }
        return true;
    }

    @Override
    public void shutdown() {
        this.bedrock.close();
    }

    @Override
    public void emergencyShutdown() {
        this.bedrock.close();
    }

    public NukkitSessionListener initDisconnectHandler(Player player){
        return new NukkitSessionListener(player);
    }

    @RequiredArgsConstructor
    private class NukkitSessionListener implements Consumer<DisconnectReason> {
        private final Player player;
        private String disconnectReason = null;

        @Override
        public void accept(DisconnectReason disconnectReason) {
            if (disconnectReason == DisconnectReason.TIMED_OUT) {
                this.disconnectReason = "Timed out";
            } else {
                this.disconnectReason = "Disconnected from Server";
            }
            // Queue for disconnect on main thread.
            BedrockInterface.this.disconnectQueue.add(this);
        }
    }
}
