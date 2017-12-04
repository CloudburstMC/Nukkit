package cn.nukkit.server.event.player;

import cn.nukkit.server.Player;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.form.response.FormResponse;
import cn.nukkit.server.form.window.FormWindow;

public class PlayerFormRespondedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected int formID;
    protected FormWindow window;

    protected boolean closed = false;

    public PlayerFormRespondedEvent(Player player, int formID, FormWindow window) {
        this.player = player;
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
