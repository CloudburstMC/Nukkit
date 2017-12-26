package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.form.window.FormWindow;
import lombok.Getter;
import lombok.Setter;

/**
 * @author CreeperFace
 */

@Getter
@Setter
public class PlayerServerSettingsRequestEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;

    private FormWindow settings;

    public PlayerServerSettingsRequestEvent(Player player, FormWindow settings) {
        super(player);
        this.settings = settings;
    }
}
