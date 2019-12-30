package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * Created by lion on 21.03.17.
 */
public class ItemCarrotOnAStick extends ItemTool {

    public ItemCarrotOnAStick(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}

