package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

/**
 * call when a player moves wrongly
 *
 * @author WilliamGao
 */

@Getter
@Setter
public class PlayerInvalidMoveEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;

    public PlayerInvalidMoveEvent(Player player) {
        super(player);
    }
}
