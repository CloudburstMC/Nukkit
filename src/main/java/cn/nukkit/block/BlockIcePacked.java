package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockIcePacked extends BlockIce {

    public BlockIcePacked() {
        this(0);
    }

    public BlockIcePacked(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return PACKED_ICE;
    }

    @Override
    public String getName() {
        return "Packed Ice";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int onUpdate(int type) {
        return 0; //not being melted
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
