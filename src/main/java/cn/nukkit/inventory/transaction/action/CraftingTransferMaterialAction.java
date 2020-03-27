package cn.nukkit.inventory.transaction.action;

import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

/**
 * @author CreeperFace
 */
public class CraftingTransferMaterialAction extends InventoryAction {

    private int slot;

    public CraftingTransferMaterialAction(Item sourceItem, Item targetItem, int slot) {
        super(sourceItem, targetItem);

        this.slot = slot;
    }

    @Override
    public void onAddToTransaction(InventoryTransaction transaction) {
        if (transaction instanceof CraftingTransaction) {
            if (this.sourceItem.isNull()) {
                ((CraftingTransaction) transaction).setInput(this.slot, this.targetItem);
            } else if (this.targetItem.isNull()) {
                ((CraftingTransaction) transaction).setExtraOutput(this.slot, this.sourceItem);
            } else {
                throw new RuntimeException("Invalid " + getClass().getName() + ", either source or target item must be air, got source: " + this.sourceItem + ", target: " + this.targetItem);
            }
        } else {
            throw new RuntimeException(getClass().getName() + " can only be added to CraftingTransactions");
        }
    }

    @Override
    public boolean isValid(Player source) {
        return true;
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player $source) {

    }

    @Override
    public void onExecuteFail(Player source) {

    }
}
