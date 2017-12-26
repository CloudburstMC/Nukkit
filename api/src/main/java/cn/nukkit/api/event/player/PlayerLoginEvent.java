package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerLoginEvent extends PlayerEvent implements Cancellable {

    protected String kickMessage;
    private boolean cancelled;

    public PlayerLoginEvent(Player player, String kickMessage) {
        super(player);
        this.kickMessage = kickMessage;
    }
}
