package com.nukkitx.server.event.player;

import com.google.common.base.Preconditions;
import com.nukkitx.api.event.Event;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.bedrock.session.LoginSession;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

import java.util.function.BiFunction;

public class PlayerInitializationEvent implements Event {
    private BiFunction<LoginSession, NukkitLevel, PlayerSession> playerCreator;

    public PlayerInitializationEvent(BiFunction<LoginSession, NukkitLevel, PlayerSession> playerCreator) {
        this.playerCreator = playerCreator;
    }

    public BiFunction<LoginSession, NukkitLevel, PlayerSession> getPlayerCreator() {
        return playerCreator;
    }

    public void setPlayerCreator(BiFunction<LoginSession, NukkitLevel, PlayerSession> playerCreator) {
        Preconditions.checkNotNull(playerCreator, "playerCreator");
        this.playerCreator = playerCreator;
    }
}
