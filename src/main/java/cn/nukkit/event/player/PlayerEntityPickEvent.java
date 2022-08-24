package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class PlayerEntityPickEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity targetEntity;
    private Item item;

    public PlayerEntityPickEvent(Player player, Entity targetEntity, Item item) {
        this.targetEntity = targetEntity;
        this.item = item;
        this.player = player;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
