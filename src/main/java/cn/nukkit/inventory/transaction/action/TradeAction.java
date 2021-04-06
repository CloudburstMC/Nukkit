package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

public class TradeAction extends InventoryAction {

	public TradeAction(Item sourceItem, Item targetItem) {
		super(sourceItem, targetItem);
	}

	@Override
	public boolean isValid(Player source) {
		return true;
	}

	@Override
	public boolean execute(Player source) {
		return source.getInventory().contains(this.sourceItem);
	}

	@Override
	public void onExecuteSuccess(Player source) {

	}

	@Override
	public void onExecuteFail(Player source) {

	}

}
