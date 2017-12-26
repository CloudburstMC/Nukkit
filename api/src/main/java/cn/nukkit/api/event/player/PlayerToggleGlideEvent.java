package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerToggleGlideEvent extends PlayerEvent implements Cancellable {

    protected final boolean isGliding;
    private boolean cancelled;

    public PlayerToggleGlideEvent(Player player, boolean isSneaking) {
        super(player);
        this.isGliding = isSneaking;
    }
}
