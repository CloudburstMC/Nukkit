package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Created on 2015/12/23 by xtypr.
 * Package cn.nukkit.server.event.player in project Nukkit .
 */

@Getter
@Setter
public abstract class PlayerMessageEvent extends PlayerEvent {

    protected String message;

    protected PlayerMessageEvent(final Player player, final String message) {
        super(player);
        this.message = message;
    }
}
