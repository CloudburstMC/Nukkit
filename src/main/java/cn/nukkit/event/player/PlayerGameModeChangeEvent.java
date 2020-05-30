package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.GameMode;
import cn.nukkit.player.Player;

public class PlayerGameModeChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final GameMode gamemode;

    public PlayerGameModeChangeEvent(Player player, GameMode newGameMode) {
        super(player);
        this.gamemode = newGameMode;
    }

    public GameMode getNewGamemode() {
        return gamemode;
    }
}
