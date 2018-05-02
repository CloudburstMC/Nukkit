/*package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import cn.nukkit.server.form.response.FormResponse;
import cn.nukkit.server.form.window.FormWindow;
import lombok.Getter;
import lombok.Setter;

public class PlayerSettingsRespondedEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final int formID;
    private final FormWindow window;
    private final boolean closed = false;
    private boolean cancelled;

    public PlayerSettingsRespondedEvent(Player player, int formID, FormWindow window) {
        this.player = player;
        this.formID = formID;
        this.window = window;
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
    public boolean isClosed() {
        return window.wasClosed();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public FormWindow getWindow() {
        return window;
    }

    public int getFormID() {
        return formID;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}*/
