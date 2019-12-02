package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;

public class PlayerFormRespondedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    protected int formID;
    protected FormWindow window;
    protected boolean closed = false;

    public PlayerFormRespondedEvent(Player player, int formID, FormWindow window) {
        this.player = player;
        this.formID = formID;
        this.window = window;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getFormID() {
        return this.formID;
    }

    public FormWindow getWindow() {
        return window;
    }

    /**
     * Can be null if player closed the window instead of submitting it
     *
     * @return response
     */
    public FormResponse getResponse() {
        return window.getResponse();
    }

    /**
     * Defines if player closed the window or submitted it
     *
     * @return form closed
     */
    public boolean wasClosed() {
        return window.wasClosed();
    }

}
