package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemShears extends ItemTool {

    public ItemShears(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_SHEARS;
    }

    @Override
    public boolean isShears() {
        return true;
    }
}
