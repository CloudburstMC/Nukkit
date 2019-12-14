package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public class BlockButtonWooden extends BlockButton {

    public BlockButtonWooden(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Wooden Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
