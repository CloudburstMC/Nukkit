package cn.nukkit.api.event.player;

import cn.nukkit.api.GameMode;
import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.server.AdventureSettings;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerGameModeChangeEvent extends PlayerEvent implements Cancellable {

    protected final GameMode newGamemode;
    protected AdventureSettings newAdventureSettings;
    private boolean cancelled;

    public PlayerGameModeChangeEvent(Player player, GameMode newGameMode, AdventureSettings newAdventureSettings) {
        super(player);
        this.newGamemode = newGameMode;
        this.newAdventureSettings = newAdventureSettings;
    }
}
