package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

public class PlayerCommandPreprocessEvent extends PlayerMessageEvent implements Cancellable {

    @Getter
    @Setter
    private boolean cancelled;

    public PlayerCommandPreprocessEvent(final Player player, String message) {
        super(player, message);
        this.message = message;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
