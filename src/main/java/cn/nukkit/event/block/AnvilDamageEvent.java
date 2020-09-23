package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class AnvilDamageEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int oldDamage;
    private int newDamage;
    private final Player player;

    public AnvilDamageEvent(Block block, int oldDamage, int newDamage, Player player) {
        super(block);
        this.oldDamage = oldDamage;
        this.newDamage = newDamage;
        this.player = player;
    }

    public int getOldDamage() {
        return this.oldDamage;
    }

    public int getNewDamage() {
        return this.newDamage;
    }

    public void setNewDamage(int newDamage) {
        this.newDamage = newDamage;
    }

    public Player getPlayer() {
        return this.player;
    }
}
