package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerToggleSneakEvent extends PlayerEvent implements Cancellable {

    protected final boolean isSneaking;
    private boolean cancelled;

    public PlayerToggleSneakEvent(Player player, boolean isSneaking) {
        super(player);
        this.isSneaking = isSneaking;
    }
}
