package cn.nukkit.network.session;

import cn.nukkit.Player;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.RakNetInterface;
import com.google.common.base.Preconditions;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.FormattedMessage;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.util.Queue;

@Log4j2
public class RakNetPlayerSession implements NetworkPlayerSession, BedrockPacketHandler {

    private final RakNetInterface server;
    private final BedrockServerSession session;

    private final Queue<BedrockPacket> inbound = PlatformDependent.newSpscQueue();

    private Player player;
    private String disconnectReason = null;

    private CompressionProvider compression = CompressionProvider.NONE;

    public RakNetPlayerSession(RakNetInterface server, BedrockServerSession session) {
        this.server = server;
        this.session = session;
    }

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        // Queue packets up to be processed on the main thread.
        this.inbound.offer(packet);
        return PacketSignal.HANDLED;
    }

    @Override
    public void onDisconnect(String reason) {
        this.disconnectReason = reason;
    }

    @Override
    public void disconnect(String reason) {
        session.disconnect(reason);
    }

    @Override
    public void sendPacket(BedrockPacket packet) {
        if (this.session.isConnected()) {
            this.session.sendPacket(packet);
        }
    }

    @Override
    public void sendImmediatePacket(BedrockPacket packet, Runnable callback) {
        if (this.session.isConnected()) {
            this.session.sendPacketImmediately(packet);
            callback.run(); // TODO: This is probably wrong, but it'll do for now.
        }
    }

    public void serverTick() {
        BedrockPacket packet;
        while ((packet = this.inbound.poll()) != null) {
            try {
                this.player.handleDataPacket(packet);
            } catch (Exception e) {
                log.error(new FormattedMessage("An error occurred whilst handling {} for {}",
                        new Object[]{packet.getClass().getSimpleName(), this.player.getName()}, e));
            }
        }
    }

    @Override
    public void setCompression(CompressionProvider compression) {
        Preconditions.checkNotNull(compression);
        this.compression = compression;
    }

    @Override
    public CompressionProvider getCompression() {
        return this.compression;
    }

    public void setPlayer(Player player) {
        Preconditions.checkArgument(this.player == null && player != null);
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    public BedrockServerSession getBackingSession() {
        return this.session;
    }

    public String getDisconnectReason() {
        return this.disconnectReason;
    }
}
