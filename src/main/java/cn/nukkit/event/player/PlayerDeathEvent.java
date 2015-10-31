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

    public PlayerDeathEvent(Player player, Item[] drops, TextContainer deathMessage) {
        super(player, drops);
        this.deathMessage = deathMessage;
    }

    public PlayerDeathEvent(Player player, Item[] drops, String deathMessage) {
        this(player, drops, new TextContainer(deathMessage));
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
}
