package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.TextContainer;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;

public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TextContainer deathMessage;
    private boolean keepInventory = false;
    private boolean keepExperience = false;
    private int experience;

    public PlayerDeathEvent(Player player, Item[] drops, TextContainer deathMessage, int experience) {
        super(player, drops);
        this.deathMessage = deathMessage;
        this.experience = experience;
    }

    public PlayerDeathEvent(Player player, Item[] drops, String deathMessage, int experience) {
        this(player, drops, new TextContainer(deathMessage), experience);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    public TextContainer getDeathMessage() {
        return deathMessage;
    }

    public void setDeathMessage(TextContainer deathMessage) {
        this.deathMessage = deathMessage;
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
