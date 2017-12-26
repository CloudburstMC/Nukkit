package cn.nukkit.api.event.player;

import cn.nukkit.api.Location;
import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMoveEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;

    private Location from;
    private Location to;

    private boolean resetBlocksAround;

    public PlayerMoveEvent(Player player, Location from, Location to) {
        this(player, from, to, true);
    }

    public PlayerMoveEvent(Player player, Location from, Location to, boolean resetBlocks) {
        super(player);
        this.from = from;
        this.to = to;
        this.resetBlocksAround = resetBlocks;
    }
}
