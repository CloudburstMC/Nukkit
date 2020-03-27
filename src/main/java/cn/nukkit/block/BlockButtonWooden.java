package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public class BlockButtonWooden extends BlockButton {

    public BlockButtonWooden(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
