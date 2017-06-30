package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockPackedIce extends BlockIce {

    public BlockPackedIce() {
        this(0);
    }

    public BlockPackedIce(int meta) {
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
}
