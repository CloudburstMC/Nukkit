/*package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.server.form.window.FormWindow;

public class PlayerServerSettingsRequestEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private FormWindow settings;
    private boolean cancelled;

    public PlayerServerSettingsRequestEvent(Player player, FormWindow settings) {
        this.player = player;
        this.settings = settings;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public FormWindow getSettings() {
        return settings;
    }

    public void setSettings(FormWindow settings) {
        this.settings = settings;
    }
}*/
