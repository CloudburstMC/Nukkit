/*package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import cn.nukkit.server.form.response.FormResponse;
import cn.nukkit.server.form.window.FormWindow;

public class PlayerFormRespondedEvent implements PlayerEvent {
    private final Player player;
    private final int formID;
    private final FormWindow window;

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
     *//*
    public FormResponse getResponse() {
        return window.getResponse();
    }

    /**
     * Defines if player closed the window or submitted it
     *//*
    public boolean wasClosed() {
        return window.wasClosed();
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}*/
