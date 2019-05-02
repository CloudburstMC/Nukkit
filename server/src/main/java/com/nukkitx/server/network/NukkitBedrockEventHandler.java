package com.nukkitx.server.network;

import com.nukkitx.api.event.server.ConnectionRequestEvent;
import com.nukkitx.api.event.server.ServerPingEvent;
import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.network.bedrock.session.LoginPacketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.InetSocketAddress;

@Log4j2
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class NukkitBedrockEventHandler implements BedrockServerEventHandler {
    private final NukkitServer server;

    @Override
    public boolean onConnectionRequest(InetSocketAddress address) {
        ConnectionRequestEvent event = new ConnectionRequestEvent(address);
        this.server.getEventManager().fire(event);

        return !event.isCancelled();
    }

    @Nullable
    @Override
    public BedrockPong onQuery(InetSocketAddress address) {
        ServerPingEvent event = new ServerPingEvent(address, this.server);
        this.server.getEventManager().fire(event);

        BedrockPong pong = new BedrockPong();
        pong.setEdition("MCPE");
        pong.setVersion("");
        pong.setMotd(event.getMotd());
        pong.setSubMotd(event.getSubMotd());
        pong.setPlayerCount(event.getPlayerCount());
        pong.setMaximumPlayerCount(event.getMaxPlayerCount());
        String gameType;
        switch (event.getDefaultGamemode()) {
            case SURVIVAL:
                gameType = "Survival";
                break;
            case CREATIVE:
                gameType = "Creative";
                break;
            case ADVENTURE:
                gameType = "Adventure";
                break;
            default:
                gameType = "Default";
                break;
        }
        pong.setGameType(gameType);
        pong.setNintendoLimited(false);
        pong.setProtocolVersion(NukkitServer.MINECRAFT_CODEC.getProtocolVersion());
        return pong;
    }

    @Override
    public void onSessionCreation(BedrockServerSession bedrockServerSession) {
        bedrockServerSession.setLogging(true);
        bedrockServerSession.setPacketHandler(new LoginPacketHandler(bedrockServerSession, this.server));
    }
}
