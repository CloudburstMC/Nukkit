package com.nukkitx.server.event.player;

import com.nukkitx.api.event.Event;
import com.nukkitx.api.level.Level;
import com.nukkitx.server.network.bedrock.session.BedrockSession;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

public class PlayerInitializationEvent implements Event {
    private final BedrockSession bedrockSession;
    private final Level level;
    private PlayerSession playerSession;

    public PlayerInitializationEvent(BedrockSession bedrockSession, Level level) {
        this.bedrockSession = bedrockSession;
        this.level = level;
    }

    public BedrockSession getBedrockSession() {
        return bedrockSession;
    }

    public Level getLevel() {
        return level;
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(PlayerSession playerSession) {
        this.playerSession = playerSession;
    }
}
