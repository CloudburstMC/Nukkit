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
    private final DamageCause cause;
    private final Player player;

    /**
     * This event is called when an anvil is damaged.
     * @param block The block (anvil) that has been damaged.
     * @param oldDamage Old damage value.
     * @param newDamage New damage value.
     * @param cause Cause of the anvil being damaged.
     * @param player The player who used the anvil.
     */
    public AnvilDamageEvent(Block block, int oldDamage, int newDamage, DamageCause cause, Player player) {
        super(block);
        this.oldDamage = oldDamage;
        this.newDamage = newDamage;
        this.cause = cause;
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

    public DamageCause getCause() {
        return this.cause;
    }

    public Player getPlayer() {
        return this.player;
    }

    public enum DamageCause {
        USE,
        FALL
    }
}
