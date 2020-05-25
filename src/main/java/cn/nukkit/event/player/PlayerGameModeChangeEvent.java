package cn.nukkit.event.player;

import cn.nukkit.AdventureSettings;
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

    protected AdventureSettings newAdventureSettings;

    public PlayerGameModeChangeEvent(Player player, GameMode newGameMode, AdventureSettings newAdventureSettings) {
        super(player);
        this.gamemode = newGameMode;
        this.newAdventureSettings = newAdventureSettings;
    }

    public GameMode getNewGamemode() {
        return gamemode;
    }

    public AdventureSettings getNewAdventureSettings() {
        return newAdventureSettings;
    }

    public void setNewAdventureSettings(AdventureSettings newAdventureSettings) {
        this.newAdventureSettings = newAdventureSettings;
    }
}
