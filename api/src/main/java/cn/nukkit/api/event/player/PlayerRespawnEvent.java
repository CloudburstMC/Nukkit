package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerRespawnEvent extends PlayerEvent {

    private Location respawnLocation;

    public PlayerRespawnEvent(Player player, Location respawnLocation) {
        super(player);
        this.respawnLocation = respawnLocation;
    }

    public enum Cause {
        DEATH,
        JOIN
    }
}
