package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemShield extends ItemTool {

    public ItemShield(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 337;
    }
}
