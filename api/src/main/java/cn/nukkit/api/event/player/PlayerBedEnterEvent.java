package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerBedEnterEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private final Block bed;

    public PlayerBedEnterEvent(Player player, Block bed) {
        super(player);
        this.bed = bed;
    }
}
