package com.nukkitx.server.event.player;

import com.nukkitx.api.event.Event;
import com.nukkitx.api.level.Level;
import com.nukkitx.protocol.bedrock.session.BedrockSession;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

public class PlayerInitializationEvent implements Event {
    private final BedrockSession<NukkitPlayerSession> bedrockSession;
    private final Level level;
    private NukkitPlayerSession playerSession;

    public PlayerInitializationEvent(BedrockSession<NukkitPlayerSession> bedrockSession, Level level) {
        this.bedrockSession = bedrockSession;
        this.level = level;
    }

    public BedrockSession getBedrockSession() {
        return bedrockSession;
    }

    public Level getLevel() {
        return level;
    }

    public NukkitPlayerSession getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(NukkitPlayerSession playerSession) {
        this.playerSession = playerSession;
    }
}
