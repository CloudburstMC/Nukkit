package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public class BlockButtonStone extends BlockButton {

    public BlockButtonStone(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Stone Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}
