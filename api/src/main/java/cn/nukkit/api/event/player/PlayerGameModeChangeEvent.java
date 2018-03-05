package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.util.GameMode;

public class PlayerGameModeChangeEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final GameMode newGamemode;
    private boolean cancelled;

    public PlayerGameModeChangeEvent(Player player, GameMode newGameMode) {
        this.player = player;
        this.newGamemode = newGameMode;
    }

    public GameMode getGameMode() {
        return newGamemode;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
