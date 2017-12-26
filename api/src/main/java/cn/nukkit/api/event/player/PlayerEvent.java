package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Event;
import lombok.Getter;

@Getter
public abstract class PlayerEvent implements Event {

    protected Player player;

    public PlayerEvent(final Player player) {
        this.player = player;
    }
}
