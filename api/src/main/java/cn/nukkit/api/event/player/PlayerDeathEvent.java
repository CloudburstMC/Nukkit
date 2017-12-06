package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.entity.EntityDeathEvent;
import cn.nukkit.api.message.Message;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;

public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Message deathMessage;
    private boolean keepInventory = false;
    private boolean keepExperience = false;
    private int experience;

    public PlayerDeathEvent(Player player, Item[] drops, Message deathMessage, int experience) {
        super(player, drops);
        this.deathMessage = deathMessage;
        this.experience = experience;
    }

    public PlayerDeathEvent(Player player, Item[] drops, String deathMessage, int experience) {
        this(player, drops, new Message(deathMessage), experience);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    public Message getDeathMessage() {
        return deathMessage;
    }

    public void setDeathMessage(Message deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = new Message(deathMessage);
    }

    public boolean getKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean getKeepExperience() {
        return keepExperience;
    }

    public void setKeepExperience(boolean keepExperience) {
        this.keepExperience = keepExperience;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
