package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.form.response.FormResponse;
import cn.nukkit.api.form.window.FormWindow;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerSettingsRespondedEvent extends PlayerEvent implements Cancellable {

    protected final int formID;
    protected final FormWindow window;
    protected final boolean closed = false;
    private boolean cancelled;

    public PlayerSettingsRespondedEvent(Player player, int formID, FormWindow window) {
        super(player);
        this.formID = formID;
        this.window = window;
    }

    /**
     * Can be null if player closed the window instead of submitting it
     */
    public FormResponse getResponse() {
        return window.getResponse();
    }

    /**
     * Defines if player closed the window or submitted it
     */
    public boolean isClosed() {
        return window.wasClosed();
    }
}
