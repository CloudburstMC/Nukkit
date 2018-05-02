package com.nukkitx.server.event.player;

import com.nukkitx.api.event.Event;
import com.nukkitx.api.level.Level;
import com.nukkitx.server.network.minecraft.session.MinecraftSession;
import com.nukkitx.server.network.minecraft.session.PlayerSession;

public class PlayerInitializationEvent implements Event {
    private final MinecraftSession minecraftSession;
    private final Level level;
    private PlayerSession playerSession;

    public PlayerInitializationEvent(MinecraftSession minecraftSession, Level level) {
        this.minecraftSession = minecraftSession;
        this.level = level;
    }

    public MinecraftSession getMinecraftSession() {
        return minecraftSession;
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
