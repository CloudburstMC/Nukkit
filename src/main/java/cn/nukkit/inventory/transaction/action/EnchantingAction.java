package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.Item;
import lombok.Getter;

public class EnchantingAction extends InventoryAction {

    @Getter
    private final int type;

    public EnchantingAction(Item source, Item target, int type) {
        super(source, target);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.ENCHANT_WINDOW_ID) instanceof EnchantInventory;
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {
    }

    @Override
    public void onExecuteFail(Player source) {

    }
}
