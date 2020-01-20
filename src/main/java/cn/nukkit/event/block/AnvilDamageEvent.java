package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.transaction.CraftingTransaction;

public class AnvilDamageEvent extends BlockFadeEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlers() {
        return handlers;
    }
    
    private final Player player;
    private final CraftingTransaction transaction;
    private final Cause cause;
    
    public AnvilDamageEvent(Block block, Block newState, Player player, CraftingTransaction transaction, Cause cause) {
        super(block, newState);
        this.player = player;
        this.transaction = transaction;
        this.cause = cause;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public CraftingTransaction getTransaction() {
        return transaction;
    }
    
    public Cause getCause() {
        return cause;
    }
    
    public enum Cause {
        USE,
        IMPACT
    }
    
}
