package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 27. 11. 2016.
 */
public class BlockButtonWooden extends BlockButton {

    public BlockButtonWooden(){
        this(0);
    }

    public BlockButtonWooden(int meta){
        super(meta);
    }

    @Override
    public int getId() {
        return WOODEN_BUTTON;
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
