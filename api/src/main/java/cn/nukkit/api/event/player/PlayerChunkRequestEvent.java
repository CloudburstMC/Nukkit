package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerChunkRequestEvent extends PlayerEvent implements Cancellable {

    private final int chunkX;
    private final int chunkZ;
    @Setter
    private boolean cancelled;

    public PlayerChunkRequestEvent(Player player, int chunkX, int chunkZ) {
        super(player);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }
}
