package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;
import com.nukkitx.api.event.entity.EntityDeathEvent;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.message.ChatMessage;
import com.nukkitx.api.message.Message;

public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {
    private boolean cancelled;
    private Message deathMessage;
    private boolean keepInventory = false;
    private boolean keepExperience = false;
    private int experience;

    public PlayerDeathEvent(Player player, ItemInstance[] drops, Message deathMessage, int experience) {
        super(player, drops);

        this.deathMessage = deathMessage;
        this.experience = experience;
    }

    public PlayerDeathEvent(Player player, ItemInstance[] drops, String deathMessage, int experience) {
        this(player, drops, new ChatMessage(deathMessage), experience);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
