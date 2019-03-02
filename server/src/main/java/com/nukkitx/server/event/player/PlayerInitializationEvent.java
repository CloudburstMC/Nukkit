package com.nukkitx.server.event.player;

import com.nukkitx.api.event.Event;
import com.nukkitx.protocol.bedrock.session.BedrockSession;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

public class PlayerInitializationEvent implements Event {
    private final BedrockSession<NukkitPlayerSession> bedrockSession;
    private final NukkitLevel level;
    private NukkitPlayerSession playerSession;

    public PlayerInitializationEvent(BedrockSession<NukkitPlayerSession> bedrockSession, NukkitLevel level) {
        this.bedrockSession = bedrockSession;
        this.level = level;
    }

    public BedrockSession getBedrockSession() {
        return bedrockSession;
    }

    public NukkitLevel getLevel() {
        return level;
    }

    public NukkitPlayerSession getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(NukkitPlayerSession playerSession) {
        this.playerSession = playerSession;
    }
}
