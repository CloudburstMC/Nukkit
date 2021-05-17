package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * @author CreeperFace
 */
@ToString(callSuper = true)
public class CraftingTakeResultAction extends InventoryAction {

    public CraftingTakeResultAction(Item sourceItem, Item targetItem) {
        super(sourceItem, targetItem);
    }

    public void onAddToTransaction(InventoryTransaction transaction) {
        if (transaction instanceof CraftingTransaction) {
            ((CraftingTransaction) transaction).setPrimaryOutput(this.getSourceItem());
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
