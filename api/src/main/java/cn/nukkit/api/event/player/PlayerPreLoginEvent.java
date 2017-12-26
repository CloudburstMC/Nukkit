package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

/**
 * Called when the player logs in, before things have been set up
 */

@Getter
@Setter
public class PlayerPreLoginEvent extends PlayerEvent implements Cancellable {

    protected String kickMessage;

    private boolean cancelled;

    public PlayerPreLoginEvent(Player player, String kickMessage) {
        super(player);
        this.kickMessage = kickMessage;
    }
}
