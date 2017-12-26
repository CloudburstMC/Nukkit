package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerToggleSprintEvent extends PlayerEvent implements Cancellable {

    protected final boolean isSprinting;
    private boolean cancelled;

    public PlayerToggleSprintEvent(Player player, boolean isSprinting) {
        super(player);
        this.isSprinting = isSprinting;
    }
}