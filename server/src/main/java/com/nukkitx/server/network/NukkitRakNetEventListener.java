package com.nukkitx.server.network;

import com.nukkitx.api.event.server.ConnectionRequestEvent;
import com.nukkitx.network.raknet.RakNetServerEventListener;
import com.nukkitx.protocol.bedrock.v332.Bedrock_v332;
import com.nukkitx.server.NukkitConfiguration;
import com.nukkitx.server.NukkitServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

@Log4j2
@RequiredArgsConstructor
public class NukkitRakNetEventListener implements RakNetServerEventListener {
    private static final int RAKNET_PROTOCOL_VERSION = 9;
    private final NukkitServer server;

    @Nonnull
    @Override
    public Action onConnectionRequest(InetSocketAddress inetSocketAddress, int protocolVersion) {
        ConnectionRequestEvent.Result result = ConnectionRequestEvent.Result.CONTINUE;
        if (server.getSessionManager().playerSessionCount() >= server.getConfiguration().getGeneral().getMaximumPlayers()) {
            result = ConnectionRequestEvent.Result.SERVER_FULL;
        }
        if (protocolVersion != RAKNET_PROTOCOL_VERSION) {
            result = ConnectionRequestEvent.Result.INVALID_PROTOCOL_VERSION;
        }
        ConnectionRequestEvent event = new ConnectionRequestEvent(inetSocketAddress, result);
        server.getEventManager().fire(event);

        Action action;
        switch (event.getResult()) {
            case BANNED:
                action = Action.BANNED;
                break;
            case SERVER_FULL:
                action = Action.NO_INCOMING_CONNECTIONS;
                break;
            case INVALID_PROTOCOL_VERSION:
                action = Action.INCOMPATIBLE_PROTOCOL_VERISON;
                break;
            default:
                action = Action.CONTINUE;
        }
        return action;
    }

    @Nonnull
    @Override
    public Advertisement onQuery(InetSocketAddress inetSocketAddress) {
        NukkitConfiguration config = server.getConfiguration();
        return new Advertisement(
                "MCPE",
                config.getGeneral().getMotd(),
                Bedrock_v332.V332_CODEC.getProtocolVersion(),
                NukkitServer.MINECRAFT_VERSION.toString(),
                server.getSessionManager().playerSessionCount(),
                config.getGeneral().getMaximumPlayers(),
                config.getGeneral().getSubMotd(),
                config.getMechanics().getDefaultGamemode().toString()
        );
    }
}
