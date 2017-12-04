package cn.nukkit.server.inventory.transaction;

import cn.nukkit.server.inventory.Inventory;
import cn.nukkit.server.inventory.transaction.action.InventoryAction;

import java.util.Set;

/**
 * @author CreeperFace
 */
public interface InventoryTransaction {

    long getCreationTime();

    Set<InventoryAction> getActions();

    Set<Inventory> getInventories();

    void addAction(InventoryAction action);

    boolean canExecute();

    boolean execute();

    boolean hasExecuted();
}
