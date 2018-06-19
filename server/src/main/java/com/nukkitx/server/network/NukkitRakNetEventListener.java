package com.nukkitx.server.network;

import com.nukkitx.api.event.server.ConnectionRequestEvent;
import com.nukkitx.network.raknet.RakNetEventListener;
import com.nukkitx.server.NukkitConfiguration;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.network.bedrock.BedrockPacketCodec;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
public class NukkitRakNetEventListener implements RakNetEventListener {
    private final NukkitServer server;

    @Override
    public Action onConnectionRequest(InetSocketAddress inetSocketAddress) {
        ConnectionRequestEvent.Result result = ConnectionRequestEvent.Result.CONTINUE;
        if (server.getSessionManager().playerSessionCount() >= server.getConfiguration().getGeneral().getMaximumPlayers()) {
            result = ConnectionRequestEvent.Result.SERVER_FULL;
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
            default:
                action = Action.CONTINUE;
        }
        return action;
    }

    @Override
    public Advertisement onQuery(InetSocketAddress inetSocketAddress) {
        NukkitConfiguration config = server.getConfiguration();
        return new Advertisement(
                "MCPE",
                config.getGeneral().getMotd(),
                BedrockPacketCodec.BROADCAST_PROTOCOL_VERSION,
                NukkitServer.MINECRAFT_VERSION.toString(),
                server.getSessionManager().playerSessionCount(),
                config.getGeneral().getMaximumPlayers(),
                config.getGeneral().getSubMotd(),
                config.getMechanics().getDefaultGamemode().toString()
        );
    }
}
