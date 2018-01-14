package cn.nukkit.server.event.player;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.level.Level;
import cn.nukkit.server.network.minecraft.session.MinecraftSession;
import cn.nukkit.server.network.minecraft.session.PlayerSession;

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
