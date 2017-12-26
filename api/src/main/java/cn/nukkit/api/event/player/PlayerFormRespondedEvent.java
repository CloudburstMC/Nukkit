package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.form.response.FormResponse;
import cn.nukkit.api.form.window.FormWindow;

public class PlayerFormRespondedEvent extends PlayerEvent {

    protected final int formID;
    protected final FormWindow window;

    protected final boolean closed = false;

    public PlayerFormRespondedEvent(Player player, int formID, FormWindow window) {
        super(player);
        this.formID = formID;
        this.window = window;
    }

    public int getFormID() {
        return this.formID;
    }

    public FormWindow getWindow() {
        return window;
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
    public boolean wasClosed() {
        return window.wasClosed();
    }

}
